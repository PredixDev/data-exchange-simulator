package com.ge.predix.solsvc.simulator.types;

/**
 * 
 * @author predix -
 */
public class DataSimulatorResponse {
	/**
	 * 
	 */
	String responseCode;
	/**
	 * 
	 */
	String responseString;
	/**
	 * 
	 */
	Boolean isError = Boolean.TRUE;
	/**
	 * 
	 */
	String errorMessage;

	/**
	 * @return -
	 */
	public String getResponseCode() {
		return this.responseCode;
	}

	/**
	 * @param responseCode -
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return -
	 */
	public String getResponseString() {
		return this.responseString;
	}

	/**
	 * @param responseString -
	 */
	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	/**
	 * @return -
	 */
	public Boolean getIsError() {
		return this.isError;
	}

	/**
	 * @param isError -
	 */
	public void setIsError(Boolean isError) {
		this.isError = isError;
	}

	/**
	 * @return -
	 */
	public String getErrorMessage() {
		return this.errorMessage;
	}

	/**
	 * @param errorMessage -
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
