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
@Entity(name = "tbl_smslog_details")
public class SmsLogEntity extends CommonEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "mobile_number")
	private Long mobileNo;
	
	@Lob
	@Column(name = "requestLog")
	private String  requestLog;
	
	@Lob
	@Column(name = "responseLog")
	private String responseLog;
	
	@Column(name = "logMethod")
	private String logMethod;
}
