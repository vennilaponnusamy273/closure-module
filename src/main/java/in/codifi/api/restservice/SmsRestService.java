package  in.codifi.api.restservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.utilities.CommonMethods;

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
	 * @param otp
	 * @param mobile Number
	 * @return
	 */
	public void sendOTPtoMobile(int otp, String mobileNumber) {
		try {
			String Text = props.getSmsFirstText() + " " + otp + " " + props.getSmsSecondText();
			String smsResponse=iSmsRestService.SendSms(props.getSmsUserId(), props.getSmsPass(), props.getSmsAppId(),
					props.getSmsSubAppId(), props.getSmsContentType(),mobileNumber, props.getSmsFrom(),
					Text, props.getSmsSelfid(), props.getSmsAlert(), props.getSmsDlrReq());
			commonMethods.storeSmsLog(Text,smsResponse,"sendClosureOTPtoMobile",Long.parseLong(mobileNumber));
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	
}
