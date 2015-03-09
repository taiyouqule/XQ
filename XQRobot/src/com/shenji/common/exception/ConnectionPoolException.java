/**
 * 
 */
package com.shenji.common.exception;

/**
 * @author zhq
 *
 */
public class ConnectionPoolException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public ConnectionPoolException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public ConnectionPoolException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public ConnectionPoolException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ConnectionPoolException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

}
