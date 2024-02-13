package in.codifi.api.service.spec;

import in.codifi.api.model.ResponseModel;

public interface ILogService {

	/**
	 * method to check rest access log table
	 * 
	 * @return
	 */
	ResponseModel checkRestAccessLogTable();
	
	/**
	 * method to check rest service access log table
	 * 
	 * @return
	 */
	ResponseModel checkRestServiceAccessLogTable();
}
