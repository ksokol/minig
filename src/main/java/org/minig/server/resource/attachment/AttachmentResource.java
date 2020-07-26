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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.minig.MinigConstants.API;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL_VALUE;

@RestController
public class AttachmentResource {

    private final AttachmentService attachmentService;

    public AttachmentResource(AttachmentService attachmentService) {
        this.attachmentService = requireNonNull(attachmentService, "attachmentService is null");
    }

    @GetMapping(value = API + "/attachment/{id:.*}", produces = ALL_VALUE)
    public ResponseEntity<?> downloadAttachment(@Id CompositeAttachmentId id) throws IOException {
        var attachment = attachmentService.findById(id);

        var httpHeaders = new HttpHeaders();
        httpHeaders.add(CONTENT_TYPE, attachment.getMime());
        httpHeaders.add(CONTENT_DISPOSITION, String.format("%s; filename=\"%s\"", attachment.getDispositionType(), attachment.getFileName()));

        return new ResponseEntity<>(IOUtils.toByteArray(attachment.getData()), httpHeaders, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(API + "/attachment/{id:.*}")
    public Map<String, Object> uploadAttachment(@Id CompositeId id, MultipartRequest file) {
        if (file.getFileMap().size() > 1) {
            throw new ClientIllegalArgumentException("too many files");
        }

        var multipartFile = file.getFileMap().values().iterator().next();
        var newMailId = attachmentService.addAttachment(id, new MultipartfileDataSource(multipartFile));
        var attachments = attachmentService.findAttachments(newMailId);

        Map<String, Object> map = new HashMap<>();
        map.put("id", newMailId);
        map.put("attachments", attachments);
        return map;
    }

    @DeleteMapping(API + "/attachment/{id:.*}")
    public Map<String, Object> deleteAttachment(@Id CompositeAttachmentId id) {
        var newMailId = attachmentService.deleteAttachment(id);
        var attachments = attachmentService.findAttachments(newMailId);

        Map<String, Object> map = new HashMap<>();
        map.put("id", newMailId);
        map.put("attachments", attachments);
        return map;
    }
}
