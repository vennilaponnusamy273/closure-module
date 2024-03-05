package in.codifi.api.utilities;

public class MessageConstants {

	public static final String PARAMETER_NULL = "The given parameter is null";
	public static final String USER_ID_NULL = "The given user ID is null";
	public static final String WRONG_DOCUMENT = "The given document type is wrong";
	public static final String NOT_AVAILABLE_POSITIONS = "Positions,Holdings and Funds are not available to proceed further.";
	
	public static final String POSITIONS_EXIST = "Positions exists.can't close the account.Please close the Positions and try again.";
	
	public static final String HOLDINGS_EXIST = "Holdings exists,please upload CMR copy to procesed further";
	public static final String FUND_AVAILABLE = "Funds available please withdraw/deposit to make Funds zero and try again";
	public static final String CMR_AVAILABLE = "CMR Copy Available, proceed further";
	public static final String FILE_NULL = "Please choose file";
	
	// Document
	public final static String FAILED_DOC_UPLOAD = "Failed while upload document";
	
	public static final String USER_ID_INVALID = "The given user ID is invalid";
	public static final String FILE_NOT_FOUND = "File not found on this ID";
	
	public static final String PDF_ENCRYPTED = "PDF is password protected,please remove the password and upload it";
	public static final String POSITIONS_NOT_EXIST = "Positions do not exists";
	public static final String FUNDS_NOT_EXIST = "Funds do not exists";
	public static final String HOLDINGS_NOT_EXIST = "Holdings do not exists";
	public static final String ERROR_WHILE_CREATING_XML = "Error occur while creating XML file";
	public static final String XML_MSG_NULL = "The XML msg is null";
	
	public static final String RETRY_OTP_TRY_AFTER = "Please request otp after";
	public static final String SECONDS = " seconds";
	public static final String OTP_TIME_EXPIRED = "Your OTP time expired";
	
	public static final String REJECTION_REASON_MANDATORY = "Please provide a rejected reason";
	public static final String CLOSURE_ID_NULL = "No Closure details found for the specified user ID";
	public static final String CLOSURE_RESET_SUCCESS = "Closure status reset successfully";
	public static final String CLOSURE_STATUS_EXCEPTION = "Invalid status. status must be 1 or 2.";
	public static final String CLOSURE_WRONG_OTP = "Invalid OTP";
	public static final String APPROVE_SUCCESS_OTP = "OTP verified successfully.";
	public static final String SEND_SUCCESS_OTP = "OTP send successfully.";
	public static final String FAILED = "Failed";
	public static final String COMPLETE_ERROR_MESSAGE = "OTP not verified or approval pending";
	public static final String NO_RECORD_FOUND = "No data found for the specified date range.";
	public static final String INVALID_DATE_FORMAT = "Invalid date format. Please provide the date in the format 'yyyy-MM-dd HH:mm:ss'.";
	public static final String APPROVED = "Your application is already approved";
	public static final String CLOSED = "Your application is closed";
	public static final String NOT_APPROVED = "Your application not yet approved";
	public static final String MAIL_SUCCESS = "Mail send successfully";
}
