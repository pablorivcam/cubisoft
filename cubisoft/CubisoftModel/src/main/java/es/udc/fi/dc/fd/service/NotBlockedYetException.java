package es.udc.fi.dc.fd.service;

public class NotBlockedYetException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotBlockedYetException() {
		super();
	}
	public NotBlockedYetException(String message) {
		super(message);
	}
}
