package org.jity.server.protocol;

public class ProtocolException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7706917493336460539L;

	/**
     * Constructs a OSJProtocolException with the specified detail message. A detail
     * message is a String that describes this particular exception.
     * @param msg the detail message
     * @param nested the exception or error that caused this exception to be
     *            thrown.
     */
    public ProtocolException(String msg) {
        super(msg);
    }
}
