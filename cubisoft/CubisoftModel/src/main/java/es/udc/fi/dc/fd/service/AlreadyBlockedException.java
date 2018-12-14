package es.udc.fi.dc.fd.service;

public class AlreadyBlockedException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyBlockedException() {
		super();
	}
	public AlreadyBlockedException(String message) {
		super(message);
	}
}
