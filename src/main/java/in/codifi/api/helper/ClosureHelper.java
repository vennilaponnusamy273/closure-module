package in.codifi.api.helper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.entity.ClosurelogEntity;
import in.codifi.api.repository.ClosurelogRepository;
import in.codifi.api.restservice.SmsRestService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;

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
	public void sendApprovalClosureSmsOTp(String userID,String mobileNo) {
		try {
			ClosurelogEntity closurelogEntity = closurelogRepository.findByUserIdAndMobile(userID,mobileNo);
			if(closurelogEntity!=null) {
			int otp = 0;
			otp = commonMethods.generateOTP(Long.parseLong(mobileNo));
			smsRestService.sendOTPtoMobile(otp,mobileNo);
			closurelogEntity.setApproveOtp(otp);
			closurelogEntity.setApproveOtpVerified(0);
			closurelogRepository.save(closurelogEntity);
		}} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(EkycConstants.CLOSURE_HELPER,"sendClosureOtp",e.getMessage(),EkycConstants.CLOSURE_ERROR_CODE);
		}
	}
}
