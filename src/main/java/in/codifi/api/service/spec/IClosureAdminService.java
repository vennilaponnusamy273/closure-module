package in.codifi.api.service.spec;

import in.codifi.api.model.LogsRequestModel;
import in.codifi.api.model.ResponseModel;

public interface IClosureAdminService {

	/**
	 * method to update Approval and rejection
	 * 
	 * @author Vennila
	 * 
	 * @param Status,userId,RejectedReason
	 * @return
	 */
	ResponseModel updateClosureStatus(int Status, String userId, String RejectedReason);

	/**
	 * method to send Closure Email and SmsOtp
	 * 
	 * @author Vennila
	 * 
	 * @param EmailID,MobileNo
	 * @return
	 */

	//ResponseModel sendClosureApprovalEmailandSmsOtp(String EmailID, String MobileNo);

	/**
	 * method to reset the Approval and rejection
	 * 
	 * @author Vennila
	 * 
	 * @param userId
	 * @return
	 */
	ResponseModel resetClosureStatus(String userId);

	/**
	 * method to GetClosureStatus
	 * 
	 * @author Vennila
	 * 
	 * @param userId
	 * @return
	 */

	ResponseModel getClosureStatus(String userId);
	
	ResponseModel getClosureLogs(LogsRequestModel logsRequestModel);
}
