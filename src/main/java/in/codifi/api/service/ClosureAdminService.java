package in.codifi.api.service;


import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.entity.ClosurelogEntity;
import in.codifi.api.helper.ClosureHelper;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ClosurelogRepository;
import in.codifi.api.service.spec.IClosureAdminService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
public class ClosureAdminService implements IClosureAdminService {

	private static final Logger logger = LogManager.getLogger(ClosureAdminService.class);

	@Inject
	CommonMethods commonMethods;
	
	@Inject
	ClosureHelper closureHelper;
	@Inject
	ClosurelogRepository closurelogRepository;

	@Override
	public ResponseModel updateClosureStatus(int status, String userId, String rejectedReason) {
		ResponseModel response = new ResponseModel();

		try {
			ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(userId);

			if (closurelogEntity == null) {
				closurelogEntity = new ClosurelogEntity();
				closurelogEntity.setUserId(userId);
			}

			closurelogEntity.setAdminstatus(status);
			closurelogEntity.setRejectedReason(status == 0 ? rejectedReason : "");

			// Save or update the closure status entity
			closurelogRepository.save(closurelogEntity);

			if (status == 1) {
				sendClosureEmailandSmsOtp(closurelogEntity.getEmail(), closurelogEntity.getMobile());
			}
			response.setMessage(EkycConstants.SUCCESS_MSG);
			response.setStat(EkycConstants.SUCCESS_STATUS);
			Map<String, Object> resultDetails = new HashMap<>();
			resultDetails.put("UserID", closurelogEntity.getUserId());
			resultDetails.put("adminstatus", closurelogEntity.getAdminstatus());
			resultDetails.put("RejectedReason", closurelogEntity.getRejectedReason());
			response.setResult(resultDetails);

		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, EkycConstants.CLOSURE_ADMIN_SERVICE, "updateClosureStatus", e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.CLOSURE_ADMIN_SERVICE, "updateClosureStatus", e.getMessage(),
					EkycConstants.CLOSURE_ERROR_CODE);
			response = commonMethods.constructFailedMsg(e.getMessage());
		}

		return response;
	}

	@Override
	public ResponseModel sendClosureEmailandSmsOtp(String EmailID, String MobileNo) {
		ResponseModel responseModel = new ResponseModel();
		try {
			if (MobileNo != null && EmailID != null) {
				commonMethods.sendClosureMail(EmailID);
				// closureHelper.sendClosureOtp(MobileNo);
				if (responseModel != null) {
					responseModel.setMessage(EkycConstants.SUCCESS_MSG);
					responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				}
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, EkycConstants.CLOSURE_ADMIN_SERVICE, "sendClosureEmailandSmsOtp",
					e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.CLOSURE_ADMIN_SERVICE, "sendClosureEmailandSmsOtp",
					e.getMessage(), EkycConstants.CLOSURE_ERROR_CODE);
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	@Override
	public ResponseModel resetClosureStatus(String userId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(userId);
			if (closurelogEntity != null) {
				closurelogEntity.setAdminstatus(3);
				closurelogEntity.setRejectedReason("");
				closurelogRepository.save(closurelogEntity);
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(MessageConstants.CLOSURE_RESET_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, EkycConstants.CLOSURE_ADMIN_SERVICE, "resetClosureStatus", e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.CLOSURE_ADMIN_SERVICE, "resetClosureStatus", e.getMessage(),
					EkycConstants.CLOSURE_ERROR_CODE);
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}

	@Override
	public ResponseModel getClosureStatus(String userId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(userId);
			if (closurelogEntity != null) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				 // Create a Map to hold additional details
	            Map<String, Object> resultDetails = new HashMap<>();
	            resultDetails.put("UserID", closurelogEntity.getUserId());
	            resultDetails.put("adminstatus", closurelogEntity.getAdminstatus());
	            resultDetails.put("RejectedReason", closurelogEntity.getRejectedReason());
	            responseModel.setResult(resultDetails);
			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.CLOSURE_ID_NULL);
			}
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.SaveLog(null, EkycConstants.CLOSURE_ADMIN_SERVICE, "getClosureStatus", e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.CLOSURE_ADMIN_SERVICE, "getClosureStatus", e.getMessage(),
					EkycConstants.CLOSURE_ERROR_CODE);
			responseModel = commonMethods.constructFailedMsg(e.getMessage());
		}
		return responseModel;
	}
}