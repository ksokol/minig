package org.minig.server;

import java.util.ArrayList;
import java.util.List;

public class MailFolderList {

	private List<MailFolder> folderList;

	public MailFolderList(List<MailFolder> folderList) {
		this.folderList = folderList;
	}

	public MailFolderList() {
		this.folderList = new ArrayList<MailFolder>();
	}

	public List<MailFolder> getFolderList() {
		return folderList;
	}

	public void setFolderList(List<MailFolder> folderList) {
		this.folderList = folderList;
	}

}
