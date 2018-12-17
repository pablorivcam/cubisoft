package es.udc.fi.dc.fd.controller.form;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

/**
 * The Class MultipartFileValidator. Class to validate the MultipartFiles on the
 * forms.
 */
public class MultipartFileValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return MultipartFile.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		MultipartFile multipartFile = (MultipartFile) target;

		if (multipartFile.getSize() <= 0) {
			errors.rejectValue("pictureFile", "MissingFile.field");
		}
	}

}
