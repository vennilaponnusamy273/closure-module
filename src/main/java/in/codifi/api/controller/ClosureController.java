package in.codifi.api.controller;

import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import in.codifi.api.controller.spec.IClosureController;
import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.PdfApplicationDataModel;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IClosureService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.EkycConstants;
import in.codifi.api.utilities.MessageConstants;

@Path("/closure")
public class ClosureController implements IClosureController {

	@Inject
	IClosureService closureService;
	@Inject
	CommonMethods commonMethods;
	
	
	@Override
	public ResponseModel PositionFundsHoldingsCheck(String Token) {
		ResponseModel responseModel = new ResponseModel();
		if (Token != null ) {
			responseModel = closureService.CheckPositionHoldandfunds(Token);
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
		if (fileModel != null && fileModel.getApplicationId() !=null &&
			    (fileModel.getDocumentType().contains(EkycConstants.CMR_COPY) ||
			     fileModel.getDocumentType().contains(EkycConstants.CLOSURE_SIGN))) {
			System.out.println("the fileModel"+fileModel.getDocumentType());
			response = closureService.UploadCMR(fileModel);
		} else {
			if (fileModel == null) {
				response = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
			} else if (!fileModel.getDocumentType().contains(EkycConstants.CMR_COPY)||!fileModel.getDocumentType().contains(EkycConstants.CLOSURE_SIGN)) {
				response = commonMethods.constructFailedMsg(MessageConstants.WRONG_DOCUMENT);
			} else {
				response = commonMethods.constructFailedMsg(MessageConstants.USER_ID_NULL);
			}
		}
		return response;
	}
	
	@SuppressWarnings("unused")
	@Override
	public Response GeneratePdf(String Token,String dpId) {
		if (Token !=null) {
			return closureService.GeneratePdf(Token,dpId);
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
			responseModel = closureService.getDpDetails(Token);
		} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}
	
	@Override
	public ResponseModel getRekycLogs(String UserId) {
		ResponseModel responseModel = new ResponseModel();
		if (UserId != null ) {
			responseModel = closureService.getRekycLogs(UserId);
		} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}

	@Override
	public ResponseModel updateAccTypeReason(String UserId, int accType, String accCloseReason,String TargetDpID,String dpID,String TargetDpIDType) {
		ResponseModel responseModel = new ResponseModel();
		if (UserId != null ) {
			responseModel = closureService.updateAccTypeReason(UserId,accType,accCloseReason,TargetDpID,dpID, TargetDpIDType);
		} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}

	@Override
	public ResponseModel generateEsign(PdfApplicationDataModel pdfModel) {
		ResponseModel responseModel = new ResponseModel();
		if (pdfModel != null ) {
			responseModel = closureService.generateEsign(pdfModel);
		} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}
	
	/**
	 * Method to re direct from NSDL
	 */
	public Response getNsdlXml(String msg) {
		if (msg!=null) {
			return closureService.getNsdlXml(msg);
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.XML_MSG_NULL).build();
		}
	}
	
	
	@Override
	public Response getCMR(@NotNull String UserId, @NotNull String type) {
		if (UserId !=null && type!=null) {
			return closureService.getCMR(UserId, type);
		} else {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(MessageConstants.PARAMETER_NULL)
					.build();
		}
	}

	@Override
	public void EsignclosureMailAndSms(String UserId, String emailId) throws MessagingException {
		closureService.EsignclosureMailAndSms(UserId, emailId);
	}

	@Override
	public ResponseModel getclosureStatus(String UserId) {
		ResponseModel responseModel = new ResponseModel();
		if (UserId != null ) {
			responseModel = closureService.getClosureStatus(UserId);
		} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}
}
