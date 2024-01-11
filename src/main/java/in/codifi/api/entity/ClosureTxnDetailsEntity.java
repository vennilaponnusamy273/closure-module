package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "tbl_closure_txn_details")
public class ClosureTxnDetailsEntity extends ClosureCommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "application_id", nullable = false)
	private String applicationId;

	@Column(name = "txn_id")
	private String txnId;

	@Column(name = "folder_location")
	private String folderLocation;
	
	@Column(name = "dpid")
	private String dpId;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "city")
	private String city;
	
	@Column(name = "emailID")
	private String emailID;
	
	@Column(name = "mobileNo")
	private String mobileNo;

}
