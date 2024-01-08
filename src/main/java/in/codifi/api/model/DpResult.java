package in.codifi.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DpResult {

	@JsonProperty("clientCode")
    private String clientCode;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("dpId")
    private String dpId;

    @JsonProperty("dpCode")
    private String dpCode;

    @JsonProperty("clDefault")
    private String clDefault;

    @JsonProperty("poa")
    private String poa;
}
