package org.minig.server.service;

import org.minig.server.MailFolder;

public interface PermissionService {

	boolean writable(MailFolder folder);

}
