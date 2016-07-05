package org.minig.server.service.impl;

import java.util.Arrays;
import java.util.List;

import org.minig.server.MailFolder;
import org.minig.server.service.PermissionService;
import org.springframework.stereotype.Component;

@Component
class NaivePermissionService implements PermissionService {

	private final List<String> notWritable = Arrays.asList("INBOX",
			"INBOX/Trash", "INBOX/Sent", "INBOX/Drafts", "INBOX.Trash",
			"INBOX.Sent", "INBOX.Drafts");

	@Override
	public boolean writable(MailFolder folder) {
		return !notWritable.contains(folder.getId());
	}

}
