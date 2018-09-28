package es.udc.fi.dc.fd.controller.account;

import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SignInController {

	/**
	 * Shows the page when the "get" petition is done.
	 */
	@GetMapping("signin")
	public final String showSignInView() {
		return SignInViewConstants.VIEW_SIGNIN_FORM;
	}

	@PostMapping("authenticate")
	String authenticate(Errors errors, RedirectAttributes ra) {
		return "redirect:/";

	}

}
