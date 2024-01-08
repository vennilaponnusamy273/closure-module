package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import in.codifi.api.controller.spec.IClosureController;
import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.ClosureoService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@Path("/closure")
public class ClosureController implements IClosureController {

	@Inject
	ClosureoService closureoService;
	@Inject
	CommonMethods commonMethods;
	
	@Override
	public ResponseModel PositionFundsHoldingsCheck(String Token) {
		ResponseModel responseModel = new ResponseModel();
		if (Token != null ) {
			responseModel = closureoService.CheckPositionHoldandfunds(Token);
		} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}
	
	/**
	 * Method to Upload proof Documnt
	 */
	@Override
	public ResponseModel uploadCmrCopy(FormDataModel fileModel) {
		ResponseModel response = new ResponseModel();
		System.out.println("the uploadCmrCopy controller");
		if (fileModel != null && fileModel.getApplicationId() !=null &&fileModel.getDocumentType().contains(EkycConstants.CMR_COPY)) {
			response = closureoService.UploadCMR(fileModel);
		} else {
			if (fileModel == null) {
				response = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else if (!fileModel.getDocumentType().contains(EkycConstants.CMR_COPY)) {
				response = commonMethods.constructFailedMsg(MessageConstants.WRONG_DOCUMENT);
			} else {
				response = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			}
		}
		return response;
	}
	
	@SuppressWarnings("unused")
	@Override
	public Response GeneratePdf(String Token) {
		if (Token !=null) {
			return closureoService.GeneratePdf(Token);
		} else {
			if ( Token ==null) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.USER_ID_NULL)
						.build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.PARAMETER_NULL)
						.build();
			}
		}
	}
	
	@Override
	public ResponseModel getDpDetails(String Token) {
		ResponseModel responseModel = new ResponseModel();
		if (Token != null ) {
			responseModel = closureoService.getDpDetails(Token);
		} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}
	
	@Override
	public ResponseModel getRekycLogs(String UserId) {
		ResponseModel responseModel = new ResponseModel();
		if (UserId != null ) {
			responseModel = closureoService.getRekycLogs(UserId);
		} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}
}
