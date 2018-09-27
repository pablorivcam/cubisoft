package es.udc.fi.dc.fd.controller.account;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import es.udc.fi.dc.fd.controller.entity.ExampleEntityViewConstants;
import es.udc.fi.dc.fd.model.form.ExampleEntityForm;
import es.udc.fi.dc.fd.model.form.SignInForm;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.service.UserProfileService;

@Controller
@RequestMapping("/account")
public class SignInController {
	

	@Autowired
	private UserProfileService userProfileService;

	public SignInController(final UserProfileService service) {
		
		userProfileService = checkNotNull(service, "Received a null pointer as service");

	}
	
	private final UserProfileService getUserProfileService() {
		return userProfileService;
	}

	
	/**
	 * Shows the page when the "get" petition is done.
	 */
	@GetMapping(path = "/signin")
	public final String showSignInView(final ModelMap model) {
		model.addAttribute(new SignInForm());
		return SignInViewConstants.VIEW_SIGNIN_FORM;
	}
	
	@PostMapping(path ="/signinuser")
	public final String signInProcess(@ModelAttribute SignInForm signInForm,
			final HttpServletResponse response) {
		
		UserProfile user = userProfileService.validateUser(signInForm.getEmail(),signInForm.getPassword());
		
		if(user==null){
			String path = SignInViewConstants.VIEW_SIGNIN_FORM;
			// Marks the response as a bad request
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			
			return path;
		}
		else{
			return "welcome";
		}
	}
}

