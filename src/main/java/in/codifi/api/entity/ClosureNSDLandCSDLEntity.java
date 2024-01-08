package in.codifi.api.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity(name = "tbl_closure_nsdlcsdl_coordinates_chola")
public class ClosureNSDLandCSDLEntity implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "COLUMN_NAMES")
	private String columnNames;

	@Column(name = "COLUMN_TYPE")
	private String columnType;

	@Column(name = "X_COORDINATE")
	private String xCoordinate;

	@Column(name = "Y_COORDINATE")
	private String yCoordinate;

	@Column(name = "PAGE_NO")
	private String pageNo;
	
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "active_status")
	private int activeStatus = 1;
}
