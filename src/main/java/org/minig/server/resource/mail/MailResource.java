package org.minig.server.resource.mail;

import org.minig.server.MailMessage;
import org.minig.server.MailMessageList;
import org.minig.server.resource.Id;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MailService;
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
@RequestMapping(value = "1")
class MailResource {

    @Autowired
    private MailService mailService;

    @RequestMapping(value = "message", method = RequestMethod.GET)
    @ResponseBody
    public MailMessageList findMessagesByFolder(@RequestParam String folder, @RequestParam(defaultValue = "1") int page,
            @RequestParam(value = "page_length", defaultValue = "10") int pageLength) {
        return mailService.findMessagesByFolder(folder, page, pageLength);
    }

    @RequestMapping(value = "message/**", method = RequestMethod.GET)
    @ResponseBody
    public MailMessage findMessage(@Id CompositeId id) {
        return mailService.findMessage(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "message/**", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteMessage(@Id CompositeId id) {
        mailService.deleteMessage(id);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "message/flag/**", method = RequestMethod.PUT)
    @ResponseBody
    public void updateMessage(@Id CompositeId id, @RequestBody MailMessage message) {
        message.setCompositeId(id);
        mailService.updateMessageFlags(message);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "message/flag", method = RequestMethod.PUT)
    @ResponseBody
    public void updateMessages(@RequestBody MailMessageList messageList) {
        mailService.updateMessagesFlags(messageList);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "message/copy", method = RequestMethod.PUT)
    @ResponseBody
    public void copyMessagesToFolder(@RequestBody MessageCopyOrMoveRequest request) {
        mailService.copyMessagesToFolder(request.getMessageIdList(), request.getFolder());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "message/move", method = RequestMethod.PUT)
    @ResponseBody
    public void moveMessagesToFolder(@RequestBody MessageCopyOrMoveRequest request) {
        mailService.moveMessagesToFolder(request.getMessageIdList(), request.getFolder());
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "message/delete", method = RequestMethod.PUT)
    @ResponseBody
    public void deleteMessagesToFolder(@RequestBody DeleteMessageRequest request) {
        mailService.deleteMessages(request.getMessageIdList());
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @RequestMapping(value = "message/draft", method = RequestMethod.POST)
    @ResponseBody
    public MailMessage createDraftMessage(@RequestBody MailMessage message) {
        CompositeId createDraftMessage = mailService.createDraftMessage(message);
        return mailService.findMessage(createDraftMessage);
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "message/draft/**", consumes = "application/json", method = RequestMethod.PUT)
    @ResponseBody
    public MailMessage updateDraftMessage(@Id CompositeId id, @RequestBody MailMessage message) {
        message.setCompositeId(id);
        CompositeId updateDraftMessage = mailService.updateDraftMessage(message);
        return mailService.findMessage(updateDraftMessage);
    }
}
