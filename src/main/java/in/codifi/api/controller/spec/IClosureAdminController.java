package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.wildfly.common.annotation.NotNull;

import in.codifi.api.model.LogsRequestModel;
import in.codifi.api.model.ResponseModel;

public interface IClosureAdminController {

	
	@Path("/updateClosureStatus")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel updateClosureStatus(@NotNull @QueryParam("status")int Status,@NotNull @QueryParam("userId") String userId,@QueryParam("RejectedReason") String RejectedReason);
	
	@Path("/resetClosureStatus")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel resetClosureStatus(@NotNull @QueryParam("userId") String userId);
	
	@Path("/getClosureStatus")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel getClosureStatus(@NotNull @QueryParam("userId") String userId);
	
	
	@Path("/getClosureLogs")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel getClosureLogs(@RequestBody LogsRequestModel logsRequestModel);
}
