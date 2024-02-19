package in.codifi.api.controller.spec;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.wildfly.common.annotation.NotNull;

import in.codifi.api.model.ResponseModel;

public interface IClosureApprovalController {

	
	/**
	 * method to verifyOtp
	 * 
	 * @author Vennila
	 * 
	 * @param id,userId,otp
	 * @return
	 */
	
	
	@Path("/verifyOtp")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel verifyOtp(@NotNull @QueryParam("id") String id,@NotNull @QueryParam("userId") String userId,@NotNull @QueryParam("otp") int otp);
	
	@Path("/sendOtp")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	ResponseModel sendOtp(@NotNull @QueryParam("userId") String userId);
}
