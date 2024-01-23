package in.codifi.api.trading.restservice;

import java.io.IOException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.codifi.api.entity.FundsDetails;
import in.codifi.api.model.ClientBasicData;
import in.codifi.api.model.DpModel;
import in.codifi.api.model.DpResult;
import in.codifi.api.model.JsonResponse;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;

@ApplicationScoped
public class tradingRestServices {

	private static final Logger logger = LogManager.getLogger(tradingRestServices.class);
	@Inject
	CommonMethods commonMethods;
	@Inject
	@RestClient
	ItradingRestServices itradingRestServices;

	public boolean getFunds(String authToken) {
		try {
			Response fundsResult = itradingRestServices.getFunds(authToken, "");

			if (fundsResult.getStatus() == 200) {
				String responseBody = fundsResult.readEntity(String.class);
				System.out.println("Response Body: " + responseBody);

				JSONObject jsonResponse = new JSONObject(responseBody);

				if (jsonResponse.has("result")) {
					JSONArray resultArray = jsonResponse.getJSONArray("result");

					if (resultArray.length() > 0) {
						JSONObject resultObject = resultArray.getJSONObject(0);
						FundsDetails fundsDetails = createFundsDetailsFromJson(resultObject);

						// Check if all fields are exactly 0
						boolean allFieldsZero = fundsDetails.isAllFieldsZero();

						if (allFieldsZero) {
							System.out.println(" Not all fields in FundsDetails are 0.");
						} else {
							System.out.println("All fields in FundsDetails are 0.");
						}
						return allFieldsZero;
					}
				} else {
					System.err.println("JSON structure does not contain 'result' field.");
				}
			} else {
				System.err.println("HTTP error: " + fundsResult.getStatus());
			}
		} catch (JSONException e) {
			handleJsonParsingError(authToken, e);
			logger.error("An error occurred: " + e.getMessage());
			e.printStackTrace();
			commonMethods.SaveLog(authToken, EkycConstants.TRADING_REST_SERVICE, "getFunds", e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.TRADING_REST_SERVICE, "getFunds", e.getMessage(),
					EkycConstants.CLOSURE_ERROR_CODE);
		}
		return false;
	}

	private FundsDetails createFundsDetailsFromJson(JSONObject resultObject) throws JSONException {
		FundsDetails fundsDetails = new FundsDetails();
		fundsDetails.setAvailableMargin(resultObject.getDouble("availableMargin"));
		fundsDetails.setOpeningBalance(resultObject.getDouble("openingBalance"));
		fundsDetails.setMarginUsed(resultObject.getDouble("marginUsed"));
		fundsDetails.setPayin(resultObject.getDouble("payin"));
		fundsDetails.setStockPledge(resultObject.getDouble("stockPledge"));
		fundsDetails.setHoldingSellCredit(resultObject.getDouble("holdingSellCredit"));
		fundsDetails.setExposure(resultObject.getDouble("exposure"));
		fundsDetails.setPremium(resultObject.getDouble("premium"));
		fundsDetails.setBookedPAndL(resultObject.getDouble("bookedPAndL"));
		fundsDetails.setMtmPAndL(resultObject.getDouble("mtmPAndL"));
		fundsDetails.setCollateral(resultObject.getDouble("collateral"));
		fundsDetails.setFundsTranstoday(resultObject.getDouble("fundsTranstoday"));
		fundsDetails.setCreditForSale(resultObject.getDouble("creditForSale"));
		fundsDetails.setTotalUtilize(resultObject.getDouble("totalUtilize"));
		fundsDetails.setAllocationOrWithdrawal(resultObject.getDouble("allocationOrWithdrawal"));
		fundsDetails.setNetAvailableFunds(resultObject.getDouble("netAvailableFunds"));
		return fundsDetails;
	}

	private void handleJsonParsingError(String authToken, JSONException e) {
		System.err.println("JSON parsing error: " + e.getMessage());
		logger.error("An error occurred: " + e.getMessage());
		e.printStackTrace();
		commonMethods.SaveLog(authToken, EkycConstants.TRADING_REST_SERVICE, "createFundsDetailsFromJson",
				e.getMessage());
		commonMethods.sendErrorMail(EkycConstants.TRADING_REST_SERVICE, "createFundsDetailsFromJson", e.getMessage(),
				EkycConstants.CLOSURE_ERROR_CODE);
	}

	public boolean getHoldings(String authToken) {
		System.out.println("the getHoldings authToken" + authToken);
		Response holdingResult = itradingRestServices.getHoldings(authToken);
		try {
			if (holdingResult.getStatus() == 200) {
				String responseBody = holdingResult.readEntity(String.class);
				System.out.println("Response Body: " + responseBody);

				JSONObject jsonResponse = new JSONObject(responseBody);

				if ("Ok".equals(jsonResponse.getString("status"))) {
					if ("no data".equalsIgnoreCase(jsonResponse.getString("message"))) {
						JSONArray resultArray = jsonResponse.getJSONArray("result");
						if (resultArray.length() == 0) {
							// "status" is "Ok", "message" is "no data", and "result" is empty, return false
							return false;
						} else {
							// "status" is "Ok", "message" is "no data", but "result" is not empty, return
							// true
							return true;
						}
					} else {
						// "status" is "Ok", but "message" is not "no data", return true
						return true;
					}
				} else {
					// Status is not "Ok", return true
					return true;
				}
			} else {
				System.err.println("HTTP error: " + holdingResult.getStatus());
				// HTTP error, return false
				return false;
			}
		} catch (JSONException e) {
			System.err.println("JSON parsing error: " + e.getMessage());
			logger.error("An error occurred: " + e.getMessage());
			e.printStackTrace();
			commonMethods.SaveLog(authToken, EkycConstants.TRADING_REST_SERVICE, "getHoldings", e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.TRADING_REST_SERVICE, "getHoldings", e.getMessage(),
					EkycConstants.CLOSURE_ERROR_CODE);
			return false;
		}
	}

	public boolean getPosition(String authToken) {
		Response PositionResult = itradingRestServices.getPosition(authToken, "");
		try {
			if (PositionResult.getStatus() == 200) {
				String responseBody = PositionResult.readEntity(String.class);
				System.out.println("Response Body: " + responseBody);

				JSONObject jsonResponse = new JSONObject(responseBody);

				if (!"Not ok".equalsIgnoreCase(jsonResponse.getString("status"))) {
					JSONArray resultArray = jsonResponse.getJSONArray("result");
					if (resultArray.length() == 0) {
						return false; // "status" is not "Not ok" and "result" is empty, return false
					} else {
						return true; // "status" is not "Not ok" and "result" is not empty, return true
					}
				} else if ("No data found for Positions".equalsIgnoreCase(jsonResponse.getString("message"))) {
					return false; // "status" is "Not ok" and message is "No data found for Positions", return
									// false
				} else {
					return true; // "status" is "Not ok" but message is not "No data found for Positions", return
									// true
				}
			} else {
				System.err.println("HTTP error: " + PositionResult.getStatus());
				return false;
			}
		} catch (JSONException e) {
			System.err.println("JSON parsing error: " + e.getMessage());
			logger.error("An error occurred: " + e.getMessage());
			e.printStackTrace();
			commonMethods.SaveLog(authToken, EkycConstants.TRADING_REST_SERVICE, "getPosition", e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.TRADING_REST_SERVICE, "getPosition", e.getMessage(),
					EkycConstants.CLOSURE_ERROR_CODE);
			return false;
		}
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public ClientBasicData getUserDetails(String token) {
		String authToken = "Bearer " + token;
		Response userDetails = itradingRestServices.getUserDetails(authToken);
		String responseBody = userDetails.readEntity(String.class);
		System.out.println("Response Body: " + responseBody);
		try {
			JsonResponse jsonResponse = objectMapper.readValue(responseBody, JsonResponse.class);
			if ("Ok".equals(jsonResponse.getStatus()) && jsonResponse.getResult() != null
					&& jsonResponse.getResult().length > 0) {
				return objectMapper.convertValue(jsonResponse.getResult()[0], ClientBasicData.class);
			}
		} catch (IOException e) {
			logger.error("An error occurred: " + e.getMessage());
			e.printStackTrace();
			commonMethods.SaveLog(authToken, EkycConstants.TRADING_REST_SERVICE, "getUserDetails", e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.TRADING_REST_SERVICE, "getUserDetails", e.getMessage(),
					EkycConstants.CLOSURE_ERROR_CODE);
		}
		return null;
	}

	public List<DpResult> getDpDetails(String authToken) {
		Response getDpDetails = itradingRestServices.getDpDetails(authToken);
		DpModel dpModel = getDpDetails.readEntity(DpModel.class);
		List<DpResult> dpResult = dpModel.getResult();
		return dpResult;
	}

}
