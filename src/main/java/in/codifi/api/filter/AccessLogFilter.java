package in.codifi.api.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.cache.HazleCacheController;
import in.codifi.api.entity.logs.AccessLogModel;
import in.codifi.api.repository.AccessLogManager;
import in.codifi.api.repository.AccesslogRepository;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.StringUtil;
import io.quarkus.arc.Priority;
import io.vertx.core.http.HttpServerRequest;

@Provider
@Priority(Priorities.USER)
public class AccessLogFilter implements ContainerRequestFilter, ContainerResponseFilter {
	@Inject
	HttpServerRequest serverRequest;
	@Inject
	AccesslogRepository logRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	AccessLogManager accessLogManager;
	private static final List<String> disablepaths = Arrays.asList(EkycConstants.PAGE_POSITION,
			EkycConstants.PAGE_CLOSUREUPLOAD, EkycConstants.PAGE_CLOSURELOG, EkycConstants.PAGE_DP);

	/**
	 * Method to capture and single save request and response
	 * 
	 * @param requestContext
	 * @param responseContext
	 */
	@SuppressWarnings("unused")
	private void caputureInSingleShot(ContainerRequestContext requestContext,
			ContainerResponseContext responseContext) {
		String deviceIp = serverRequest.remoteAddress().toString();
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				try {
					ObjectMapper objMapper = new ObjectMapper();
					AccessLogModel accessLogModel = new AccessLogModel();
					UriInfo uriInfo = requestContext.getUriInfo();
					MultivaluedMap<String, String> headers = requestContext.getHeaders();
					accessLogModel.setContentType(headers.getFirst(EkycConstants.CONSTANT_CONTENT_TYPE));
					accessLogModel.setDeviceIp(headers.getFirst("X-Forwarded-For"));
					accessLogModel.setInTime((Timestamp) requestContext.getProperty("inTime"));
					accessLogModel.setOutTime(new Timestamp(System.currentTimeMillis()));
					accessLogModel.setDomain(headers.getFirst("Host"));
					accessLogModel.setMethod(requestContext.getMethod());
					String queryParams = uriInfo.getRequestUri().getQuery();
					if (StringUtil.isNotNullOrEmpty(queryParams)) {
						accessLogModel.setReqBody(queryParams);
						MultivaluedMap<String, String> queryParameters = uriInfo.getQueryParameters();
						String applicationId = findApplicationId(queryParameters);
						accessLogModel.setApplicationId(applicationId);
					} else {
						String requestBody = (String) requestContext.getProperty(EkycConstants.CONST_REQ_BODY);
						accessLogModel.setReqBody(requestBody);
						try {
							JsonNode jsonNode = objMapper.readTree(requestBody);
							String sid = jsonNode.path("sid").asText(null);
							String applicationId = jsonNode.path("applicationId").asText(null);
							String mobileNo = jsonNode.path("mobileNo").asText(null);
							if (sid != null) {
								accessLogModel.setApplicationId(sid);
							} else if (applicationId != null) {
								accessLogModel.setApplicationId(applicationId);
							} else if (mobileNo != null) {
								accessLogModel.setApplicationId(mobileNo);
							}
						} catch (JsonProcessingException e) {
							System.out.println("Error parsing JSON: " + e.getMessage());
						}
					}
					Object reponseObj = responseContext.getEntity();
					accessLogModel.setResBody(objMapper.writeValueAsString(reponseObj));
					accessLogModel.setUri(uriInfo.getPath().toString());
					accessLogModel.setUserAgent(headers.getFirst(EkycConstants.USER_AGENT));
					accessLogModel.setReqId(requestContext.getProperty("threadId") != null
							? requestContext.getProperty("threadId").toString()
							: "EKYC");
					Long thredId = Thread.currentThread().getId();
					accessLogManager.insertAccessLogsIntoDB(accessLogModel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		pool.shutdown();

	}

	private String findApplicationId(MultivaluedMap<String, String> queryParameters) {
		String[] possibleKeys = { "applicationId", "sid", "id" }; // Add other variations as needed

		for (String key : possibleKeys) {
			if (queryParameters.containsKey(key)) {
				return queryParameters.getFirst(key);
			}
		}

		return null; // Or handle the case when applicationId is not found
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		caputureInSingleShot(requestContext, responseContext);
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		try {
			requestContext.setProperty(EkycConstants.CONST_IN_TIME, new Timestamp(System.currentTimeMillis()));
			byte[] body = requestContext.getEntityStream().readAllBytes();
			InputStream stream = new ByteArrayInputStream(body);
			requestContext.setEntityStream(stream);
			String formedReq = new String(body);
			requestContext.setProperty(EkycConstants.CONST_REQ_BODY, formedReq);
			String path = requestContext.getUriInfo().getPath();
			boolean visible = disablepaths.stream().anyMatch(visibleType -> visibleType.contains(path));
			if (!visible && StringUtil.isEqual(
					HazleCacheController.getInstance().getKraKeyValue().get(EkycConstants.CONST_FILTER),
					EkycConstants.TRUE)) {
				String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
				if (StringUtil.isNullOrEmpty(authorizationHeader)) {
					requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
				} else {
					validateToken(requestContext, authorizationHeader);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Method to validate the authorization token
	 * 
	 * @author prade
	 * 
	 * @param requestContext
	 * @param authorizationHeader
	 */
	public void validateToken(ContainerRequestContext requestContext, String authorizationHeader) {
		String fullToken = authorizationHeader.substring("Bearer".length()).trim();
		if (StringUtil.isNotNullOrEmpty(fullToken)) {
			String token[] = fullToken.split(" ");
			if (StringUtil.isNotNullOrEmpty(token[0]) && StringUtil.isNotNullOrEmpty(token[1])) {
				String mobileNumber = commonMethods.decrypt(token[1]);
				if (StringUtil.isNotNullOrEmpty(mobileNumber)
						&& HazleCacheController.getInstance().getAuthToken().containsKey(mobileNumber)
						&& StringUtil.isEqual(HazleCacheController.getInstance().getAuthToken().get(mobileNumber),
								fullToken)) {
					UriInfo uriInfo = requestContext.getUriInfo();
					String queryParams = uriInfo.getRequestUri().getQuery();
					if (StringUtil.isNotNullOrEmpty(queryParams)) {
						String applciationId = getApplicationIdFromQueryParam(queryParams);
						String[] mobileAppId = mobileNumber.split("_");
						if (!applciationId.equals(mobileAppId[1])) {
							requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
						}
					}
					HazleCacheController.getInstance().getAuthToken().put(mobileNumber, fullToken, 300,
							TimeUnit.SECONDS);
				} else {
					requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
				}
			} else {
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			}
		} else {
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}
	}

	/**
	 * Method to get application Id from query param
	 * 
	 * @param queryParams
	 * @return
	 */
//	private long getApplicationIdFromQueryParam(String queryParams) {
//		long applicationID = 0l;
//		String[] keyValue = queryParams.split("&");
//		for (int i = 0; i < keyValue.length; i++) {
//			String query = keyValue[i];
//			if (query.contains("applicationId")) {
//				String[] appIdValue = query.split("=");
//				if (StringUtil.isNotNullOrEmpty(appIdValue[1]) && isNumeric(appIdValue[1])) {
//					applicationID = Long.parseLong(appIdValue[1]);
//				}
//			}
//		}
//		return applicationID;
//	}

	private String getApplicationIdFromQueryParam(String queryParams) {
		String applicationID = null;
		String[] keyValue = queryParams.split("&");
		for (int i = 0; i < keyValue.length; i++) {
			String query = keyValue[i];
			if (query.contains("applicationId")) {
				String[] appIdValue = query.split("=");
				if (StringUtil.isNotNullOrEmpty(appIdValue[1]) && isCorrectFormat(appIdValue[1])) {
					applicationID = appIdValue[1];
				}
			}
		}
		return applicationID;
	}

	private boolean isCorrectFormat(String applicationID) {
		String[] parts = applicationID.split("-");
		if (parts.length == 4) {
			if (parts[0].equals("CRM") && parts[1].equals("OPP")) {
				try {
//	                int year = Integer.parseInt(parts[2]);
//	                int sequence = Integer.parseInt(parts[3]);
					return true;
				} catch (NumberFormatException e) {
					// One of the parts is not a number
				}
			}
		}
		return false;
	}

	/**
	 * check string is muneric
	 * 
	 * @author prade
	 * @param strNum
	 * @return
	 */
	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
}
