package es.udc.fi.dc.fd.controller.account;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SignInController {

	/**
	 * Shows the page when the "get" petition is done.
	 */
	@GetMapping("signin")
	public final String showSignInView(final Model model,
			@RequestParam(name = "error", required = false, defaultValue = "false") final Boolean error) {

		model.addAttribute("error", error);

		return SignInViewConstants.VIEW_SIGNIN_FORM;
	}

	// @PostMapping("authenticate")
	// String authenticate(Errors errors, RedirectAttributes ra) {
	// return "redirect:/";

	// }

}
