
package com.eot.banking.dto;

import org.codehaus.jackson.annotate.JsonProperty;

public class ResponseMessage {
	
    @JsonProperty("transactionref")
    private String transactionref;
    @JsonProperty("message")
    private String message;
    @JsonProperty("receiver")
    private String receiver;
    private Integer validation_code;
    @JsonProperty("amount")
    private String amount;
    
    private String commissioncurrency;
    private Integer commissionamount;

    @JsonProperty("transactionref")
    public String getTransactionref() {
        return transactionref;
    }

    @JsonProperty("transactionref")
    public void setTransactionref(String transactionref) {
        this.transactionref = transactionref;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("receiver")
    public String getReceiver() {
        return receiver;
    }

    @JsonProperty("receiver")
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    @JsonProperty("amount")
    public String getAmount() {
        return amount;
    }

    @JsonProperty("amount")
    public void setAmount(String amount) {
        this.amount = amount;
    }

	public Integer getValidation_code() {
		return validation_code;
	}

	public void setValidation_code(Integer validation_code) {
		this.validation_code = validation_code;
	}

	public String getCommissioncurrency() {
		return commissioncurrency;
	}

	public void setCommissioncurrency(String commissioncurrency) {
		this.commissioncurrency = commissioncurrency;
	}



	public Integer getCommissionamount() {
		return commissionamount;
	}

	public void setCommissionamount(Integer commissionamount) {
		this.commissionamount = commissionamount;
	}

	@Override
	public String toString() {
		return "ResponseMessage [transactionref=" + transactionref + ", message=" + message + ", receiver=" + receiver + ", validation_code=" + validation_code + ", amount=" + amount + ", commissioncurrency=" + commissioncurrency + ", commissionamount=" + commissionamount + "]";
	}

	
}
