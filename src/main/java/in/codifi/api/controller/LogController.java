package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.ILogController;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.restservice.SmsRestService;
import in.codifi.api.service.spec.ILogService;

@Path("/logs")
public class LogController implements ILogController {
	@Inject
	ILogService logService;
	@Inject
	SmsRestService smsRestService;
	
	/*
	 * method to check the rest access log table if exist or not
	 */
	@Override
	public ResponseModel CreateLogTable() {
		ResponseModel responseModel = new ResponseModel();
		responseModel=logService.checkRestAccessLogTable();
		return responseModel;
	}

	/*
	 * method to check the rest service access log table if exist or not
	 */
	@Override
	public ResponseModel CreateRestLogTable() {
		ResponseModel responseModel = new ResponseModel();
		responseModel=logService.checkRestServiceAccessLogTable();
		return responseModel;
	}
	
	@Override
	public ResponseModel completesms(String userID) {
		ResponseModel responseModel = new ResponseModel();
		smsRestService.SendCompletedSms(userID);
		return responseModel;
	}
	
	
	@Override
	public ResponseModel sendOTPtoMobile(int otp,String mobileNo) {
		ResponseModel responseModel = new ResponseModel();
		smsRestService.sendOTPtoMobile(otp,mobileNo);
		return responseModel;
	}

	@Override
	public ResponseModel sendEsignSms(String userID, String dpID, String mobileNO) {
		ResponseModel responseModel = new ResponseModel();
		smsRestService.sendEsignSms(userID,dpID,mobileNO);
		return responseModel;
	}
	
}
