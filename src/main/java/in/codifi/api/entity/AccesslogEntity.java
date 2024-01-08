package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_access_log")
public class AccesslogEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id")
	private String applicationId;

	@Column(name = "uri")
	private String uri;

	@Column(name = "method")
	private String method;

	@Column(name = "req_id")
	private String reqId;

	@Lob
	@Column(name = "req_body")
	private String reqBody;

	@Lob
	@Column(name = "res_body")
	private String resBody;

	@Column(name = "user_agent")
	private String userAgent;

	@Column(name = "device_ip")
	private String deviceIp;

	@Column(name = "content_type")
	private String contentType;

	@Column(name = "session")
	private String session;

}
