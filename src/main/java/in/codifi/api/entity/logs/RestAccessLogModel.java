package in.codifi.api.entity.logs;

import java.io.Serializable;

import in.codifi.api.entity.CommonEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestAccessLogModel extends CommonEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String applicationId;
	private String uri;
	private String method;
	private String reqBody;
	private String resBody;
}
