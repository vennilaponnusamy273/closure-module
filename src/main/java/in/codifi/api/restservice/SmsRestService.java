package  in.codifi.api.restservice;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ClosurelogEntity;
import in.codifi.api.repository.ClosurelogRepository;
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
	@Inject
	ClosurelogRepository closurelogRepository;

	/**
	 * Method to send otp to Mobile Number
	 * 
	 * @param otp
	 * @param mobile Number
	 * @return
	 */
	public void sendOTPtoMobile(int otp, String mobileNumber) {
		try {
			String Text ="Dear Customer, Your OTP to process your online DP & Trading account closure is "+ otp;
			System.out.println("the sendOTPtoMobile Text"+Text);
			String smsResponse=iSmsRestService.SendSms(props.getSmsUserId(), props.getSmsPass(), props.getSmsAppId(),
					props.getSmsSubAppId(), props.getSmsContentType(),mobileNumber, props.getSmsFrom(),
					Text, props.getSmsSelfid(), props.getSmsAlert(), props.getSmsDlrReq());
			commonMethods.storeSmsLog(Text,smsResponse,"sendOTPtoMobile",Long.parseLong(mobileNumber));
		} catch (Exception e) {

			e.printStackTrace();

		}
	}
	
	
	public void sendRejectionClosureOtp(String dpId,String Reason,String MobileNO) {
		try {
			//String Text ="Your online account closure request for the trading "+dpId+" and Demat account ------ has been rejected Reason: "+Reason+". Team CSEC.";
			String Text="Your online account closure request for the trading ------- and Demat account ------ has been rejected Reason: [Brief Explanation]. Team CSEC.";
			System.out.println("the sendRejectionClosureOtp Text"+Text);
			String smsResponse=iSmsRestService.SendSms(props.getSmsUserId(), props.getSmsPass(), props.getSmsAppId(),
					props.getSmsSubAppId(), props.getSmsContentType(),MobileNO, props.getSmsFrom(),
					Text, props.getSmsSelfid(), props.getSmsAlert(), props.getSmsDlrReq());
			commonMethods.storeSmsLog(Text,smsResponse,"sendRejectionClosureOtp",Long.parseLong(MobileNO));
		} catch (Exception e) {

			e.printStackTrace();

		}
	}
	
	public void sendEsignSms(String userId,String DpID,String MobileNO) {
		try {
			//String Text ="Your online account closure request for the trading "+dpId+" and Demat account ------ has been rejected Reason: "+Reason+". Team CSEC.";
			String Text="We've received your closure request for the Trading__client id_______/Demat _dp id______account on [date]. Our team is reviewing the documents, and we'll update you on the status soon.\r\n"
					+ "\r\n"
					+ "Thank you for your patience.\r\n"
					+ "";
			System.out.println("the sendEsignMail Text"+Text);
			String smsResponse=iSmsRestService.SendSms(props.getSmsUserId(), props.getSmsPass(), props.getSmsAppId(),
					props.getSmsSubAppId(), props.getSmsContentType(),MobileNO, props.getSmsFrom(),
					Text, props.getSmsSelfid(), props.getSmsAlert(), props.getSmsDlrReq());
			commonMethods.storeSmsLog(Text,smsResponse,"sendEsignSms",Long.parseLong(MobileNO));
		} catch (Exception e) {

			e.printStackTrace();

		}
	}


	public void SendCompletedSms(String userId) {
		try {
			ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(userId);
			String Text="Dear Customer, Your online request submitted for closure of Trading Account [Account Number] and Demat Account [Account Number] has been successfully processed. The closure was completed on [Date]. Team CSEC.";
			System.out.println("the SendCompletedSms Text"+Text);
			String smsResponse=iSmsRestService.SendSms(props.getSmsUserId(), props.getSmsPass(), props.getSmsAppId(),
					props.getSmsSubAppId(), props.getSmsContentType(),closurelogEntity.getMobile(), props.getSmsFrom(),
					Text, props.getSmsSelfid(), props.getSmsAlert(), props.getSmsDlrReq());
			commonMethods.storeSmsLog(Text,smsResponse,"SendCompletedSms",Long.parseLong(closurelogEntity.getMobile()));
		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	
}
