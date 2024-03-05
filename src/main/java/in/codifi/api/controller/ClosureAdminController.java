package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.controller.spec.IClosureAdminController;
import in.codifi.api.model.LogsRequestModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IClosureAdminService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@Path("/closureAdmin")
public class ClosureAdminController implements IClosureAdminController {

	private static final Logger logger = LogManager.getLogger(ClosureAdminController.class);

	@Inject
	IClosureAdminService IclosureAdminService;
	@Inject
	CommonMethods commonMethods;

	@Override
	public ResponseModel updateClosureStatus(int status, String userId, String rejectedReason) {
		ResponseModel responseModel = new ResponseModel();
		try {
			if (status != 1 && status != 2&&status!=3) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.CLOSURE_STATUS_EXCEPTION);
			} else if (status == 2 && (rejectedReason == null || rejectedReason.isEmpty())) {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.REJECTION_REASON_MANDATORY);
			} else {
				if (userId != null) {
					responseModel = IclosureAdminService.updateClosureStatus(status, userId, rejectedReason);
				} else {
					responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.CLOSUREADMINCONTROLLER,"updateClosureStatus",e.getMessage(),EkycConstants.CLOSURE_ERROR_CODE);
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	@Override
	public ResponseModel resetClosureStatus(String userId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			if (userId != null) {
				responseModel = IclosureAdminService.resetClosureStatus(userId);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			}

		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.CLOSUREADMINCONTROLLER,"resetClosureStatus",e.getMessage(),EkycConstants.CLOSURE_ERROR_CODE);
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	@Override
	public ResponseModel getClosureStatus(String userId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			if (userId != null) {
				responseModel = IclosureAdminService.getClosureStatus(userId);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.CLOSUREADMINCONTROLLER,"getClosureStatus",e.getMessage(),EkycConstants.CLOSURE_ERROR_CODE);
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}
	
	@Override
	public ResponseModel getClosureLogs(LogsRequestModel logsRequestModel) {
		ResponseModel response = new ResponseModel();
		try {
			if (logsRequestModel!=null) {
				response = IclosureAdminService.getClosureLogs(logsRequestModel);
			} else {
				response = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			}
		} catch (Exception e) {
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
		return response;
	}
	
	@Override
	public ResponseModel getStatusCount(LogsRequestModel logsRequestModel) {
		ResponseModel response = new ResponseModel();
		try {
			if (logsRequestModel!=null) {
				response = IclosureAdminService.getStatusCount(logsRequestModel);
			} else {
				response = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			}
		} catch (Exception e) {
			response = commonMethods.constructFailedMsg(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseModel resendConfirmationMail(String userId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			if (userId != null) {
				responseModel = IclosureAdminService.resendConfirmationMail(userId);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.CLOSUREADMINCONTROLLER,"resendConfirmationMail",e.getMessage(),EkycConstants.CLOSURE_ERROR_CODE);
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}
}
