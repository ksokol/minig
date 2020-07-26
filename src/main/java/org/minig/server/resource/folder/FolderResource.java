package org.minig.server.resource.folder;

import org.minig.server.MailFolder;
import org.minig.server.resource.Id;
import org.minig.server.service.FolderService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static org.minig.MinigConstants.API;

@RestController
public class FolderResource {

    private final FolderService folderService;

    public FolderResource(FolderService folderService) {
        this.folderService = Objects.requireNonNull(folderService, "folderService is null");
    }

    @GetMapping(API + "/folder")
    public List<MailFolder> findBySubscribed(@RequestParam(required = false) Boolean subscribed) {
        return folderService.findBySubscribed(subscribed);
    }

    @GetMapping(API + "/folder/**")
    public MailFolder findById(@Id String id) {
        return folderService.findById(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(API + "/folder")
    public void createFolder(@RequestBody CreateFolderRequest request) {
        folderService.createFolderInInbox(request.getFolder());
    }

    @PostMapping(API + "/folder/**")
    public MailFolder createFolderInParent(@Id String id, @RequestBody CreateFolderRequest request) {
        return folderService.createFolderInParent(id, request.getFolder());
    }

    @PutMapping(API + "/folder/**")
    public void updateFolder(@Id String id, @RequestBody MailFolder folder) {
        folder.setId(id);
        folderService.updateFolder(folder);
    }

    @DeleteMapping(API + "/folder/**")
    public void deleteFolder(@Id String id) {
        folderService.deleteFolder(id);
    }
}
