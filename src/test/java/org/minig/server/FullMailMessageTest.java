package org.minig.server;

import org.apache.commons.io.IOUtils;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.minig.server.service.MimeMessageBuilder;

import java.util.List;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author Kamill Sokol
 */
public class FullMailMessageTest {

    private MimeMessageBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = MimeMessageBuilder.withSource(TestConstants.MULTIPART_WITH_ATTACHMENT);
    }

    @Test
    public void shouldBeAnInstanceOfPartialMailMessage() throws Exception {
        assertThat(givenFullMailMessage(), instanceOf(PartialMailMessage.class));
    }

    @Test
    public void shouldGetBccRecipient() throws Exception {
        builder.setRecipientBcc("r1@local", "personal1");

        assertThat(givenFullMailMessage().getBcc(), contains(
                address(is("r1@local"), is("personal1"))
        ));
    }

    @Test
    public void shouldGetCcRecipient() throws Exception {
        builder.setRecipientCc("r1@local", "personal1");

        assertThat(givenFullMailMessage().getCc(), contains(
                address(is("r1@local"), is("personal1"))
        ));
    }

    @Test
    public void shouldGetToRecipient() throws Exception {
        builder.setRecipientTo("r1@local", "personal1");

        assertThat(givenFullMailMessage().getTo(), contains(
                address(is("r1@local"), is("personal1"))
        ));
    }

    @Test
    public void shouldGetEmptyRecipients() throws Exception {
        assertThat(givenFullMailMessage().getTo(), empty());
        assertThat(givenFullMailMessage().getCc(), empty());
        assertThat(givenFullMailMessage().getBcc(), empty());
        assertThat(givenFullMailMessage().getReplyTo(), empty());
    }

    @Test
    public void shouldGetTextBody() throws Exception {
        assertThat(givenFullMailMessage().getText(), containsString("plain"));
    }

    @Test
    public void shouldNotContainHtmlBody() throws Exception {
        assertThat(givenFullMailMessage().isHtml(), is(false));
    }

    @Test
    public void shouldContainHtmlBody() throws Exception {
        builder = MimeMessageBuilder.withSource(TestConstants.HTML);

        assertThat(givenFullMailMessage().isHtml(), is(true));
    }

    @Test
    public void shouldGetReplyTo() throws Exception {
        builder.setReplyTo("r1@local", "personal1");

        assertThat(givenFullMailMessage().getReplyTo(), contains(
                address(is("r1@local"), is("personal1"))
        ));
    }

    @Test
    public void shouldGetInReplyTo() throws Exception {
        builder.setInReplyTo("<messageId>");

        assertThat(givenFullMailMessage().getInReplyTo(), is("<messageId>"));
        assertThat(givenFullMailMessage().getReferences(), is("<messageId>"));
    }

    @Test
    public void shouldGetMDNSent() throws Exception {
        builder.setMDNSent(true);

        assertThat(givenFullMailMessage().isMdnSent(), is(true));
    }

    @Test
    public void shouldGetForwardedMessageId() throws Exception {
        builder.setForwardedMessageId("1");

        assertThat(givenFullMailMessage().getForwardedMessageId(), is("1"));
    }

    @Test
    public void shouldGetAskForDispositionNotification() throws Exception {
        builder.setAskForDispositionNotification(true);

        assertThat(givenFullMailMessage().isAskForDispositionNotification(), is(true));
    }

    @Test
    public void shouldGetAskForDispositionNotificationWithReceiptSet() throws Exception {
        builder.setReceipt(true).setAskForDispositionNotification(true);

        assertThat(givenFullMailMessage().isAskForDispositionNotification(), is(true));
    }

    @Test
    public void shouldGetHighPriority() throws Exception {
        builder.setHighPriority(true);

        assertThat(givenFullMailMessage().isHighPriority(), is(true));
    }

    @Test
    public void shouldGetMailer() throws Exception {
        builder.setMailer("mailer");

        assertThat(givenFullMailMessage().getMailer(), is("mailer"));
    }

    @Test
    public void shouldGetRecipientDispositionNotification() throws Exception {
        builder.setRecipientDispositionNotification("r1@local", "personal1");

        assertThat(givenFullMailMessage().getDispositionNotification(), contains(
                address(is("r1@local"), is("personal1"))
        ));
    }

    @Test
    public void shouldGetAttachments() throws Exception {
        byte[] BODY = new byte[] { (byte) 'a' };
        byte[] BODY2 = new byte[] { (byte) 'b' };

        List<MailAttachment> attachments = givenFullMailMessage().getAttachments();
        assertThat(attachments, hasSize(2));

        assertThat(attachments.get(0).getId(), is("folder|<51BDA5AE.90106@localhost>|1.png"));
        assertThat(attachments.get(0).getContentId(), nullValue());
        assertThat(attachments.get(0).getMime(), is("image/png"));
        assertThat(attachments.get(0).getFileName(), is("1.png"));
        assertThat(IOUtils.toByteArray(attachments.get(0).getData()), is(BODY));

        assertThat(attachments.get(1).getId(), is("folder|<51BDA5AE.90106@localhost>|2.png"));
        assertThat(attachments.get(1).getContentId(), nullValue());
        assertThat(attachments.get(1).getMime(), is("image/png"));
        assertThat(attachments.get(1).getFileName(), is("2.png"));
        assertThat(IOUtils.toByteArray(attachments.get(1).getData()), is(BODY2));
    }

    private FullMailMessage givenFullMailMessage() {
        return new FullMailMessage(builder.spy());
    }

    private Matcher<MailMessageAddress> address(Matcher<String> emailMatcher, Matcher<String> personalMatcher) {
        return allOf(
                hasProperty("email", emailMatcher),
                hasProperty("displayName", personalMatcher)
        );
    }
}
