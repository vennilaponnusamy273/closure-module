package in.codifi.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FundsDetails {

	@JsonProperty("availableMargin")
    private double availableMargin;

    @JsonProperty("openingBalance")
    private double openingBalance;

    @JsonProperty("marginUsed")
    private double marginUsed;

    @JsonProperty("payin")
    private double payin;

    @JsonProperty("stockPledge")
    private double stockPledge;

    @JsonProperty("holdingSellCredit")
    private double holdingSellCredit;

    @JsonProperty("exposure")
    private double exposure;

    @JsonProperty("premium")
    private double premium;

    @JsonProperty("bookedPAndL")
    private double bookedPAndL;

    @JsonProperty("mtmPAndL")
    private double mtmPAndL;

    @JsonProperty("collateral")
    private double collateral;

    @JsonProperty("fundsTranstoday")
    private double fundsTranstoday;

    @JsonProperty("creditForSale")
    private double creditForSale;

    @JsonProperty("totalUtilize")
    private double totalUtilize;

    @JsonProperty("allocationOrWithdrawal")
    private double allocationOrWithdrawal;

    @JsonProperty("netAvailableFunds")
    private double netAvailableFunds;
    
    public boolean isAllFieldsZero() {
        return availableMargin != 0.0 ||
               openingBalance != 0.0 ||
               marginUsed != 0.0 ||
               payin != 0.0 ||
               stockPledge != 0.0 ||
               holdingSellCredit != 0.0 ||
               exposure != 0.0 ||
               premium != 0.0 ||
               bookedPAndL != 0.0 ||
               mtmPAndL != 0.0 ||
               collateral != 0.0 ||
               fundsTranstoday != 0.0 ||
               creditForSale != 0.0 ||
               totalUtilize != 0.0 ||
               allocationOrWithdrawal != 0.0 ||
               netAvailableFunds != 0.0;
    }



}
