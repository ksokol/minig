package org.minig.server.service;

import config.ServiceTestConfig;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailAttachment;
import org.minig.server.TestConstants;
import org.minig.test.javamail.MailboxRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.minig.server.TestConstants.MOCK_USER;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(ServiceTestConfig.class)
@ActiveProfiles("test")
public class AttachmentServiceTest {

    @Autowired
    private AttachmentService service;

    @Rule
    public MailboxRule mailboxRule = new MailboxRule(MOCK_USER);

    @Test
    public void testFindAttachments_hasAttachments() throws MessagingException {
        MimeMessage mimeMessage = new MimeMessageBuilder().setFolder("INBOX").build(TestConstants.MULTIPART_WITH_ATTACHMENT);
        mailboxRule.append("INBOX", mimeMessage);

        CompositeId id = new CompositeId();
        id.setFolder("INBOX");
        id.setMessageId(mimeMessage.getMessageID());

        List<MailAttachment> findAttachments = service.findAttachments(id);

        assertThat(findAttachments, hasSize(2));
    }

    @Test
    public void testFindAttachments_hasNoAttachments() throws MessagingException {
        MimeMessage mimeMessage = new MimeMessageBuilder().build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);
        mailboxRule.append("INBOX", mimeMessage);

        CompositeId id = new CompositeId();
        id.setFolder("INBOX");
        id.setMessageId(mimeMessage.getMessageID());

        List<MailAttachment> findAttachments = service.findAttachments(id);

        assertThat(findAttachments, hasSize(0));
    }

    @Test
    public void testFindAttachments_noMessage() {
        CompositeId id = new CompositeId();
        id.setFolder("INBOX");
        id.setMessageId("<id@localhost>");

        List<MailAttachment> findAttachments = service.findAttachments(id);

        assertThat(findAttachments, hasSize(0));
    }

    @Test(expected = NotFoundException.class)
    public void shouldThrowNotFoundExceptionWhenAttachmentIsUnknown() throws Exception {
        service.findById(new CompositeAttachmentId("INBOX", "unknown","unknown"));
    }

    @Test
    public void shouldFindInlineAttachmentById() throws Exception {
        MimeMessage mimeMessage = new MimeMessageBuilder().setFolder("INBOX").build(TestConstants.ALTERNATIVE);
        mailboxRule.append("INBOX", mimeMessage);

        CompositeAttachmentId compositeAttachmentId = new CompositeAttachmentId("INBOX", mimeMessage.getMessageID(),"1367760625.51865ef16cc8c@swift.generated");
        MailAttachment mailAttachment = service.findById(compositeAttachmentId);

        assertThat(mailAttachment.getId(), is(compositeAttachmentId.getId()));
        assertThat(mailAttachment.getMime(), is(IMAGE_PNG_VALUE));
        assertThat(mailAttachment.getContentId(), is(compositeAttachmentId.getFileName()));
        assertThat(mailAttachment.getDispositionType(), is("inline"));
        assertThat(mailAttachment.getFileName(), is(compositeAttachmentId.getFileName()));
        assertThat(IOUtils.toByteArray(mailAttachment.getData()), is(new byte[] { (byte) 'a' }));
    }

}
