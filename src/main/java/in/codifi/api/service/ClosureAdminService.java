package in.codifi.api.service;


import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.entity.ClosureStatusEntity;
import in.codifi.api.entity.ClosurelogEntity;
import in.codifi.api.helper.ClosureHelper;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ClosureStatusRepository;
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
	ClosureStatusRepository closureStatusRepository;
	@Inject
	ClosureHelper closureHelper;
	@Inject
	ClosurelogRepository closurelogRepository;

	@Override
	public ResponseModel updateClosureStatus(int status, String userId, String rejectedReason) {
		ResponseModel response = new ResponseModel();

		try {
			ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(userId);

			if (closurelogEntity != null) {
				ClosureStatusEntity closureStatusEntity = closureStatusRepository.findByUserId(userId);

				if (closureStatusEntity == null) {
					closureStatusEntity = new ClosureStatusEntity();
					closureStatusEntity.setUserId(userId);
				}

				closureStatusEntity.setStatus(status);
				closureStatusEntity.setRejectedReason(status == 0 ? rejectedReason : "");

				// Save or update the closure status entity
				closureStatusRepository.save(closureStatusEntity);

				if (status == 1) {
					sendClosureEmailandSmsOtp(closurelogEntity.getEmail(), closurelogEntity.getMobile());
				}
				response.setMessage(EkycConstants.SUCCESS_MSG);
				response.setStat(EkycConstants.SUCCESS_STATUS);
				response.setResult(closureStatusEntity);
			} else {
				response = commonMethods.constructFailedMsg(MessageConstants.CLOSURE_ID_NULL);
			}
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
			closureStatusRepository.deleteByUserId(userId);
			responseModel.setMessage(EkycConstants.SUCCESS_MSG);
			responseModel.setStat(EkycConstants.SUCCESS_STATUS);
			responseModel.setResult(MessageConstants.CLOSURE_RESET_SUCCESS);
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
			ClosureStatusEntity closureStatusEntity = closureStatusRepository.findByUserId(userId);
			if (closureStatusEntity != null) {
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(closureStatusEntity);
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