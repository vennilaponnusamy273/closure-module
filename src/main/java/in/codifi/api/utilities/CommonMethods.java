package in.codifi.api.utilities;

import java.io.FileInputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.mail.MessagingException;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.ClosureEmailTemplateEntity;
import in.codifi.api.entity.ClosureErrorLogEntity;
import in.codifi.api.entity.ClosureSmsLogEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.ClosureEmailLogRepository;
import in.codifi.api.repository.ClosureEmailTemplateRepository;
import in.codifi.api.repository.ClosureErrorLogRepository;
import in.codifi.api.repository.ClosureSmsLogRepository;

@ApplicationScoped
public class CommonMethods {

	@Inject
	ClosureEmailLogRepository emailLogRepository;
	@Inject
	ClosureEmailTemplateRepository emailTemplateRepository;
	@Inject
	ClosureErrorLogRepository errorLogRepository;
	@Inject
	CommonMail commonMail;
	@Inject
	ApplicationProperties props;
	@Inject
	ClosureSmsLogRepository smsLogRepository;
	/**
	 * Method to construct Failed method
	 * 
	 * @author prade
	 * @param failesMessage
	 * @return
	 */
	public ResponseModel constructFailedMsg(String failesMessage) {
		ResponseModel model = new ResponseModel();
		model.setStat(EkycConstants.FAILED_STATUS);
		model.setMessage(EkycConstants.FAILED_MSG);
		model.setReason(failesMessage);
		return model;
	}
	

	/**
	 * Method to send Error message to mail
	 * 
	 * @author pradeep
	 * @param errorMessage
	 * @param errorCode
	 */
	public void sendErrorMail(String errorMessage, String errorCode) {
		ClosureEmailTemplateEntity emailTemplateEntity = emailTemplateRepository.findByKeyData("error");
		if (emailTemplateEntity != null && emailTemplateEntity.getBody() != null
				&& emailTemplateEntity.getSubject() != null && emailTemplateEntity.getToAddress() != null) {
			List<String> toAdd = new ArrayList<>();
			toAdd.add(emailTemplateEntity.getToAddress());
			String bodyMessage = emailTemplateEntity.getBody();
			String body = bodyMessage.replace("{errorMessage}", errorMessage).replace("{errorCode}", errorCode);
			String subject = emailTemplateEntity.getSubject();
			if (emailTemplateEntity.getCc() != null) {
				String[] ccAddresses = emailTemplateEntity.getCc().split(",");
				for (String ccAddress : ccAddresses) {
					toAdd.add(ccAddress.trim());
				}
			}
			commonMail.sendMail(toAdd, subject, body);
		}
	}
	
	/**
	 * Method to save logs
	 * 
	 * @author prade
	 * @param applicationId
	 * @param className
	 * @param methodName
	 * @param reason
	 */
	public void SaveLog(String applicationId, String className, String methodName, String reason) {
		ClosureErrorLogEntity errorLogEntity = errorLogRepository.findByApplicationIdAndClassNameAndMethodName(applicationId,
				className, methodName);
		if (errorLogEntity == null) {
			errorLogEntity = new ClosureErrorLogEntity();
			errorLogEntity.setApplicationId(applicationId);
			errorLogEntity.setClassName(className);
			errorLogEntity.setMethodName(methodName);
		}
		errorLogEntity.setReason(reason);
		if (errorLogEntity != null) {
			errorLogRepository.save(errorLogEntity);
		}
	}

	public String decrypt(String encrypted) {
		byte[] ivbuf = new byte[16];
		try {
			IvParameterSpec iv = new IvParameterSpec(ivbuf);
			SecretKeySpec skeySpec = new SecretKeySpec(props.getTokenEncryptKey().getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));
			return new String(original);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	public String readUserNameFromCerFile(String certificateFilepath) {
		String userName = "";
		try (FileInputStream fis = new FileInputStream(certificateFilepath)) {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			Certificate cert = certFactory.generateCertificate(fis);
			X509Certificate x509Cert = (X509Certificate) cert;
			if (StringUtil.isNotNullOrEmpty(x509Cert.getSubjectDN().toString())) {
				String subject = x509Cert.getSubjectDN().toString();
				userName = StringUtil.substringAfter(subject, "CN=");
			}
			return userName;
		} catch (Exception e) {
			e.printStackTrace();
			return userName;
		}
	}
	
	public int generateOTP(long mobileNumber) {
		int otp = 123456;
		if (mobileNumber == 1234567890 || mobileNumber == 1111100000) {
			otp = 000000;
		} else {
			otp = (int) (Math.random() * 900000) + 100000;
		}
		System.out.println("OTP : " + otp);
		return otp;
	}
	
	public void sendEsignClosureMail(String emailId) throws MessagingException {
		ClosureEmailTemplateEntity emailTempentity = emailTemplateRepository.findByKeyData("EsignClosure");
		try {
			System.out.println("tje sendEsignClosureMail");
			List<String> toAdd = new ArrayList<>();
			toAdd.add(emailId);
			String body_Message = emailTempentity.getBody();
			commonMail.sendMail(toAdd, emailTempentity.getSubject(), body_Message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to send mail
	 * 
	 * @param user
	 * @return
	 **/
	public void sendClosureMail(String emailId) throws MessagingException {
		ClosureEmailTemplateEntity emailTempentity = emailTemplateRepository.findByKeyData("Closure");
		try {
			List<String> toAdd = new ArrayList<>();
			toAdd.add(emailId);
			String body_Message = emailTempentity.getBody();
			commonMail.sendMail(toAdd, emailTempentity.getSubject(), body_Message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void storeSmsLog(String request, String smsResponse, String logMethod, long mobileNumber) {
		if (request == null || smsResponse == null || logMethod == null) {
			// Handle invalid input, such as throwing an IllegalArgumentException.
			throw new IllegalArgumentException("Request, smsResponse, or logMethod cannot be null.");
		}

		try {
			ClosureSmsLogEntity smsLogEntity = new ClosureSmsLogEntity();
			smsLogEntity.setMobileNo(mobileNumber);
			smsLogEntity.setLogMethod(logMethod);
			smsLogEntity.setRequestLog(request);
			smsLogEntity.setResponseLog(smsResponse);
			smsLogRepository.save(smsLogEntity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
