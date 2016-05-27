package org.minig.server.service;

import java.util.List;

import org.minig.server.MailFolder;

public interface FolderRepository {

	MailFolder getInbox();

	MailFolder getTrash();

	MailFolder getDraft();

	MailFolder getSent();

	List<MailFolder> findAll();

	List<MailFolder> findBySubscribed(Boolean subscribed);

	List<MailFolder> findChildren(String id);

	MailFolder create(String parent, String folder);

	MailFolder read(String folder);

	void update(MailFolder folder);

	void delete(String id);
}
