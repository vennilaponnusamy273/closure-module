package in.codifi.api.utilities;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import in.codifi.api.config.ApplicationProperties;
import in.codifi.api.entity.EmailTemplateEntity;
import in.codifi.api.entity.ErrorLogEntity;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.repository.EmailLogRepository;
import in.codifi.api.repository.EmailTemplateRepository;
import in.codifi.api.repository.ErrorLogRepository;

@ApplicationScoped
public class CommonMethods {

	@Inject
	EmailLogRepository emailLogRepository;
	@Inject
	EmailTemplateRepository emailTemplateRepository;
	@Inject
	ErrorLogRepository errorLogRepository;
	@Inject
	CommonMail commonMail;
	@Inject
	ApplicationProperties props;
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
		EmailTemplateEntity emailTemplateEntity = emailTemplateRepository.findByKeyData("error");
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
		ErrorLogEntity errorLogEntity = errorLogRepository.findByApplicationIdAndClassNameAndMethodName(applicationId,
				className, methodName);
		if (errorLogEntity == null) {
			errorLogEntity = new ErrorLogEntity();
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
}
