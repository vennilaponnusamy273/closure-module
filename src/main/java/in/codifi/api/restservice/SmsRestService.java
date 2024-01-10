package  in.codifi.api.restservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;

@ApplicationScoped
public class SmsRestService {
	@Inject
	@RestClient
	ISmsRestService iSmsRestService;
	@Inject
	ApplicationProperties props;
	@Inject
	CommonMethods commonMethods;

	/**
	 * Method to send otp to Mobile Number
	 * 
	 * @author Sowmiya
	 * @param otp
	 * @param mobile Number
	 * @return
	 */
	public void sendOTPtoMobile(int otp, long mobileNumber) {
		try {
			String Text = props.getSmsFirstText() + " " + otp + " " + props.getSmsSecondText();
			String smsResponse=iSmsRestService.SendSms(props.getSmsUserId(), props.getSmsPass(), props.getSmsAppId(),
					props.getSmsSubAppId(), props.getSmsContentType(), String.valueOf(mobileNumber), props.getSmsFrom(),
					Text, props.getSmsSelfid(), props.getSmsAlert(), props.getSmsDlrReq());
			commonMethods.storeSmsLog(Text,smsResponse,"sendClosureOTPtoMobile",mobileNumber);
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	
}
