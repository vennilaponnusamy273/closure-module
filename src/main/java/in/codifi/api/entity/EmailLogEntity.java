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
@Entity(name = "tbl_email_Log_details")
public class EmailLogEntity extends CommonEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	
	@Column(name = "email_id")
	private String emailId;
	
	@Lob
	@Column(name = "reqLog")
	private String reqLog;
	
	@Lob
	@Column(name = "reqLogSub")
	private String reqLogSub;
	
	@Lob
	@Column(name = "responseLog")
	private String responseLog;
	
	@Column(name = "logMethod")
	private String logMethod;
}
