package org.minig.server.resource.attachment;

import java.io.IOException;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.minig.server.MailAttachment;
import org.minig.server.MailAttachmentList;
import org.minig.server.resource.Id;
import org.minig.server.service.AttachmentService;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;
import org.minig.server.service.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

@Controller
@RequestMapping(value = "1")
public class AttachmentResource {

    @Autowired
    private AttachmentService attachmentService;

    @RequestMapping(value = "attachment/**", method = RequestMethod.GET)
    public ResponseEntity<?> readAttachment(@Id CompositeId id) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type", "application/json");

        if (id.getId() == null) {
            throw new NotFoundException();
        }

        if (id instanceof CompositeAttachmentId) {
            MailAttachment findAttachment = attachmentService.findAttachment((CompositeAttachmentId) id);
            ResponseEntity<?> responseEntity = new ResponseEntity<MailAttachment>(findAttachment, responseHeaders, HttpStatus.OK);
            return responseEntity;
        }

        if (id instanceof CompositeId) {
            MailAttachmentList findAttachments = attachmentService.findAttachments(id);
            ResponseEntity<MailAttachmentList> responseEntity = new ResponseEntity<MailAttachmentList>(findAttachments, responseHeaders, HttpStatus.OK);
            return responseEntity;
        }

        throw new NotFoundException();
    }

    @RequestMapping(value = "attachment/**", produces = "*/*", params = "download=true", method = RequestMethod.GET)
    public void downloadAttachment(@Id CompositeAttachmentId id, HttpServletResponse response) throws IOException {
        MailAttachment attachment = attachmentService.findAttachment(id);

        response.setContentType(attachment.getMime());
        response.setHeader("Content-Disposition", "attachment; filename=" + attachment.getFileName());
        attachmentService.readAttachment(id, response.getOutputStream());
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "attachment/**", method = RequestMethod.POST)
    @ResponseBody
    public CompositeId uploadAttachment(@Id CompositeId id, MultipartRequest file) throws IOException {
        CompositeId addAttachment = id;
        for (Entry<String, MultipartFile> entry : file.getFileMap().entrySet()) {
            addAttachment = attachmentService.addAttachment(addAttachment, new MultipartfileDataSource(entry.getValue()));
        }
        if (addAttachment != null) {
            return addAttachment;
        }
        throw new NotFoundException();
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "attachment/**", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteAttachment(@Id CompositeAttachmentId id) {

        // List<MultipartFile> list = file.getMultiFileMap().get("myfilename");
        //
        // System.out.println(list.get(0).getBytes());
        // MailAttachment attachment = attachmentService.findAttachment(id);
        //
        // response.setContentType(attachment.getMime());
        // response.setHeader("Content-Disposition", "attachment; filename=" +
        // attachment.getFileName());
        // attachmentService.readAttachment(id, response.getOutputStream());

        return null;
    }
}
