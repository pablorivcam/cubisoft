package es.udc.fi.dc.fd.controller.account;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.udc.fi.dc.fd.model.form.SignUpForm;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.model.persistence.UserProfile.UserType;
import es.udc.fi.dc.fd.service.UserProfileService;

@Controller
public class SignUpController {
	@Autowired
	private UserProfileService userProfileService;

	/**
	 * Instantiates a new sign up controller.
	 *
	 * @param service
	 *            the service
	 */
	public SignUpController(final UserProfileService service) {
		userProfileService = checkNotNull(service, "Received a null pointer as service");
	}

	/**
	 * Shows the page when the "get" petition is done.
	 *
	 * @param model
	 *            the model
	 * @return the string
	 */
	@GetMapping("signup")
	public final String showSingnUpView(final ModelMap model) {
		model.addAttribute(new SignUpForm());
		return AccountViewConstants.VIEW_SIGNUP;
	}

	@PostMapping("signup")
	String signup(@Valid @ModelAttribute SignUpForm signupForm, Errors errors, RedirectAttributes ra) {
		if (errors.hasErrors()) {
			return AccountViewConstants.VIEW_SIGNUP;
		}
		UserProfile account = userProfileService.save(new UserProfile(signupForm.getLogin(), signupForm.getName(),
				signupForm.getSurname(), signupForm.getPassword(), signupForm.getEmail(), null, null, UserType.PUBLIC));
		userProfileService.signin(account);
		// see /WEB-INF/i18n/messages.properties and
		// /WEB-INF/views/homeSignedIn.html
		return "redirect:/";
	}
}
