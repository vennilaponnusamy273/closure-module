package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReKycResmodel {

	
	private boolean holdings;
	private boolean funds;
	private boolean positions;
	private String holdings_remarks;
	private String funds_remarks;
	private String positions_remarks;
	private boolean cmrCopy;
}
