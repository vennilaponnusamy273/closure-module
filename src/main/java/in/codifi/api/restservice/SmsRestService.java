package  in.codifi.api.restservice;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ClosurelogEntity;
import in.codifi.api.repository.ClosurelogRepository;
import in.codifi.api.utilities.CommonMethods;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
			String Text ="Dear Customer, Your OTP to process your online DP & Trading account closure is "+otp+". - Chola Securities";
			String smsResponse=iSmsRestService.SendSms(props.getSmsUserId(), props.getSmsPass(), props.getSmsAppId(),
					props.getSmsSubAppId(), props.getSmsContentType(),mobileNumber, props.getSmsFrom(),
					Text.replace("+", "%20"), props.getSmsSelfid(), props.getSmsAlert(), props.getSmsDlrReq());
			commonMethods.storeSmsLog(Text,smsResponse,"sendOTPtoMobile",Long.parseLong(mobileNumber));
		} catch (Exception e) {

			e.printStackTrace();

		}
	}
	
	
	public void sendRejectionClosureOtp(String dpId,String Reason,String MobileNO) {
		try {
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
	
	public void sendEsignSms(String userId, String DpID, String MobileNO) {

		Date currentDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("M/dd/yyyy");
		String formattedDate = sdf.format(currentDate);

		String smsUrl = "https://push3.maccesssmspush.com/servlet/com.aclwireless.pushconnectivity.listeners.TextListener"
				+ "?userId=cholalt"+"&pass=chol987" + "&appid=cholalt" + "&subappid=cholalt" + "&contenttype=1"
				+ "&to=" + MobileNO + "&from=CHOLAS"
				+ "&text=Dear%20Customer,%20We%27ve%20received%20your%20closure%20request%20for%20the%20Trading%20"
				+ userId + "%20/%20Demat%20" + DpID + "%20account%20on%20" + formattedDate
				+ ".%20Our%20team%20is%20reviewing%20the%20documents,%20and%20we%27ll%20update%20you%20on%20the%20status%20soon.%20Thank%20you%20for%20your%20patience.%20-%20Chola%20Securities"
				+ "&selfid=true" + "&alert=1" + "&dlrreq=true";

		HttpClient httpClient = HttpClients.createDefault();
		HttpGet request = new HttpGet(smsUrl);

		try {
			HttpResponse response = httpClient.execute(request);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			StringBuilder result = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}

			System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
			System.out.println("SMS Gateway Response: " + result.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 

	


	public void SendCompletedSms(String userId) {
		try {
	        Date currentDate = new Date();
	        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	        String formattedDate = sdf.format(currentDate);
			ClosurelogEntity closurelogEntity = closurelogRepository.findByUserId(userId);
			String Text="Dear Customer, Your online request submitted for closure of Trading Account "+closurelogEntity.getUserId()+" and Demat Account "+closurelogEntity.getDpId()+" has been successfully processed. The closure was completed on "+formattedDate+". - Chola Securities";
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
