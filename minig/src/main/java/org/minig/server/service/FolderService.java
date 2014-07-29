package org.minig.server.service;

import org.minig.server.MailFolder;
import org.minig.server.MailFolderList;

public interface FolderService {

	void createFolderInInbox(String folder);

    MailFolder createFolderInParent(String parent, String folder);

	void updateFolder(MailFolder folder);

	MailFolderList findAll();

	MailFolderList findBySubscribed(Boolean subscribed);

	// TODO : rename
	MailFolderList findByParent(String parent);

	// MailFolderList findByEditable(Boolean editable);

	MailFolder findById(String id);

	void deleteFolder(String id);

}