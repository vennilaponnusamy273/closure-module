package in.codifi.api.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Holding {

	 private String isin;
     private String realizedPnl;
     private String unrealizedPnl;
     private String netPnl;
     private String netQty;
     private String buyPrice;
     private String holdQty;
     private String dpQty;
     private String benQty;
     private String unpledgedQty;
     private String collateralQty;
     private String brkCollQty;
     private String btstQty;
     private String usedQty;
     private String tradedQty;
     private String sellableQty;
     private String authQty;
     private boolean authFlag;
     private String sellAmount;
     private List<Symbol> symbol;
}
