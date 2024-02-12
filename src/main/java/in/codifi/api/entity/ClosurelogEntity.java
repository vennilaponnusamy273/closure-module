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
public class ClosurelogEntity extends ClosureCommonEntity{

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
	
	@Column(name = "nsdl")
	private int nsdl;
	
	@Column(name = "csdl")
	private int cdsl;
	
	@Column(name = "accounttype")
	private int accType=0;
	
	@Column(name = "accclosingreason")
	private String accclosingreasion;
	
	@Column(name = "ownCode")
	private String ownCode;
	@Column(name = "brcode")
	private String brcode;
	@Column(name = "dealerCode")
	private String dealerCode;
	@Column(name = "relationshipCode")
	private String relationshipCode;
	@Column(name = "teamLeader")
	private String teamLeader;
	@Column(name = "address1")
	private String address1;
	
	@Column(name = "address2")
	private String address2;
	@Column(name = "address3")
	private String address3;
	@Column(name = "city")
	private String city;
	@Column(name = "state")
	private String state;
	@Column(name = "pincode")
	private String pincode;
	@Column(name = "country")
	private String country;
	
	
	@Column(name = "mobile")
	private String mobile;
	@Column(name = "pangir")
	private String pangir;
	@Column(name = "corraddress1")
	private String corraddress1;
	@Column(name = "corraddress2")
	private String corraddress2;
	@Column(name = "corraddress3")
	private String corraddress3;
	@Column(name = "corrcity")
	private String corrcity;
	
	
	@Column(name = "corrstate")
	private String corrstate;
	@Column(name = "corrPin")
	private String corrPin;
	@Column(name = "corrCountry")
	private String corrCountry;
	@Column(name = "occupation")
	private String occupation;
	@Column(name = "gender")
	private String gender;
	@Column(name = "maritalStatus")
	private String maritalStatus;
	
	
	
	@Column(name = "dob")
	private String dob;
	@Column(name = "uniqueIdentification")
	private String uniqueIdentification;
	@Column(name = "gstno")
	private String gstno;
	@Column(name = "status")
	private String status;
	@Column(name = "authorizationType")
	private String authorizationType;
	@Column(name = "email")
	private String email;
	
	
	
	@Column(name = "emailcc")
	private String emailcc;
	@Column(name = "active")
	private String active;
	@Column(name = "uccClientCategory")
	private String uccClientCategory;
	@Column(name = "nameAsperPan")
	private String nameAsperPan;
	@Column(name = "fatherSpouseFlag")
	private String fatherSpouseFlag;
	@Column(name = "fatherhusbandname")
	private String fatherhusbandname;
	
	
	
	@Column(name = "accountOpenDT")
	private String accountOpenDT;
	@Column(name = "pep")
	private String pep;
	@Column(name = "nincome")
	private String nincome;
	@Column(name = "networth")
	private String networth;
	@Column(name = "nom1")
	private String nom1;
	@Column(name = "nom2")
	private String nom2;
	
	
	@Column(name = "nom3")
	private String nom3;
	@Column(name = "introCode")
	private String introCode;
	@Column(name = "firstName")
	private String firstName;
	@Column(name = "middleName")
	private String middleName;
	@Column(name = "lastName")
	private String lastName;
	@Column(name = "emailbc")
	private String emailbc;
	
	@Column(name = "prefix")
	private String prefix;
	@Column(name = "sebiMtf")
	private String sebiMtf;
	
	@Column(name = "targetDpID")
	private String targetDpID;
	
	@Column(name = "targetRepository")
	private String targetRepository;

	@Column(name = "adminstatus")
	private int adminstatus=0;
	
	@Column(name = "RejectedReason")
	private String RejectedReason;
	
}
