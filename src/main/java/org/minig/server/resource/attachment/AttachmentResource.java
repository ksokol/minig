package org.minig.server.resource.attachment;

import org.apache.commons.io.IOUtils;
import org.minig.server.MailAttachment;
import org.minig.server.resource.Id;
import org.minig.server.resource.exception.ClientIllegalArgumentException;
import org.minig.server.service.AttachmentService;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.CompositeId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL_VALUE;

/**
 * @author Kamill Sokol
 */
@Controller
@RequestMapping(value = "1")
public class AttachmentResource {

    private final AttachmentService attachmentService;

    public AttachmentResource(AttachmentService attachmentService) {
        this.attachmentService = requireNonNull(attachmentService, "attachmentService is null");
    }

    @GetMapping(value = "attachment/{id:.*}", produces = ALL_VALUE)
    public ResponseEntity<?> downloadAttachment(@Id CompositeAttachmentId id) throws IOException {
        MailAttachment attachment = attachmentService.findById(id);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(CONTENT_TYPE, attachment.getMime());
        httpHeaders.add(CONTENT_DISPOSITION, String.format("%s; filename=\"%s\"", attachment.getDispositionType(), attachment.getFileName()));

        return new ResponseEntity<>(IOUtils.toByteArray(attachment.getData()), httpHeaders, HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping("attachment/{id:.*}")
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

    @DeleteMapping("attachment/{id:.*}")
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
