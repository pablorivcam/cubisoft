package es.udc.fi.dc.fd.controller.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.udc.fi.dc.fd.model.form.SignUpForm;

@Controller
@RequestMapping("/account")
public class SignUpController {

	/**
	 * Name for the picture form.
	 */
	public static final String VIEW_SIGNUP = "account/signup";

	public SignUpController() {

	}

	/**
	 * Shows the page when the "get" petition is done.
	 */
	@GetMapping(path = "/signup")
	public final String showSingnUpView(final ModelMap model) {
		model.addAttribute(new SignUpForm());
		return VIEW_SIGNUP;
	}

	// TODO: Create method to submit the user sign up anotated with
	// @PostMapping(path="/signupUser")

}
