package es.udc.fi.dc.fd.controller.account;

import static com.google.common.base.Preconditions.checkNotNull;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.udc.fi.dc.fd.model.form.SignUpForm;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.service.UserProfileService;

@Controller
@RequestMapping("/account")
public class SignUpController {

	private final UserProfileService userProfileService;

	public SignUpController(final UserProfileService service) {
		userProfileService = checkNotNull(service, "Received a null pointer as service");
	}

	private final UserProfileService getUserProfileService() {
		return userProfileService;
	}

	/**
	 * Shows the page when the "get" petition is done.
	 */
	@GetMapping(path = "/signup")
	public final String showSingnUpView(final ModelMap model) {
		model.addAttribute(new SignUpForm());
		return AccountViewConstants.VIEW_SIGNUP;
	}

	/**
	 * TODO arreglar este método para que me mande a la página de login una vez
	 * registrado, o me salga un error si algunno de los campos no es correcto
	 **/
	@PostMapping(path = "/signupUser")
	public String saveUser(@ModelAttribute SignUpForm signUpForm/* , ModelMap modelMap */) {

		UserProfile userProfile = new UserProfile();

		userProfile.setLogin(signUpForm.getLogin());
		userProfile.setFirstName(signUpForm.getName());
		userProfile.setLastName(signUpForm.getSurname());
		userProfile.setPassword(signUpForm.getPassword());
		userProfile.setEmail(signUpForm.getEmail());

		getUserProfileService().save(userProfile);

		// modelMap.addAttribute(new SignUpForm());
		// return SignUpViewConstants.VIEW_SIGNUP;
		// FIXME change this to the login url path when finished
		return "welcome";
	}

}
