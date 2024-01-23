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
@Entity(name = "tbl_closure_status_logs")
public class ClosureStatusEntity extends ClosureCommonEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "userId")
	private String userId;
	
	@Column(name = "status")
	private int status;
	
	@Column(name = "RejectedReason")
	private String RejectedReason;

}
