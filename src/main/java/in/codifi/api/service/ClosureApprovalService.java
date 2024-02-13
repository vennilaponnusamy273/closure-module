package in.codifi.api.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ClosureEmailTemplateEntity;
import in.codifi.api.entity.ClosurelogEntity;
import in.codifi.api.helper.ClosureHelper;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ClosureEmailLogRepository;
import in.codifi.api.repository.ClosureEmailTemplateRepository;
import in.codifi.api.repository.ClosurelogRepository;
import in.codifi.api.restservice.SmsRestService;
import in.codifi.api.service.spec.IClosureApprovalService;
import in.codifi.api.utilities.CommonMail;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
public class ClosureApprovalService  implements IClosureApprovalService{

	private static final Logger logger = LogManager.getLogger(ClosureApprovalService.class);
	@Inject
	ClosurelogRepository closurelogRepository;
	@Inject
	CommonMethods commonMethods;
	@Inject
	ClosureEmailLogRepository emailLogRepository;
	@Inject
	CommonMail commonMail;
	@Inject
	ClosureEmailTemplateRepository emailTemplateRepository;
	@Inject
	ApplicationProperties props;
	
	@Inject
	ClosureHelper closureHelper;
	
	@Inject
	SmsRestService smsRestService;
	
	@Override
	public ResponseModel verifyOtp(String id, String userId, int otp) {
	    ResponseModel response = new ResponseModel();
	    try {
	        ClosurelogEntity closurelogEntity = closurelogRepository.findByIdAndUserId(Long.parseLong(id), userId);
	        if (closurelogEntity != null) {
	            int existingOtp = closurelogEntity.getApproveOtp();
	            if (existingOtp == otp) {
	                closurelogEntity.setApproveOtpVerified(1);
	                closurelogRepository.save(closurelogEntity);
	                SendClosureCompletedMail(id,userId);
	                smsRestService.SendCompletedSms(userId);
	                response.setStat(EkycConstants.SUCCESS_STATUS);
	                response.setMessage(EkycConstants.SUCCESS_MSG);
	                response.setResult(MessageConstants.APPROVE_SUCCESS_OTP);
	            } else {
	                response = commonMethods.constructFailedMsg(MessageConstants.CLOSURE_WRONG_OTP);
	            }
	        } else {
	            response = commonMethods.constructFailedMsg(MessageConstants.USER_ID_INVALID);
	        }
	    } catch (Exception e) {
	        logger.error("An error occurred: " + e.getMessage());
	        commonMethods.SaveLog(null, EkycConstants.CLOSURE_SERVICE, "verifyOtp", e.getMessage());
	        commonMethods.sendErrorMail(EkycConstants.CLOSURE_SERVICE, "verifyOtp", e.getMessage(), EkycConstants.CLOSURE_ERROR_CODE);
	        response = commonMethods.constructFailedMsg(e.getMessage());
	    }
	    return response;
	}

	public void SendClosureCompletedMail(String id, String userId) throws MessagingException {
	    ClosureEmailTemplateEntity emailTempentity = emailTemplateRepository.findByKeyData("CompleteClosure");
	    ClosurelogEntity closurelogEntity = closurelogRepository.findByIdAndUserId(Long.parseLong(id), userId);
	    try {
	        List<String> toAdd = new ArrayList<>();
	        toAdd.add(closurelogEntity.getEmail());
	        String body = emailTempentity.getBody();

	        // Format the current date
	        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy/HH:mm:ss");
	        String currentdate = dateFormat.format(new Date());

	        String bodyMessageNew = body
	                .replace("{UserName}", closurelogEntity.getNameAsperPan())
	                .replace("{Trading}", userId)
	                .replace("{Demat}", closurelogEntity.getDpId())
	                .replace("{Date}", currentdate);
	        commonMail.sendMail(toAdd, emailTempentity.getSubject(), bodyMessageNew);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
