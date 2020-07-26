package org.minig.server.resource.submission;

import org.minig.server.MailMessage;
import org.minig.server.resource.Id;
import org.minig.server.service.CompositeId;
import org.minig.server.service.submission.DispositionService;
import org.minig.server.service.submission.SubmissionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

import static org.minig.MinigConstants.API;

@RestController
public class SubmissionResource {

    private final SubmissionService mailSendService;
    private final DispositionService dispositionService;

    public SubmissionResource(SubmissionService mailSendService, DispositionService dispositionService) {
        this.mailSendService = Objects.requireNonNull(mailSendService, "mailSendService is null");
        this.dispositionService = Objects.requireNonNull(dispositionService, "dispositionService is null");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(API + "/submission")
    public void send(@RequestBody MailMessage mailMessage) {
        mailSendService.sendMessage(mailMessage);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(API + "/submission/disposition/**")
    public void send(@Id CompositeId id) {
        dispositionService.sendDisposition(id);
    }
}
