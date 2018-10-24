package es.udc.fi.dc.fd.service;

public class AlreadyLikedException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyLikedException() {
		super();
	}
	public AlreadyLikedException(String message) {
		super(message);
	}
}
