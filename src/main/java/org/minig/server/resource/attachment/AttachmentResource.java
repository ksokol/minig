package org.minig.server.resource.attachment;

import org.minig.server.MailAttachment;
import org.minig.server.resource.Id;
import org.minig.server.resource.exception.ClientIllegalArgumentException;
import org.minig.server.service.AttachmentService;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;
import org.minig.server.service.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kamill Sokol
 */
@Controller
@RequestMapping(value = "1", produces = "application/json; charset=UTF-8")
public class AttachmentResource {

    @Autowired
    private AttachmentService attachmentService;

    @RequestMapping(value = "attachment/**", method = RequestMethod.GET)
    public ResponseEntity<?> readAttachment(@Id CompositeId id) {
        if (id.getId() == null) {
            throw new NotFoundException();
        }

        if (id instanceof CompositeAttachmentId) {
            MailAttachment findAttachment = attachmentService.findAttachment((CompositeAttachmentId) id);
            ResponseEntity<?> responseEntity = new ResponseEntity<MailAttachment>(findAttachment, HttpStatus.OK);
            return responseEntity;

        }
        if (id instanceof CompositeId) {
            List<MailAttachment> findAttachments = attachmentService.findAttachments(id);
            ResponseEntity<List<MailAttachment>> responseEntity = new ResponseEntity<>(findAttachments, HttpStatus.OK);
            return responseEntity;
        }

        throw new NotFoundException();
    }

    @RequestMapping(value = "attachment/**", produces = "*/*", params = "download=true", method = RequestMethod.GET)
    public void downloadAttachment(@Id CompositeAttachmentId id, HttpServletResponse response) throws IOException {
        MailAttachment attachment = attachmentService.findAttachment(id);

        response.setContentType(attachment.getMime());
        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", attachment.getFileName()));
        attachmentService.readAttachment(id, response.getOutputStream());
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "attachment/**", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> uploadAttachment(@Id CompositeId id, MultipartRequest file) throws IOException {
        if(file.getFileMap().size() > 1) {
            throw new ClientIllegalArgumentException("too many files");
        }

        MultipartFile multipartFile = file.getFileMap().values().iterator().next();
        CompositeId newMailId = attachmentService.addAttachment(id, new MultipartfileDataSource(multipartFile));
        List<MailAttachment> attachments = attachmentService.findAttachments(newMailId);

        Map<String, Object> map = new HashMap<>();
        map.put("id", newMailId);
        map.put("attachments", attachments);
        return map;
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "attachment/**", method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, Object> deleteAttachment(@Id CompositeAttachmentId id) {
        CompositeId newMailId = attachmentService.deleteAttachment(id);
        List<MailAttachment> attachments = attachmentService.findAttachments(newMailId);

        Map<String, Object> map = new HashMap<>();
        map.put("id", newMailId);
        map.put("attachments", attachments);
        return map;
    }
}
