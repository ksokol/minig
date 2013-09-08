package org.minig.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

import org.minig.server.MailFolder;
import org.minig.server.service.FolderRepository;
import org.minig.server.service.RepositoryException;
import org.minig.server.service.impl.helper.FolderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
class FolderRepositoryImpl implements FolderRepository {

	@Autowired
	private MailContext mailContext;

	@Autowired
	private FolderMapper folderMapper;

	@Override
	public List<MailFolder> findAll() {
		return findBySubscribed(null);
	}

	@Override
	public List<MailFolder> findBySubscribed(Boolean subscribed) {
		try {
			Store store = mailContext.getStore();
			List<MailFolder> folderList = new ArrayList<MailFolder>();

			for (Folder folder : store.getDefaultFolder().list("*")) {
				if (subscribed == null) {
					MailFolder mailFolder = folderMapper.toMailFolder(folder);
					folderList.add(mailFolder);
				} else {
					if (subscribed.equals(folder.isSubscribed())) {
						MailFolder mailFolder = folderMapper
								.toMailFolder(folder);
						folderList.add(mailFolder);
					}
				}
			}

			return folderList;
		} catch (MessagingException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	@Override
	public List<MailFolder> findChildren(String id) {
		Assert.hasLength(id, "id is null");

		try {
			Store store = mailContext.getStore();
			List<MailFolder> folderList = new ArrayList<MailFolder>();

			for (Folder folder : store.getFolder(id).list("%")) {
				MailFolder mailFolder = folderMapper.toMailFolder(folder);

				if (id.equals(mailFolder.getParentFolderId())) {
					folderList.add(mailFolder);
				}
			}

			return folderList;
		} catch (MessagingException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	@Override
	public MailFolder create(String parent, String folder) {
		Assert.hasText(parent, "parent must not be null");
		Assert.hasText(folder, "folder must not be null");

		try {
			Folder parentFolder = mailContext.getFolder(parent);

			if (!parentFolder.exists()) {
				throw new RepositoryException("parent does not exist");
			}

			Folder testTwo = mailContext.getFolder(parentFolder.getFullName(),
					folder);

			if (!testTwo.exists()) {
				boolean isCreated = true;

				Folder newFolder = parentFolder.getFolder(folder);
				isCreated = newFolder.create(Folder.HOLDS_MESSAGES);

				if (isCreated) {
					newFolder.setSubscribed(true);
				} else {
					throw new RepositoryException("could not create folder");
				}

				return folderMapper.toMailFolder(newFolder);
			} else {
				return folderMapper.toMailFolder(testTwo);
			}
		} catch (RepositoryException e) {
			throw e;
		} catch (MessagingException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	@Override
	public MailFolder read(String folder) {
		Assert.hasText(folder, "folder is null");

		try {
			Folder storeFolder = mailContext.getFolder(folder);

			if (storeFolder.exists()) {
				return folderMapper.toMailFolder(storeFolder);
			} else {
				return null;
			}
		} catch (MessagingException e) {
			throw new RepositoryException(e.getMessage(), e);
		}
	}

	@Override
	public MailFolder getInbox() {
		return folderMapper.toMailFolder(mailContext.getInbox());
	}

	@Override
	public MailFolder getTrash() {
		Folder trash = mailContext.getTrash();
		return folderMapper.toMailFolder(trash);
	}

	@Override
	public MailFolder getDraft() {
		Folder draft = mailContext.getDraft();
		return folderMapper.toMailFolder(draft);
	}

	@Override
	public MailFolder getSent() {
		Folder sent = mailContext.getSent();
		return folderMapper.toMailFolder(sent);
	}

	@Override
	public void update(MailFolder source) {
		Assert.notNull(source, "folder is null");
		Assert.notNull(source.getId(), "folder.id is null");

		if (source.getSubscribed() != null) {
			Folder oldFolder = mailContext.getFolder(source.getId(), true);

			try {
				oldFolder.setSubscribed(source.getSubscribed());
			} catch (MessagingException e) {
				throw new RepositoryException(e.getMessage(), e);
			}
		}
	}

	@Override
	public void delete(String id) {
		Assert.hasText(id, "id must not be null");

		boolean deletionFailure = false;

		try {
			Folder storeFolder = mailContext.getFolder(id, false);

			if (storeFolder.exists()) {
				storeFolder.setSubscribed(false);
				deletionFailure = !storeFolder.delete(true);
			}
		} catch (MessagingException e) {
			throw new RepositoryException(e.getMessage(), e);
		}

		if (deletionFailure) {
			throw new RepositoryException("delete failed");
		}
	}
}
