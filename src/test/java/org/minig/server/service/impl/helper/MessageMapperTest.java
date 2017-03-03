package org.minig.server.service.impl.helper;

import org.junit.BeforeClass;
import org.junit.Test;
import org.minig.server.MailMessage;
import org.minig.server.MailMessageBody;
import org.minig.server.TestConstants;
import org.minig.server.converter.MessageToCompositeAttachmentIdConverter;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.converter.Converter;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Kamill Sokol
 */
public class MessageMapperTest {

    private static MessageMapper uut = new MessageMapper();

    @BeforeClass
    public static void beforeClass() {
        ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();

        Set<Converter> converters = new HashSet<>();
        converters.add(new MessageToCompositeAttachmentIdConverter());

        conversionServiceFactoryBean.setConverters(converters);
        conversionServiceFactoryBean.afterPropertiesSet();

        uut.setConversionService(conversionServiceFactoryBean.getObject());
    }

    @Test
    public void testId() {
        MimeMessageBuilder builder = new MimeMessageBuilder();
        MimeMessage m = builder.setMessageId("messageIdTest").setFolder("folderTest").mock();

        String expectedId = "folderTest|messageIdTest";

        MailMessage c = uut.convertShort(m);

        assertEquals(expectedId, c.getId());
        assertEquals("folderTest", c.getFolder());
    }

    @Test
    public void testConvertShort() {
        MimeMessageBuilder builder = new MimeMessageBuilder();
        MimeMessage m = builder.mock();

        MailMessage c = uut.convertShort(m);

        assertEquals(builder.getFolder(), c.getFolder());
        assertEquals(builder.getSender(), c.getSender());
        assertEquals(builder.getSubject(), c.getSubject());
        assertEquals(builder.getDate(), c.getDate());
        assertEquals(builder.isAnswered(), c.getAnswered());
        assertEquals(builder.isHighPriority(), c.getHighPriority());
        assertEquals(builder.isStarred(), c.getStarred());
        assertEquals(builder.isRead(), c.getRead());
        assertEquals(builder.isAskForDispositionNotification(), c.getAskForDispositionNotification());
        assertEquals(builder.isReceipt(), c.getReceipt());
        assertEquals(builder.isDeleted(), c.getDeleted());

        assertNotNull(c.getAttachments());
    }

    @Test
    public void testConvertFull() {
        MimeMessageBuilder builder = new MimeMessageBuilder();
        MimeMessage m = builder.setRecipientTo("recipient2@localhost").setRecipientCc("recipient11@localhost")
                .setRecipientBcc("recipient21@localhost").setRecipientDispositionNotification("recipient101@localhost")
                .setHighPriority(true).setReceipt(true).setAskForDispositionNotification(true).setInReplyTo("inReplyTo").mock();

        MailMessage c = uut.convertFull(m);

        assertEquals(builder.getFolder(), c.getFolder());
        assertEquals(builder.getSender(), c.getSender());
        assertEquals(builder.getRecipientTo(), c.getTo());
        assertEquals(builder.getRecipientCc(), c.getCc());
        assertEquals(builder.getRecipientBcc(), c.getBcc());
        assertEquals(builder.getSubject(), c.getSubject());
        assertEquals(builder.getDate(), c.getDate());
        assertEquals(builder.getMailer(), c.getMailer());
        assertEquals(builder.isAnswered(), c.getAnswered());
        assertEquals(builder.isHighPriority(), c.getHighPriority());
        assertEquals(builder.isStarred(), c.getStarred());
        assertEquals(builder.isRead(), c.getRead());
        assertEquals(builder.getDispositionNotification(), c.getDispositionNotification());
        assertEquals(builder.isAskForDispositionNotification(), c.getAskForDispositionNotification());
        assertEquals(builder.isReceipt(), c.getReceipt());
        assertEquals(builder.isDeleted(), c.getDeleted());
        assertThat(c.getInReplyTo(), is("inReplyTo"));

        assertNotNull(c.getAttachments());
    }

    @Test
    public void testAttachmentIds() throws MessagingException {
        MimeMessageBuilder builder = new MimeMessageBuilder();
        MimeMessage m = builder.build(TestConstants.MULTIPART_WITH_ATTACHMENT);

        CompositeAttachmentId id1 = new CompositeAttachmentId(m.getFolder().getFullName(), m.getMessageID(), "1.png");
        CompositeAttachmentId id2 = new CompositeAttachmentId(m.getFolder().getFullName(), m.getMessageID(), "2.png");

        MailMessage c = uut.convertShort(m);

        assertEquals(id1.getId(), c.getAttachments().get(0).getId());
        assertEquals(id2.getId(), c.getAttachments().get(1).getId());
    }

    @Test
    public void testBody() {
        MimeMessageBuilder builder = new MimeMessageBuilder();
        MimeMessage m = builder.build(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);

        MailMessage c = uut.convertFull(m);

        String plain = c.getBody().getPlain();
        String html = c.getBody().getHtml();

        assertThat(plain.length(), greaterThanOrEqualTo(1449)); //ignore line endings
        assertTrue(plain.contains("From: 2013-04-25 09:35:54, To: 2013-04-25 09:44:54, Downtime: 0h 09m 00s"));

        assertThat(html.length(), greaterThanOrEqualTo(25257));
        assertTrue(html.contains("<td><br><h3>178.254.55.49</h3></td></tr>"));
    }

    @Test
    public void testToMimeMessage_draftInfo() throws MessagingException {
        MailMessage mm = new MailMessage();

        Message message = uut.toMessage(mm);
        String[] header = message.getHeader("X-Mozilla-Draft-Info");
        assertTrue(header == null);

        mm.setReceipt(true);

        message = uut.toMessage(mm);
        header = message.getHeader("X-Mozilla-Draft-Info");
        assertEquals("receipt=1", header[0]);

        mm.setAskForDispositionNotification(true);

        message = uut.toMessage(mm);
        header = message.getHeader("X-Mozilla-Draft-Info");
        assertEquals("receipt=1; DSN=1", header[0]);

        mm.setReceipt(false);

        message = uut.toMessage(mm);
        header = message.getHeader("X-Mozilla-Draft-Info");
        assertEquals("DSN=1", header[0]);
    }

    @Test
    public void testToMimeMessage_highPriority() throws MessagingException {
        MailMessage mm = new MailMessage();

        Message message = uut.toMessage(mm);
        String[] header = message.getHeader("X-Priority");
        assertTrue(header == null);

        mm.setHighPriority(true);

        message = uut.toMessage(mm);
        header = message.getHeader("X-Priority");
        assertEquals("1", header[0]);
    }

    @Test
    public void testToMimeMessage_userFlags() throws MessagingException {
        MimeMessage m = new MimeMessageBuilder().setRecipientDispositionNotification("test@localhost").setForwarded(true).setMDNSent(true).mock();

        MailMessage mm = uut.convertShort(m);

        assertTrue(mm.getForwarded());
        assertTrue(mm.getDispositionNotification() == null);
    }

    @Test
    public void testToMime4jMessage() throws MessagingException {
        MailMessage mailMessage = new MailMessage();
        MailMessageBody mailMessageBody = new MailMessageBody();
        mailMessageBody.setPlain("plain");
        mailMessage.setBody(mailMessageBody);
        mailMessage.setInReplyTo("inReplyTo");
        mailMessage.setForwardedMessageId("forwardId");

        Mime4jMessage mime4jMessage = uut.toMime4jMessage(mailMessage);
        assertThat(mime4jMessage.getPlain(), is("plain"));
        assertThat(mime4jMessage.getInReplyTo(), is("inReplyTo"));
        assertThat(mime4jMessage.toMessage().getHeader("References")[0], is("inReplyTo"));
        assertThat(mime4jMessage.getForwardedMessageId(), is("forwardId"));
    }

    @Test
    public void testToMime4jMessageNPEcheck() {
        MailMessage mailMessage = new MailMessage();

        Mime4jMessage mime4jMessage = uut.toMime4jMessage(mailMessage);
        assertThat(mime4jMessage.getPlain(), is(""));
        assertThat(mime4jMessage.getInReplyTo(), nullValue());
        assertThat(mime4jMessage.getForwardedMessageId(), nullValue());
    }

    @Test
    public void testMime4jAttachment() throws Exception {
        MimeMessage mime = new MimeMessageBuilder().build(TestConstants.MULTIPART_ATTACHMENT_BINARY);
        List<CompositeAttachmentId> attachments = uut.convertFull(mime).getAttachments();

        assertThat(attachments, hasSize(1));
        assertThat(attachments.get(0).getId(), is("folder|<6080306@localhost>|umlaut ä.png"));
    }

    @Test
    public void testMime4jNestedMessage() throws Exception {
        MimeMessage mime = new MimeMessageBuilder().build(TestConstants.NESTED_MESSAGE);
        List<CompositeAttachmentId> attachments = uut.convertFull(mime).getAttachments();

        assertThat(attachments, hasSize(1));
        assertThat(attachments.get(0).getFileName(), is("Disposition Notification Test.eml"));
    }

    @Test
    public void testReadAttachment_hasAttachment2() throws Exception {
        String fileName = "umlaut ä veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery long.png";
        MimeMessage mime = new MimeMessageBuilder().build(TestConstants.MULTIPART_RFC_2231_2);
        List<CompositeAttachmentId> attachments = uut.convertFull(mime).getAttachments();

        assertThat(attachments, hasSize(1));
        assertThat(attachments.get(0).getFileName(), is(fileName));
    }
}
