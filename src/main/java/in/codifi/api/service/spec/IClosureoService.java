package in.codifi.api.service.spec;

import javax.ws.rs.core.Response;

import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.ResponseModel;

public interface IClosureoService {

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
	Response GeneratePdf(String token);

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

}
