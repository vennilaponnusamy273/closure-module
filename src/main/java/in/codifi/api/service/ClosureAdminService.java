package in.codifi.api.service;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import in.codifi.api.restservice.SmsRestService;
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
	@Inject
	SmsRestService smsRestService;

	@Override
	public ResponseModel updateClosureStatus(int status, String userId, String rejectedReason) {
		ResponseModel response = new ResponseModel();

		try {
			ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(userId);
			if (status == 3) {
			    if (closurelogEntity.getAdminstatus() != 1 || closurelogEntity.getApproveOtpVerified() != 1) {
			        return commonMethods.constructFailedMsg(MessageConstants.COMPLETE_ERROR_MESSAGE);
			    }
			}
			if (closurelogEntity == null) {
				closurelogEntity = new ClosurelogEntity();
				closurelogEntity.setUserId(userId);
			}

			closurelogEntity.setAdminstatus(status);
			closurelogEntity.setRejectedReason(status == 2 ? rejectedReason : "");

			// Save or update the closure status entity
			closurelogRepository.save(closurelogEntity);

			if (status == 1) {
				sendClosureApprovalEmailandSmsOtp(closurelogEntity.getEmail(), closurelogEntity.getMobile(),
						closurelogEntity.getNameAsperPan() != null ? closurelogEntity.getNameAsperPan() : "",
						closurelogEntity.getDpId(), userId);
			} else if (status == 2) {
				sendClosureRejectionEmailandSms(closurelogEntity.getEmail(), closurelogEntity.getMobile(),
						closurelogEntity.getNameAsperPan() != null ? closurelogEntity.getNameAsperPan() : "",
						closurelogEntity.getDpId(), rejectedReason, userId);
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


	public ResponseModel sendClosureApprovalEmailandSmsOtp(String EmailID, String MobileNo,String Username, String DpID,String userId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			if (MobileNo != null && EmailID != null) {
				commonMethods.sendApprovalClosureMail(EmailID,Username,DpID,userId);
				// closureHelper.sendApprovalClosureSmsOTp(userId,MobileNo);
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


	public ResponseModel sendClosureRejectionEmailandSms(String EmailID, String MobileNo,String name,String DpId,String RejectedReason,String userId) {
		ResponseModel responseModel = new ResponseModel();
		try {
			if (MobileNo != null && EmailID != null) {
				commonMethods.sendRejectionClosureMail(EmailID,name,DpId,RejectedReason,userId);
				smsRestService.sendRejectionClosureOtp(DpId,RejectedReason,MobileNo);
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

	        // Parse into LocalDateTime and set time to 23:59:59.999999 for the toDate
	        LocalDateTime endDateTime = LocalDateTime.parse(to, inputFormatter)
	            .with(LocalTime.of(23, 59, 59, 999999999));
	        Date fromDate = Date.from(fromDateTime.atZone(ZoneId.systemDefault()).toInstant());
	        Date toDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

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

	                String status = (closurelogEntity.getAdminstatus() == 1) ? "Approved" : 
	                	(closurelogEntity.getAdminstatus() == 0) ? "InProgress" : 
	                    (closurelogEntity.getAdminstatus() == 2) ? "Rejected" :
	                    (closurelogEntity.getAdminstatus() == 3) ? "Closed" : "";


	                resultDetails.put("UserID", closurelogEntity.getUserId());
	                resultDetails.put("Name", name);
	                resultDetails.put("Status", status);
	                resultDetails.put("approveOtpverified", closurelogEntity.getApproveOtpVerified());

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


	@Override
	public ResponseModel getStatusCount(LogsRequestModel logsRequestModel) {
		ResponseModel responseModel = new ResponseModel();

		try {
			String from = logsRequestModel.getFromDate();
			String to = logsRequestModel.getToDate();

			LocalDateTime fromDateTime = LocalDateTime.parse(from, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			LocalDateTime endDateTime = LocalDateTime.parse(to, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
					.with(LocalTime.of(23, 59, 59, 999999999));

			Date fromDate = Date.from(fromDateTime.atZone(ZoneId.systemDefault()).toInstant());
			Date toDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());

			List<Object[]> result = closurelogRepository.findStatus(fromDate, toDate);
			if (result != null && !result.isEmpty()) {
				Object[] row = result.get(0); // Assuming you expect only one row

				Map<String, Object> resultData = new HashMap<>();
				resultData.put("Total Record Count", row[0]);
				resultData.put("Approved Count", row[1]);
				resultData.put("Rejected Count", row[2]);
				resultData.put("New/Reset Count", row[3]);
				resultData.put("Verified Count", row[4]);
				resultData.put("Not Verified Count", row[5]);
				responseModel.setMessage(EkycConstants.SUCCESS_MSG);
				responseModel.setStat(EkycConstants.SUCCESS_STATUS);
				responseModel.setResult(resultData);

			} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.NO_RECORD_FOUND);
			}
		} catch (DateTimeParseException e) {
			logger.error("Error parsing date: " + e.getMessage());
			responseModel = commonMethods.constructFailedMsg(MessageConstants.INVALID_DATE_FORMAT);
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage(), e);
			responseModel = commonMethods.constructFailedMsg("An error occurred while processing the request.");
		}

		return responseModel;
	}


	@Override
	public ResponseModel resendConfirmationMail(String userId) {
		ResponseModel response = new ResponseModel();

		try {
			ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(userId);
			if (closurelogEntity.getAdminstatus() == 1) {
				sendClosureApprovalEmailandSmsOtp(closurelogEntity.getEmail(), closurelogEntity.getMobile(),
						closurelogEntity.getNameAsperPan() != null ? closurelogEntity.getNameAsperPan() : "",
						closurelogEntity.getDpId(), userId);
				response.setMessage(EkycConstants.SUCCESS_MSG);
				response.setStat(EkycConstants.SUCCESS_STATUS);
				response.setResult(MessageConstants.MAIL_SUCCESS);
			} else {
				response = commonMethods.constructFailedMsg(MessageConstants.NOT_APPROVED);
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

}