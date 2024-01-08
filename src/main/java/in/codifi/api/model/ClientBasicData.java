package in.codifi.api.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientBasicData {

		private String ownCode;
	    private String termCode;
	    private String brCode;
	    private String dealerCode;
	    private String relationshipCode;
	    private String teamLeader;
	    private String address1;
	    private String address2;
	    private String address3;
	    private String city;
	    private String state;
	    private String pincode;
	    private String country;
	    private String tel1;
	    private String tel2;
	    private String tel3;
	    private String fax;
	    private String mobile;
	    private String pangir;
	    private String corraddress1;
	    private String corraddress2;
	    private String corraddress3;
	    private String corrcity;
	    private String corrstate;
	    private String corrPin;
	    private String corrCountry;
	    private String occupation;
	    private String gender;
	    private String maritalStatus;
	    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	    private Date dob;
	    private String uniqueIdentification;
	    private String gstno;
	    private String status;
	    private String authorizationType;
	    private String email;
	    private String emailcc;
	    private String active;
	    private String uccClientCategory;
	    private String nameAsperPan;
	    private String fatherSpouseFlag;
	    private String fatherhusbandname;
	    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	    private Date accountOpenDT;
	    private String pep;
	    private String nincome;
	    private String networth;
	    private String nom1;
	    private String nom2;
	    private String nom3;
	    private String introCode;
	    private String firstName;
	    private String middleName;
	    private String lastName;
	    private String emailbc;
	    private String prefix;
	    private String sebiMtf;
}
