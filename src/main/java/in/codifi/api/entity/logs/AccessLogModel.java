package in.codifi.api.entity.logs;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Lob;

import in.codifi.api.entity.CommonEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessLogModel  extends CommonEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String applicationId;
	private String uri;
	private String method;
	private String reqId;
	private Timestamp inTime;
	private Timestamp outTime;
	private String domain;
	@Lob
	private String reqBody;
	@Lob
	private String resBody;
	private String userAgent;
	private String deviceIp;
	private String contentType;
	private String session;

}
