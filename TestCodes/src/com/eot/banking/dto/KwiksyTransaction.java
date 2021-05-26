
package com.eot.banking.dto;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;


public class KwiksyTransaction {

    @JsonProperty("responseStatus")
    private String responseStatus;
    @JsonProperty("responseMessage")
    private List<ResponseMessage> responseMessage = new ArrayList<>();
    @JsonProperty("responseCode")
    private String responseCode;

    @JsonProperty("responseStatus")
    public String getResponseStatus() {
        return responseStatus;
    }

    @JsonProperty("responseStatus")
    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    @JsonProperty("responseMessage")
    public List<ResponseMessage> getResponseMessage() {
        return responseMessage;
    }

    @JsonProperty("responseMessage")
    public void setResponseMessage(List<ResponseMessage> responseMessage) {
        this.responseMessage = responseMessage;
    }

    @JsonProperty("responseCode")
    public String getResponseCode() {
        return responseCode;
    }

    @JsonProperty("responseCode")
    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

	@Override
	public String toString() {
		return "KwiksyTransaction [responseStatus=" + responseStatus + ", responseMessage= [" + responseMessage + "], responseCode=" + responseCode + "]";
	}
    
    

}
