package in.codifi.api.service.spec;

import javax.mail.MessagingException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;

import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.PdfApplicationDataModel;
import in.codifi.api.model.ResponseModel;

public interface IClosureService {

	/**
	 * method to CheckPositionHoldandfunds
	 * 
	 * @author Vennila
	 * 
	 * @param Token
	 * @return
	 */
	ResponseModel CheckPositionHoldandfunds(String Token);

	/**
	 * method to uploadCmRCopy
	 * 
	 * @author Vennila
	 * 
	 * @param Token
	 * @return
	 */

	ResponseModel UploadCMR(FormDataModel fileModel);

	/**
	 * method to Genearte NSDL or CSDL PDF
	 * 
	 * @author Vennila
	 * 
	 * @param Token,DpID
	 * @return
	 */
	Response GeneratePdf(String token, String dpId);

	/**
	 * method to getDpDetails
	 * 
	 * @author Vennila
	 * 
	 * @param Token
	 * @return
	 */

	ResponseModel getDpDetails(String token);

	/**
	 * method to getRekycLogs
	 * 
	 * @author Vennila
	 * 
	 * @param userId
	 * @return
	 */

	ResponseModel getRekycLogs(String userId);

	/**
	 * method to updateAccTypeReason
	 * 
	 * @author Vennila
	 * 
	 * @param userId,accType,accCloseReason
	 * @return
	 */

	ResponseModel updateAccTypeReason(String userId, int accType, String accCloseReason,String TargetDpID,String dpId,String TargetDpIDType);

	/**
	 * method to getNsdlXml
	 * 
	 * @author Vennila
	 * 
	 * @param msg
	 * @return
	 */

	Response getNsdlXml(String msg);

	/**
	 * method to generateEsign
	 * 
	 * @author Vennila
	 * 
	 * @param PdfApplicationDataModel
	 * @return
	 */

	ResponseModel generateEsign(PdfApplicationDataModel pdfModel);
	
	
	/**
	 * Method to download uploaded file
	 * 
	 * @param userId and DocType
	 * @param type
	 * @return
	 */
	Response getCMR(@NotNull String applicationId, @NotNull String type);
	
	/**
	 * Method to EsignclosureMailAndSms
	 * 
	 * @param UserId and emailID
	 * @param type
	 * @return
	 */

	void EsignclosureMailAndSms(String UserId, String emailID) throws MessagingException;

}
