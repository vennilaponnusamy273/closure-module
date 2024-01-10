package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.jboss.resteasy.reactive.MultipartForm;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.model.FormDataModel;
import in.codifi.api.model.PdfApplicationDataModel;
import in.codifi.api.model.ResponseModel;

@SuppressWarnings("removal")
public interface IClosureController {
	
	
	@Path("/PositionFundsHoldingsCheck")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel PositionFundsHoldingsCheck(@NotNull @QueryParam("token") String Token);

	/**
	 * Method to Upload proof Documnt
	 */
	@Path("/upload")
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@APIResponse(description = "Method to Upload CMR Document")
	ResponseModel uploadCmrCopy(@MultipartForm FormDataModel fileModel);

	
	/**
	 * Method to Generate PDF
	 * 
	 * @author Vennila
	 * @return
	 */
	@Path("/getPdf")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@APIResponse(description = "Method to Generate PDF")
	Response GeneratePdf(@NotNull @QueryParam("token") String Token,@NotNull @QueryParam("dpId") String dpId);

	
	@Path("/getDpDetails")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel getDpDetails(@NotNull @QueryParam("token") String Token);

	@Path("/getclosureLogs")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel getRekycLogs(@NotNull @QueryParam("UserId")String UserId);
	
	
	@Path("/updateAccTypeReason")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel updateAccTypeReason(@NotNull @QueryParam("UserId")String UserId,@NotNull @QueryParam("accType") int accType,@NotNull @QueryParam("accCloseReason")String accCloseReason);
	
	@Path("/generateEsign")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	 ResponseModel generateEsign(@RequestBody PdfApplicationDataModel pdfModel);
	
	@Path("/getNsdlXml")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@APIResponse(description = "Test")
	Response getNsdlXml(@FormParam("msg") String msg);

}
