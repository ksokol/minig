package org.minig.server.resource.mail;

import org.minig.server.FullMailMessage;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageList;
import org.minig.server.PartialMailMessage;
import org.minig.server.resource.Id;
import org.minig.server.service.CompositeId;
import org.minig.server.service.mail.MailService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.minig.MinigConstants.API;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

@RestController
class MailResource {

    private final MailService mailService;

    MailResource(MailService mailService) {
        this.mailService = Objects.requireNonNull(mailService, "mailService is null");
    }

    @GetMapping({API + "/message", API + "/message/"})
    public Map<String, Object> findMessagesByFolder(
            @RequestParam String folder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(value = "page_length", defaultValue = "10") int pageLength
    ) {
        var messagesByFolder = mailService.findMessagesByFolder(folder, page, pageLength);

        // maintain API compatibility
        Map<String, Object> response = new HashMap<>();

        response.put("fullLength", messagesByFolder.getTotalElements());
        response.put("page", messagesByFolder.getNumber());
        response.put("mailList", messagesByFolder.getContent());

        return response;
    }

    @GetMapping(value = API + "/message/{id:.*}/html", produces = TEXT_HTML_VALUE)
    public String htmlBody(@Id CompositeId id) {
        return mailService.findHtmlBodyByCompositeId(id);
    }

    @GetMapping(API + "/message/{id:.*}")
    public FullMailMessage findMessage(@Id CompositeId id) {
        return mailService.findByCompositeId(id);
    }

    @DeleteMapping(API + "/message/{id:.*}")
    public void deleteMessage(@Id CompositeId id) {
        mailService.deleteMessage(id);
    }

    @PutMapping(API + "/message/flag/{id:.*}")
    public void updateMessage(@Id CompositeId id, @RequestBody MailMessage message) {
        message.setCompositeId(id);
        mailService.updateMessageFlags(message);
    }

    @PutMapping(API + "/message/flag")
    public void updateMessages(@RequestBody MailMessageList messageList) {
        mailService.updateMessagesFlags(messageList);
    }

    @PutMapping(API + "/message/copy")
    public void copyMessagesToFolder(@RequestBody MessageCopyOrMoveRequest request) {
        mailService.copyMessagesToFolder(request.getMessageIdList(), request.getFolder());
    }

    @PutMapping(API + "/message/move")
    public void moveMessagesToFolder(@RequestBody MessageCopyOrMoveRequest request) {
        mailService.moveMessagesToFolder(request.getMessageIdList(), request.getFolder());
    }

    @PutMapping(API + "/message/delete")
    public void deleteMessagesToFolder(@RequestBody DeleteMessageRequest request) {
        mailService.deleteMessages(request.getMessageIdList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(API + "/message/draft")
    public MailMessage createDraftMessage(@RequestBody MailMessage message) {
        var createDraftMessage = mailService.createDraftMessage(message);
        return mailService.findMessage(createDraftMessage);
    }

    @PutMapping(value = API + "/message/draft/{id:.*}", consumes = APPLICATION_JSON_VALUE)
    public MailMessage updateDraftMessage(@Id CompositeId id, @RequestBody MailMessage message) {
        message.setCompositeId(id);
        var updateDraftMessage = mailService.updateDraftMessage(message);
        return mailService.findMessage(updateDraftMessage);
    }
}
