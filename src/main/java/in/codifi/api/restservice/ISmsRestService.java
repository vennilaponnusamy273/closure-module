package in.codifi.api.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "config-sms")
@RegisterClientHeaders
public interface ISmsRestService {
	/**
	 * Method to Send SMS OTP
	 * 
	 * @author Sowmiya
	 * @param userId
	 * @param pass
	 * @param appId
	 * @param subAppId
	 * @param contentType
	 * @param mobileNumber
	 * @param from
	 * @param message
	 * @param selfId
	 * @param alert
	 * @param dlrReq
	 * @return
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public String SendSms(@QueryParam("userId") String userId, @QueryParam("pass") String pass,
			@QueryParam("appid") String appId, @QueryParam("subappid") String subAppId,
			@QueryParam("contenttype") String contentType, @QueryParam("to") String mobileNumber,
			@QueryParam("from") String from, @QueryParam("text") String message, @QueryParam("selfid") String selfId,
			@QueryParam("alert") String alert, @QueryParam("dlrreq") String dlrReq);

}
