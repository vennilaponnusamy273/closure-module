package in.codifi.api.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JsonResponse {

	private String status;
    private String message;

    @JsonProperty("result")
    private ClientBasicData[] result;
}
