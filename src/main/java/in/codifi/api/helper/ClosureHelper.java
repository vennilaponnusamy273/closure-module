package in.codifi.api.helper;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import in.codifi.api.restservice.SmsRestService;
import in.codifi.api.utilities.CommonMethods;

@ApplicationScoped
public class ClosureHelper {
	private static final Logger logger = LogManager.getLogger(ClosureHelper.class);
	@Inject
	CommonMethods commonMethods;
	@Inject
	SmsRestService smsRestService;
	
	/**
	 * Method to save User Sms Otp in DB
	 * 
	 * @param otp
	 * @param userEntity
	 * @return
	 */
	public void sendClosureOtp(String mobileNo) {
		try {
			int otp = 0;
			otp = commonMethods.generateOTP(Long.parseLong(mobileNo));
			smsRestService.sendOTPtoMobile(otp,mobileNo);
		} catch (Exception e) {
			logger.error("An error occurred: " + e.getMessage());
			commonMethods.sendErrorMail(
					"An error occurred while processing your request, In saveOrUpdateSmsTrigger for the Error: "
							+ e.getMessage(),
					"ERR-001");
		}
	}

}
