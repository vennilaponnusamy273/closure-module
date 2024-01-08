package in.codifi.api.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_Error_log")
public class ErrorLogEntity extends CommonEntity{

	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id")
	private String applicationId;
	
	@Column(name = "class_name")
	private String className;
	
	@Column(name = "method_name")
	private String methodName;
	
	@Column(name = "reason")
	private String reason;
}
