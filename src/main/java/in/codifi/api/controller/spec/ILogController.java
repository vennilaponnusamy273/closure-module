package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import in.codifi.api.model.ResponseModel;

public interface ILogController {

	/**
	 * method to check rest access log table
	 * 
	 * @author sowmiya
	 * @return
	 */
	@Path("/Logtables")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel CreateLogTable();
	
	/**
	 * method to check rest access log table
	 * 
	 * @return
	 */
	@Path("/RestServiceLogtables")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel CreateRestLogTable();
	
}
