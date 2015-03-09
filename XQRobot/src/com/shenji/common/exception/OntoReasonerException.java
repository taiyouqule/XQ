/**
 * 
 */
package com.shenji.common.exception;

/**
 * @author zhq
 * 
 */
public class OntoReasonerException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Enum<ErrorCode> errorCode = null;

	public Enum<ErrorCode> getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Enum<ErrorCode> errorCode) {
		this.errorCode = errorCode;
	}

	public static enum ErrorCode {
		UserTreeIsNull;
	};

	/**
	 * 
	 */
	public OntoReasonerException(ErrorCode code) {
		// TODO Auto-generated constructor stub
		this.errorCode = code;
	}

	/**
	 * @param message
	 */
	public OntoReasonerException(String message, ErrorCode code) {
		super(message);
		this.errorCode = code;
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public OntoReasonerException(Throwable cause, ErrorCode code) {
		super(cause);
		this.errorCode = code;
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OntoReasonerException(String message, Throwable cause, ErrorCode code) {
		super(message, cause);
		this.errorCode = code;
		// TODO Auto-generated constructor stub
	}

}
