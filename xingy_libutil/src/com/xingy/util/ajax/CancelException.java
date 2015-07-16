package com.xingy.util.ajax;

@SuppressWarnings("serial")
public class CancelException  extends Exception{
    /**
     * Constructs a new {@code Exception} that includes the current stack trace.
     * 
     * @since Android 1.0
     */
    public CancelException() {
        super();
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace and the
     * specified detail message.
     * 
     * @param detailMessage
     *            the detail message for this exception.
     * @since Android 1.0
     */
    public CancelException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace, the
     * specified detail message and the specified cause.
     * 
     * @param detailMessage
     *            the detail message for this exception.
     * @param throwable
     *            the cause of this exception.
     * @since Android 1.0
     */
    public CancelException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace and the
     * specified cause.
     * 
     * @param throwable
     *            the cause of this exception.
     * @since Android 1.0
     */
    public CancelException(Throwable throwable) {
        super(throwable);
    }	
}
