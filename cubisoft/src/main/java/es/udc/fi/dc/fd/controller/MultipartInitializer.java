package es.udc.fi.dc.fd.controller;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

@ControllerAdvice
public class MultipartInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	private static final String LOCATION = "/tmp";
	private static final long MAX_FILE_SIZE = 1024 * 1024 * 25;// 25MB
	private static final long MAX_REQUEST_SIZE = 1024 * 1024 * 30;// 30MB
	private static final int FILE_SIZE_THRESHOLD = 0;

	@Override
	protected Class<?>[] getRootConfigClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String[] getServletMappings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
		registration.setMultipartConfig(getMultipartConfigElement());
	}

	private MultipartConfigElement getMultipartConfigElement() {
		MultipartConfigElement multipartConfigElement = new MultipartConfigElement(LOCATION, MAX_FILE_SIZE,
				MAX_REQUEST_SIZE, FILE_SIZE_THRESHOLD);
		return multipartConfigElement;
	}
}
