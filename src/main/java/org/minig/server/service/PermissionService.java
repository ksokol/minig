package org.minig.server.service;

import org.minig.server.MailFolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PermissionService {

    private final List<String> notWritable = Arrays.asList("INBOX",
            "INBOX/Trash", "INBOX/Sent", "INBOX/Drafts", "INBOX.Trash",
            "INBOX.Sent", "INBOX.Drafts");

    public boolean writable(MailFolder folder) {
        return !notWritable.contains(folder.getId());
    }

}
