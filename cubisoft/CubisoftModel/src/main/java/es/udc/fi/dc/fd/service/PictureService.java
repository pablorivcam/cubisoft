package es.udc.fi.dc.fd.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceNotFoundException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.udc.fi.dc.fd.model.form.UploadPictureForm;
import es.udc.fi.dc.fd.model.persistence.Picture;
import es.udc.fi.dc.fd.model.persistence.PictureTag;
import es.udc.fi.dc.fd.model.persistence.Post;
import es.udc.fi.dc.fd.model.persistence.Tag;
import es.udc.fi.dc.fd.model.persistence.UserProfile;
import es.udc.fi.dc.fd.repository.PictureRepository;
import es.udc.fi.dc.fd.repository.PictureTagRepository;
import es.udc.fi.dc.fd.repository.TagRepository;
import es.udc.fi.dc.fd.repository.UserProfileRepository;

/**
 * The Class PictureService.
 */
@Service
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PictureService {

	/** The picture repository. */
	@Autowired
	private PictureRepository pictureRepository;

	@Autowired
	private UserProfileRepository userProfileRepository;

	@Autowired
	private TagRepository tagRepository;

	@Autowired
	private PictureTagRepository pictureTagRepository;

	@Autowired
	private PostService postService;

	private final static Logger logger = Logger.getLogger(PictureService.class.getName());

	/**
	 * Save a picture into the database.
	 *
	 * @param picture
	 *            the picture
	 * @return the picture
	 */
	@Transactional
	public Picture save(Picture picture) {
		return pictureRepository.save(picture);
	}

	/**
	 * Gets the pictures by author.
	 *
	 * @param author
	 *            the author
	 * @return the pictures by author
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	public List<Picture> getPicturesByAuthor(UserProfile author) throws InstanceNotFoundException {

		if (author == null) {
			throw new NullPointerException("The author param cannot be null.");
		}

		if (!userProfileRepository.exists(author.getEmail())) {
			throw new InstanceNotFoundException("The user with the email" + author.getEmail() + " doestn exist.");
		}

		return pictureRepository.findPicturesByAuthor(author);

	}

	/**
	 * Gets the pictures by description.
	 *
	 * @param description
	 *            the description
	 * @return the pictures by description
	 */
	public List<Picture> getPicturesByDescription(String description) {

		return pictureRepository.findPicturesByDescription(description);

	}

	/**
	 * Deletes a picture.
	 *
	 * @param picture
	 *            the picture
	 */
	@Transactional
	public void delete(Picture picture) {
		pictureRepository.delete(picture);
	}

	/**
	 * Modifies a picture description.
	 *
	 * @param modifyId
	 *            the modify id
	 * @param description
	 *            the description
	 * @return the picture
	 * @throws InstanceNotFoundException
	 *             the instance not found exception
	 */
	@Transactional(rollbackFor = InstanceNotFoundException.class)
	public Picture modifyPictureDescription(Long modifyId, String description) throws InstanceNotFoundException {

		if (!pictureRepository.existsById(modifyId)) {
			throw new InstanceNotFoundException("The picture with that id doesn't exist.");
		}

		Picture p = pictureRepository.findById(modifyId).get();

		// FIXME: de esta manera si modificamos una imagen desde el perfil de
		// otro
		// usuario nos lleva al nuestro.

		// Modificamos la descripcion de la imagen en la BD
		// pictureService.modifyPicture(p, pictureDescription);
		p.setDescription(description);

		setPictureTags(p, getDescriptionHashtags(description));

		return save(p);
	}

	/**
	 * Sets the tags of a Picture.
	 *
	 * @param picture
	 *            the picture
	 * @param tags_text
	 *            the text of the tags that we want to add to the image.
	 * @return the picture with the new tags
	 */
	@Transactional()
	public Picture setPictureTags(Picture picture, List<String> tags_text) {

		List<PictureTag> pictureTags = new ArrayList<>();

		// Deletion of the existent pictureTags at the picture
		if (picture.getPicture_tags() != null) {
			for (PictureTag imageTag : picture.getPicture_tags()) {
				pictureTagRepository.delete(imageTag);
			}
		}
		for (String tag_text : tags_text) {

			String aux_tag_text = tag_text.substring(1, tag_text.length());

			// Creation of the new tags if they dont exist.
			if (!tagRepository.existsByText(aux_tag_text)) {
				tagRepository.save(new Tag(aux_tag_text, new ArrayList<>()));
			}

			PictureTag pictureTag = new PictureTag(picture, tagRepository.findTagByText(aux_tag_text));

			pictureTagRepository.save(pictureTag);
			// System.out.println("Esta wea sigue siendo: " + pictureTag + " porque encontre
			// " + pictureTag.getTag()
			// + " con imagen " + pictureTag.getPicture());

			// System.out.println(pictureTag.getTag().getText());

			pictureTags.add(pictureTag);
		}

		picture.setPicture_tags(pictureTags);
		return pictureRepository.save(picture);
	}

	/**
	 * Method that uploads a file into the server.
	 *
	 * @param uploadPictureForm
	 *            the upload picture form that contains the form data. @param
	 *            userAuthenticated the user that is the author of the image @param
	 *            folderPath the destiny folder path @return the multipart file
	 *            generated by the method @throws
	 * @param userAuthenticated
	 *            the user authenticated
	 * @param folderPath
	 *            the folder path
	 * @return the multipart file
	 */
	@Transactional(noRollbackFor = IOException.class)
	public MultipartFile uploadPicture(UploadPictureForm uploadPictureForm, Principal userAuthenticated,
			String folderPath) {

		MultipartFile file = uploadPictureForm.getPictureFile();

		InputStream inputStream = null;
		OutputStream outputStream = null;

		// Empty file comprobation
		if (!file.isEmpty()) {
			try {

				// Creation of the resources folder
				File folder = new File(folderPath);
				if (!folder.exists()) {
					if (!folder.mkdirs()) {
						System.out.println("Failed to create new directory");
					}
				}

				// Creation of the new file to save the image
				String originalFileName = file.getOriginalFilename();
				String fileName = FilenameUtils.removeExtension(originalFileName);
				String extension = FilenameUtils.getExtension(originalFileName);
				String finalFileName = fileName + "." + extension;

				inputStream = file.getInputStream();
				File newFile = new File(folderPath + "/" + finalFileName);

				// If the file exists we asign a version to it
				int version = 1;
				while (newFile.exists()) {
					finalFileName = fileName + version + "." + extension;
					newFile = new File(folderPath + "/" + finalFileName);
					version++;
				}

				if (!newFile.createNewFile()) {
					System.out.println("Failed to create new file");
				}
				System.out.println("" + finalFileName);

				// Save the image into the file
				outputStream = Files.newOutputStream(newFile.toPath());
				int read = 0;
				byte[] bytes = new byte[1024];

				while ((read = inputStream.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}

				outputStream.close();
				System.out.println("Archivo guardado en: " + newFile.getAbsolutePath());

				// Obtenemos el autor asociado
				UserProfile author = userProfileRepository.findOneByEmail(userAuthenticated.getName());

				// Guardamos todo en la base de datos
				Picture p = new Picture(uploadPictureForm.getDescription(), Calendar.getInstance(), finalFileName,
						author, null);

				p = save(p);

				setPictureTags(p, getDescriptionHashtags(p.getDescription()));

				Post post = new Post(Calendar.getInstance(), p, author, (long) 0, (long) 0, (long) 0, false);

				postService.save(post);

			} catch (IOException e) {
				logger.log(Level.INFO, e.getMessage(), e);
			}

		}

		return file;
	}

	/**
	 * Gets the pictures by hashtags.
	 *
	 * @param hashtags
	 *            the hashtags text
	 * @return the pictures found with that hashtags
	 */
	public List<Picture> getPicturesByHashtags(String[] hashtags) {

		List<Tag> tags = new ArrayList<>();

		for (String hashtag : hashtags) {

			if (tagRepository.existsByText(hashtag)) {
				tags.add(tagRepository.findTagByText(hashtag));
			}
		}

		if (tags.size() == 0) {
			return new ArrayList<Picture>();
		}

		return pictureRepository.findPicturesByTags(tags);

	}

	public String formatDescriptionToHTML(String description) {

		String result = "<p>";

		for (String word : description.split("\\s+")) {
			if (word.startsWith("#")) {
				result += "<spam class=\"hashtag\">" + word + "</spam> ";
			} else {
				result += word + " ";
			}

		}

		return result += "</p>";

	}

	private List<String> getDescriptionHashtags(String description) {

		ArrayList<String> tags = new ArrayList<>();

		for (String word : description.split("\\s+")) {
			if (word.startsWith("#")) {
				tags.add(word);
			}
		}

		return tags;
	}

}
