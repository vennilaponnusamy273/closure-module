package in.codifi.api.trading.restservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "config-trading")
@RegisterClientHeaders
public interface ItradingRestServices {

	@GET
	@Path("/furest/funds/limits")
	@Consumes("application/json")
    Response getFunds(@HeaderParam("Authorization") String authToken,String RequestBody);

	
	@GET
	@Path("/horest/holdings/")
	@Consumes("application/json")
	Response getHoldings(@HeaderParam("Authorization") String authToken);


	@GET
	@Path("/porest/positions/")
	@Consumes("application/json")
	Response getPosition(@HeaderParam("Authorization") String authToken,String RequestBody);


	@GET
	@Path("/curest/common/getClientBasicData")
	@Consumes("application/json")
	Response getUserDetails(@HeaderParam("Authorization") String authToken);


	@GET
	@Path("/curest/common/getUserDpDetails")
	@Consumes("application/json")
	Response getDpDetails(@HeaderParam("Authorization") String authToken);
}
