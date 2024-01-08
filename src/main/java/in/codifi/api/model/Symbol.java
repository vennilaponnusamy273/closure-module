package in.codifi.api.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Symbol {

	 private String exchange;
     private String token;
     private String tradingSymbol;
     private String pdc;
     private String ltp;
}
