package org.minig.server.resource.folder;

import org.minig.server.MailFolder;
import org.minig.server.MailFolderList;
import org.minig.server.resource.Id;
import org.minig.server.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "1", produces = "application/json; charset=UTF-8")
public class FolderResource {

    @Autowired
    private FolderService folderService;

    @RequestMapping(value = "folder", method = RequestMethod.GET)
    @ResponseBody
    public MailFolderList findBySubscribed(@RequestParam(required = false) Boolean subscribed) {
        return folderService.findBySubscribed(subscribed);
    }

    @RequestMapping(value = "folder/**", method = RequestMethod.GET)
    @ResponseBody
    public MailFolder findById(@Id String id) {
        return folderService.findById(id);
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "folder", method = RequestMethod.POST)
    @ResponseBody
    public void createFolder(@RequestBody CreateFolderRequest request) {
        folderService.createFolderInInbox(request.getFolder());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "folder/**", method = RequestMethod.POST)
    @ResponseBody
    public MailFolder createFolderInParent(@Id String id, @RequestBody CreateFolderRequest request) {
        return folderService.createFolderInParent(id, request.getFolder());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "folder/**", method = RequestMethod.PUT)
    @ResponseBody
    public void updateFolder(@Id String id, @RequestBody MailFolder folder) {
        folder.setId(id);
        folderService.updateFolder(folder);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "folder/**", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteFolder(@Id String id) {
        folderService.deleteFolder(id);
    }
}
