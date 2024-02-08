package in.codifi.api.service;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.entity.ClosurelogEntity;
import in.codifi.api.helper.ClosureHelper;
import in.codifi.api.model.LogsRequestModel;
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
			closurelogEntity.setRejectedReason(status == 2 ? rejectedReason : "");

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
				closurelogEntity.setAdminstatus(0);
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

	@Override
	public ResponseModel getClosureLogs(LogsRequestModel logsRequestModel) {
	    ResponseModel responseModel = new ResponseModel();

	    try {
	        List<ClosurelogEntity> closurelogEntities = null;
	        String from = logsRequestModel.getFromDate();
	        String to = logsRequestModel.getToDate();
	        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	        // Parse into LocalDateTime instead of LocalDate
	        LocalDateTime fromDateTime = LocalDateTime.parse(from, inputFormatter);
	        LocalDateTime toDateTime = LocalDateTime.parse(to, inputFormatter);

	        // Convert LocalDateTime to java.util.Date
	        Date fromDate = Date.from(fromDateTime.atZone(ZoneId.systemDefault()).toInstant());
	        Date toDate = Date.from(toDateTime.atZone(ZoneId.systemDefault()).toInstant());

	        if (logsRequestModel.getUserId() != null) {
	            closurelogEntities = closurelogRepository.findByUserIdAndDate(logsRequestModel.getUserId(), fromDate, toDate);
	        } else {
	            closurelogEntities = closurelogRepository.findByDate(fromDate, toDate);
	        }

	        // Check if closure logs are found
	        if (closurelogEntities != null && !closurelogEntities.isEmpty()) {
	            responseModel.setMessage(EkycConstants.SUCCESS_MSG);
	            responseModel.setStat(EkycConstants.SUCCESS_STATUS);

	            // Extract details for the response
	            List<Map<String, Object>> resultList = new ArrayList<>();

	            for (ClosurelogEntity closurelogEntity : closurelogEntities) {
	                Map<String, Object> resultDetails = new HashMap<>();

	                String firstName = closurelogEntity.getFirstName();
	                String middleName = closurelogEntity.getMiddleName();
	                String lastName = closurelogEntity.getLastName();

	                String name = "";

	                if (firstName != null && !firstName.isEmpty()) {
	                    name += firstName;
	                }

	                if (middleName != null && !middleName.isEmpty()) {
	                    if (!name.isEmpty()) {
	                        name += " ";
	                    }
	                    name += middleName;
	                }

	                if (lastName != null && !lastName.isEmpty()) {
	                    if (!name.isEmpty()) {
	                        name += " ";
	                    }
	                    name += lastName;
	                }

	                String status = (closurelogEntity.getAdminstatus() == 1) ? "Approved" : ((closurelogEntity.getAdminstatus() == 2) ? "Rejected" : "");

	                resultDetails.put("UserID", closurelogEntity.getUserId());
	                resultDetails.put("Name", name);
	                resultDetails.put("Status", status);

	                resultList.add(resultDetails);
	            }

	            responseModel.setResult(resultList);
	        } else {
	            responseModel = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
	        }

	        return responseModel;

	    } catch (Exception e) {
	        logger.error("An error occurred: " + e.getMessage());
	        responseModel = commonMethods.constructFailedMsg(e.getMessage());
	        e.printStackTrace();
	    }

	    return responseModel;
	}

}