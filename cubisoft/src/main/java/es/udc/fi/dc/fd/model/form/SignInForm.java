package es.udc.fi.dc.fd.model.form;


import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

public class SignInForm implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@NotEmpty
	private String email;
	
	@NotEmpty
	private String password;

	public SignInForm() {
		super();
	}
	
	public final String getEmail() {
        return email;
    }
	public final String getPassword() {
        return password;
    }
	
	public final void setEmail(final String value) {
        email = value;
    }
	public final void setPassword(final String value) {
        password = value;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SignInForm other = (SignInForm) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SignInForm [email=" + email + ", password=" + password + "]";
	}
}