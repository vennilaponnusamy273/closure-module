package in.codifi.api.helper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.entity.ClosurelogEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ClosurelogRepository;
import in.codifi.api.restservice.SmsRestService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@ApplicationScoped
public class ClosureHelper {
	private static final Logger logger = LogManager.getLogger(ClosureHelper.class);
	@Inject
	CommonMethods commonMethods;
	@Inject
	SmsRestService smsRestService;
	@Inject
	ClosurelogRepository closurelogRepository;

	
	/**
	 * Method to save User Sms Otp in DB
	 * 
	 * @param otp
	 * @param userEntity
	 * @return
	 */
	public ResponseModel sendApprovalClosureSmsOTp(String userID) {
		ResponseModel responseModel = new ResponseModel();
		try {
			ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(userID);
			if(closurelogEntity!=null) {
			int otp = 0;
			otp = commonMethods.generateOTP(Long.parseLong(closurelogEntity.getMobile()));
			smsRestService.sendOTPtoMobile(otp,closurelogEntity.getMobile());
			closurelogEntity.setApproveOtp(otp);
			closurelogEntity.setApproveOtpVerified(0);
			closurelogRepository.save(closurelogEntity);
			responseModel.setResult(MessageConstants.SEND_SUCCESS_OTP);
		}} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.CLOSURE_HELPER,"sendClosureOtp",e.getMessage(),EkycConstants.CLOSURE_ERROR_CODE);
		}
		return responseModel;
	}
}
