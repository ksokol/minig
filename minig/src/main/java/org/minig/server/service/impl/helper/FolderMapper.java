package org.minig.server.service.impl.helper;

import javax.mail.Folder;
import javax.mail.MessagingException;

import org.minig.server.MailFolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class FolderMapper {

	public MailFolder toMailFolder(Folder source) {
		Assert.notNull(source);

		try {
			MailFolder target = new MailFolder();

			setId(target, source);
			setName(target, source);
			setSubscribed(target, source);

			setParent(target, source);
			setPath(target, source);

			return target;
		} catch (MessagingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void setId(MailFolder target, Folder source) {
		target.setId(source.getFullName());
	}

	private void setName(MailFolder target, Folder source) {
		target.setName(source.getName());
	}

	private void setSubscribed(MailFolder target, Folder source) {
		target.setSubscribed(source.isSubscribed());
	}

	private void setParent(MailFolder target, Folder source)
			throws MessagingException {
		if (source.getParent() != null) {
			target.setParentFolderId(source.getParent().getFullName());
		}
	}

	private void setPath(MailFolder target, Folder source)
			throws MessagingException {
		target.setPath(source.getFullName());
	}
}
