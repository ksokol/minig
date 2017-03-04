package org.minig.server.service;

import org.minig.server.MailFolder;
import org.minig.server.MailFolderList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private MailRepository mailRepository;

    public void createFolderInInbox(String folder) {
        createFolderInParent(null, folder);
    }

    public MailFolder createFolderInParent(String parent, String folder) {
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

        return folderRepository.create(parentFolder.getId(), folder);
    }

    public MailFolderList findAll() {
        List<MailFolder> l = folderRepository.findAll();

        for (MailFolder mf : l) {
            boolean writable = permissionService.writable(mf);
            mf.setEditable(writable);
        }

        return new MailFolderList(l);
    }

    public MailFolderList findBySubscribed(Boolean subscribed) {
        List<MailFolder> l = folderRepository.findBySubscribed(subscribed);

        for (MailFolder mf : l) {
            boolean writable = permissionService.writable(mf);
            mf.setEditable(writable);
        }

        return new MailFolderList(l);
    }

    public MailFolderList findByParent(String parent) {

        List<MailFolder> findChildren = folderRepository.findChildren(parent);

        return new MailFolderList(findChildren);
    }

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

    public MailFolder findById(String id) {
        Assert.hasLength(id);

        return folderRepository.read(id);
    }

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

}
