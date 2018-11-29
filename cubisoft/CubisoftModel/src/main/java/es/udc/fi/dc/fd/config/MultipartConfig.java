package es.udc.fi.dc.fd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * The Class MultipartConfig. File to configure the way to upload the files into
 * the server.
 */
@Configuration
public class MultipartConfig {

	/**
	 * Multi part resolver. Method to configure the Multipart.
	 *
	 * @return the commons multipart resolver
	 */
	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver multiPartResolver() {

		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(100000);

		return multipartResolver;
	}

}
