package in.codifi.api.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResponseModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int stat;
	private String page;
	private String message;
	private String reason;
	private Object result;
	private Object Address_response;
	private Object rejectionUser;
	
}
