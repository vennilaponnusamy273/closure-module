package in.codifi.api.service.spec;

import in.codifi.api.model.ResponseModel;

public interface IClosureApprovalService {

	
	/**
	 * method to verifyOtp
	 * 
	 * @author Vennila
	 * 
	 * @param id,userId,otp
	 * @return
	 */
	ResponseModel verifyOtp(String id, String userId, int otp);

}
