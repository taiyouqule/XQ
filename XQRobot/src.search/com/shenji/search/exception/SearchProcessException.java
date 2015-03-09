/**
 * 
 */
package com.shenji.search.exception;

/**
 * @author zhq
 * 
 */
public class SearchProcessException extends Exception {
	private Enum<ErrorCode> errorCode = null;
	public final static String separator =" # ";

	public Enum<ErrorCode> getErrorCode() {
		return errorCode;
	}
	

	public void setErrorCode(Enum<ErrorCode> errorCode) {
		this.errorCode = errorCode;
	}

	public static enum ErrorCode {
		LuceneFileError(401),SearchDocError(402),UnKnowError(403),SynEngineError(405),NoSearchResult(406),SearchResultToArrayError(407);
		private int value;

		private ErrorCode(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static ErrorCode valueOf(int value) throws IllegalArgumentException{ // 手写的从int到enum的转换函数
			for (ErrorCode t : ErrorCode.values()) {
				if (t.value() == value) {
					return t;
				}
			}
			throw new IllegalArgumentException("Enum ErrorCode Argument is Error!");
		}
	};

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public SearchProcessException(Enum<ErrorCode> code) {
		// TODO Auto-generated constructor stub
		this.errorCode = code;
	}

	/**
	 * @param message
	 */
	public SearchProcessException(String message, Enum<ErrorCode> code) {
		super(message);
		this.errorCode = code;
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public SearchProcessException(Throwable cause, Enum<ErrorCode> code) {
		super(cause);
		this.errorCode = code;
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SearchProcessException(String message, Throwable cause,
			Enum<ErrorCode> code) {
		super(message,cause);
		this.errorCode = code;	
		//"["+((ErrorCode)code).value()+"]"+
		// TODO Auto-generated constructor stub
	}

}
