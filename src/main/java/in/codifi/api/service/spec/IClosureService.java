package in.codifi.api.service.spec;

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

	ResponseModel UploadCMR(FormDataModel fileModel);

	/**
	 * method to Genearte NSDL or CSDL PDF
	 * 
	 * @author Vennila
	 * 
	 * @param Token
	 * @return
	 */
	Response GeneratePdf(String token,String dpId);

	/**
	 * method to getDpDetails
	 * 
	 * @author Vennila
	 * 
	 * @param Token
	 * @return
	 */
	
	ResponseModel getDpDetails(String token);

	ResponseModel getRekycLogs(String userId);

	ResponseModel updateAccTypeReason(String userId, int accType, String accCloseReason);

	Response getNsdlXml(String msg);

	ResponseModel generateEsign(PdfApplicationDataModel pdfModel);


	ResponseModel closuremailotp(String EmailID, String MobileNo);

}
