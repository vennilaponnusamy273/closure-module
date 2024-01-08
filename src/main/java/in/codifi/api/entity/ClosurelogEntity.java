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
@Entity(name = "tbl_closure_log")
public class ClosurelogEntity extends CommonEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "userId")
	private String userId;
	
	@Column(name = "dpId")
	private String dpId;
	
	@Column(name = "position")
	private boolean position;
	
	@Column(name = "holdings")
	private boolean holdings;
	
	@Column(name = "funds")
	private boolean funds;
	
	@Column(name = "cmrpath")
	private String cmrpath;
	
	@Column(name = "accoutType")
	private String accoutType;
	
	@Column(name = "nsdl")
	private int nsdl;
	
	@Column(name = "csdl")
	private int cdsl;

	
}
