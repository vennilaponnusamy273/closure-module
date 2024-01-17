package in.codifi.api.model;

import java.io.Serializable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.FormParam;

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormDataModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@RestForm("file")
	@Valid
	@NotNull
	private FileUpload file;

	@FormParam(value = "applicationId")
	private String applicationId;

	@FormParam(value = "documentType")
	private String documentType;

	@FormParam(value = "typeOfProof")
	private String typeOfProof;
	
	@FormParam(value = "targetDpID")
	private String targetDpID;
	
	@FormParam(value = "targetRepository")
	private String targetRepository;

}
