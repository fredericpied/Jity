
package org.jity.referential.dateConstraint;

public class DateException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1034270238243994342L;

	/**
     * Constructs a ServerException with the specified detail message. A detail
     * message is a String that describes this particular exception.
     * @param msg the detail message
     * @param nested the exception or error that caused this exception to be
     *            thrown.
     */
    public DateException(String msg) {
        super(msg);
    }
}
