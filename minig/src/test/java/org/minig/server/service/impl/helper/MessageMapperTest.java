package org.minig.server.service.impl.helper;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.minig.server.MailMessage;
import org.minig.server.TestConstants;
import org.minig.server.service.CompositeAttachmentId;
import org.minig.server.service.MimeMessageBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MessageMapperTest {

    private static MessageMapper uut = new MessageMapper();

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
                .setHighPriority(true).setReceipt(true).setAskForDispositionNotification(true).mock();

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

        assertEquals(1489, plain.length());
        assertTrue(plain.contains("From: 2013-04-25 09:35:54, To: 2013-04-25 09:44:54, Downtime: 0h 09m 00s"));

        assertEquals(25350, html.length());
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
        MimeMessage m = new MimeMessageBuilder().setRecipientDispositionNotification("test@localhost").setForwarded(true).setMDNSent(true)
                .mock();

        MailMessage mm = uut.convertShort(m);

        assertTrue(mm.getForwarded());
        assertTrue(mm.getDispositionNotification() == null);
    }
}
