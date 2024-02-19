package in.codifi.api.controller;

import javax.inject.Inject;
import javax.ws.rs.Path;

import in.codifi.api.controller.spec.IClosureApprovalController;
import in.codifi.api.helper.ClosureHelper;
import in.codifi.api.model.ResponseModel;
import in.codifi.api.service.spec.IClosureApprovalService;
import in.codifi.api.utilities.CommonMethods;
import in.codifi.api.utilities.MessageConstants;

@Path("/ClosureApproval")
public class ClosureApprovalController implements IClosureApprovalController {

	@Inject
	IClosureApprovalService iClosureApprovalService;
	@Inject
	CommonMethods commonMethods;
	@Inject
	ClosureHelper closureHelper;
	
	@Override
	public ResponseModel verifyOtp(String id, String userId, int otp) {
		ResponseModel responseModel = new ResponseModel();
		if (id != null &&userId!=null&&otp>0 ) {
			responseModel = iClosureApprovalService.verifyOtp(id,userId,otp);
		} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}

	@Override
	public ResponseModel sendOtp(String userId) {
		ResponseModel responseModel = new ResponseModel();
		if (userId!=null ) {
			responseModel = closureHelper.sendApprovalClosureSmsOTp(userId);
		} else {
				responseModel = commonMethods.constructFailedMsg(MessageConstants.PARAMETER_NULL);
		}
		return responseModel;
	}

}
