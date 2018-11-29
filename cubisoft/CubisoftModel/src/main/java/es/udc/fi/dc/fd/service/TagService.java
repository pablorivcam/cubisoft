package es.udc.fi.dc.fd.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import es.udc.fi.dc.fd.model.persistence.Tag;
import es.udc.fi.dc.fd.repository.TagRepository;

@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TagService {

	@Autowired
	TagRepository tagRepository;

	/**
	 * Find tags by keywords.
	 *
	 * @param keywords
	 *            the keywords
	 * @return the list of tags found.
	 */
	public List<Tag> findTagsByKeywords(String keywords) {
		return tagRepository.findTagsByKeywords(keywords);
	}

}
