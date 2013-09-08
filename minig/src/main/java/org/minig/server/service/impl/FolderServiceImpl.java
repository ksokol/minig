package org.minig.server.service.impl;

import java.util.List;

import org.minig.server.MailFolder;
import org.minig.server.MailFolderList;
import org.minig.server.service.FolderRepository;
import org.minig.server.service.FolderService;
import org.minig.server.service.MailRepository;
import org.minig.server.service.PermissionService;
import org.minig.server.service.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class FolderServiceImpl implements FolderService {

	@Autowired
	private FolderRepository folderRepository;

	@Autowired
	private PermissionService permissionService;

	@Autowired
	private MailRepository mailRepository;

	@Override
	public void createFolderInInbox(String folder) {
		createFolderInParent(null, folder);
	}

	@Override
	public void createFolderInParent(String parent, String folder) {
		Assert.hasText(folder, "folder must be not null");

		MailFolder parentFolder = null;

		if (parent != null) {
			parentFolder = folderRepository.read(parent);
		}

		if (parentFolder == null) {
			parentFolder = folderRepository.getInbox();
		}

		if (parentFolder == null) {
			throw new ServiceException("no parent folder found");
		}

		folderRepository.create(parentFolder.getId(), folder);
	}

	@Override
	public MailFolderList findAll() {
		List<MailFolder> l = folderRepository.findAll();

		for (MailFolder mf : l) {
			boolean writable = permissionService.writable(mf);
			mf.setEditable(writable);
		}

		return new MailFolderList(l);
	}

	// @Override
	// public MailFolderList findByEditable(Boolean editable) {
	// if (editable == null) {
	// return new MailFolderList();
	// }
	//
	// List<MailFolder> l = new ArrayList<MailFolder>();
	// List<MailFolder> notEditable = new ArrayList<MailFolder>();
	//
	// MailFolder draft = folderRepository.getDraft();
	// MailFolder inbox = folderRepository.getInbox();
	// MailFolder trash = folderRepository.getTrash();
	// MailFolder sent = folderRepository.getSent();
	//
	// notEditable.addAll(Arrays.asList(draft, inbox, trash, sent));
	//
	// // permissionService.writable(folder)
	//
	// List<MailFolder> findAll = folderRepository.findAll();
	//
	// if (Boolean.FALSE.equals(editable)) {
	// l.addAll(notEditable);
	// } else {
	// List<MailFolder> all = folderRepository.findAll();
	//
	// for (MailFolder mf : all) {
	//
	// if (permissionService.writable(mf)) {
	//
	// }
	//
	// if (!notEditable.contains(mf)) {
	// l.add(mf);
	// }
	// }
	// }
	//
	// return new MailFolderList(l);
	// }

	@Override
	public MailFolderList findBySubscribed(Boolean subscribed) {
		List<MailFolder> l = folderRepository.findBySubscribed(subscribed);

		for (MailFolder mf : l) {
			boolean writable = permissionService.writable(mf);
			mf.setEditable(writable);
		}

		return new MailFolderList(l);
	}

	@Override
	public MailFolderList findByParent(String parent) {

		List<MailFolder> findChildren = folderRepository.findChildren(parent);

		return new MailFolderList(findChildren);
	}

	@Override
	public void updateFolder(MailFolder source) {
		Assert.notNull(source, "folder is null");
		Assert.notNull(source.getId(), "folder.id is null");

		MailFolder target = folderRepository.read(source.getId());

		if (target != null) {
			if (source.getSubscribed() != null) {
				target.setSubscribed(source.getSubscribed());
			}

			folderRepository.update(target);

			// moving folder and messages into new parent
			if (source.getParentFolderId() != null
					&& !source.getParentFolderId().equals(
							target.getParentFolderId())) {
				move(source);
			}
		}
	}

	private void move(MailFolder source) {
		MailFolder target = folderRepository.create(source.getParentFolderId(),
				source.getName());
		target.setSubscribed(source.getSubscribed());

		mailRepository.copyMessages(source.getId(), target.getId());

		List<MailFolder> findChildren = folderRepository.findChildren(source
				.getId());

		for (MailFolder mf : findChildren) {
			mf.setParentFolderId(target.getId());
			updateFolder(mf);
		}

		folderRepository.delete(source.getId());
	}

	@Override
	public MailFolder findById(String id) {
		Assert.hasLength(id);

		return folderRepository.read(id);
	}

	@Override
	public void deleteFolder(String id) {
		Assert.hasLength(id);

		MailFolder folderToDelete = folderRepository.read(id);

		if (folderToDelete == null) {
			return;
		}

		MailFolder trash = folderRepository.getTrash();

		if (folderToDelete.getParentFolderId().startsWith(trash.getId())) {
			folderRepository.delete(folderToDelete.getId());
		} else {
			folderToDelete.setParentFolderId(trash.getId());
			updateFolder(folderToDelete);
		}
	}

}
