package org.minig.server.service.impl.helper.mime;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.minig.server.TestConstants;
import org.minig.server.service.CompositeId;

import javax.activation.FileDataSource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import static javax.mail.Message.RecipientType.BCC;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.minig.test.hamcrest.Mime4jMessageMatchers.alternative;
import static org.minig.test.hamcrest.Mime4jMessageMatchers.attachment;
import static org.minig.test.hamcrest.Mime4jMessageMatchers.attachmentName;
import static org.minig.test.hamcrest.Mime4jMessageMatchers.htmlBody;
import static org.minig.test.hamcrest.Mime4jMessageMatchers.inlineAttachment;
import static org.minig.test.hamcrest.Mime4jMessageMatchers.mixed;
import static org.minig.test.hamcrest.Mime4jMessageMatchers.octetStreamBody;
import static org.minig.test.hamcrest.Mime4jMessageMatchers.related;
import static org.minig.test.hamcrest.Mime4jMessageMatchers.textBody;
import static org.minig.test.hamcrest.MimeMatcherAssert.assertMimeMessage;

/**
 * @author Kamill Sokol
 */
public class Mime4jMessageTest {

    @Test
    public void testSetPlain() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN);

        assertTrue(mime4jMessage.getPlain().contains("This is a message written solely for testing."));

        mime4jMessage.setPlain("replacing plain text");

        assertEquals("replacing plain text", mime4jMessage.getPlain());
    }

    @Test
    public void testSetPlain2() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.HTML);

        assertTrue(mime4jMessage.getHtml().contains("<body bgcolor="));

        mime4jMessage.setHtml("<tr><td></td></tr>");

        assertEquals("<tr><td></td></tr>", mime4jMessage.getHtml());
    }

    @Test
    public void testSetPlain3() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN);

        assertTrue(mime4jMessage.getPlain().contains("This is a message written solely for testing."));

        mime4jMessage.setHtml("<tr><td></td></tr>");

        assertTrue(mime4jMessage.getPlain().contains("This is a message written solely for testing."));
        assertEquals("<tr><td></td></tr>", mime4jMessage.getHtml());
    }

    @Test
    public void testSetPlain4() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.HTML);

        assertTrue(mime4jMessage.getHtml().contains("<body bgcolor="));

        mime4jMessage.setPlain("This is a message written solely for testing.");

        assertTrue(mime4jMessage.getHtml().contains("<body bgcolor="));
        assertTrue(mime4jMessage.getPlain().contains("This is a message written solely for testing."));
    }

    @Test
    public void testSetPlain5() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.HTML);
        assertThat(mime4jMessage.getAttachments(), hasSize(0));

        mime4jMessage.addAttachment(new FileDataSource(TestConstants.ATTACHMENT_IMAGE_1_PNG));
        assertThat(mime4jMessage.getAttachments(), hasSize(1));
    }

    @Test
    public void testSetMultipart6() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_PLAIN_AND_ATTACHMENT);

        assertThat(mime4jMessage.getPlain(), is(equalToIgnoringWhiteSpace("plain text")));
        assertThat(mime4jMessage.getHtml(), is(""));

        String replacedBody = "replaced plain " + new Date().toString();
        mime4jMessage.setPlain(replacedBody);
        mime4jMessage.setHtml(replacedBody);

        assertThat(mime4jMessage.getPlain(), is(replacedBody));
        assertThat(mime4jMessage.getHtml(), is(replacedBody));
    }

    @Test
    public void testSetMultipart7() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);

        assertTrue(mime4jMessage.getPlain().contains("Pingdom Monthly Report"));
        assertTrue(mime4jMessage.getHtml().contains("<table width="));

        String replacedBody = "replaced plain " + new Date().toString();
        mime4jMessage.setPlain(replacedBody);
        mime4jMessage.setHtml(replacedBody);

        assertEquals(replacedBody, mime4jMessage.getPlain());
        assertEquals(replacedBody, mime4jMessage.getHtml());
    }

    @Test
    public void testSetMultipart8() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);

        assertEquals("", mime4jMessage.getPlain().trim());
        assertEquals("", mime4jMessage.getHtml().trim());

        String replacedBody = "replaced plain " + new Date().toString();
        mime4jMessage.setPlain(replacedBody);
        mime4jMessage.setHtml(replacedBody);

        assertEquals(replacedBody, mime4jMessage.getPlain());
        assertEquals(replacedBody, mime4jMessage.getHtml());
    }

    @Test
    public void testSetMultipart9() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_HTML_AND_ATTACHMENT);

        assertEquals("", mime4jMessage.getPlain());
        assertTrue(mime4jMessage.getHtml().contains("<body bgcolor="));

        String replacedBody = "replaced plain " + new Date().toString();
        mime4jMessage.setPlain(replacedBody);
        mime4jMessage.setHtml(replacedBody);

        assertEquals(replacedBody, mime4jMessage.getPlain());
        assertEquals(replacedBody, mime4jMessage.getHtml());
    }

    @Test
    public void testSetPlain6() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN);
        assertThat(mime4jMessage.getAttachments(), hasSize(0));

        mime4jMessage.addAttachment(new FileDataSource(TestConstants.ATTACHMENT_IMAGE_1_PNG));
        assertThat(mime4jMessage.getAttachments(), hasSize(1));
    }

    @Test
    public void testSetPlain7() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);
        assertThat(mime4jMessage.getAttachments(), hasSize(0));

        mime4jMessage.addAttachment(new FileDataSource(TestConstants.ATTACHMENT_IMAGE_1_PNG));
        assertThat(mime4jMessage.getAttachments(), hasSize(1));
    }

    @Test
    public void testSetPlain8() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);
        assertThat(mime4jMessage.getAttachments(), hasSize(2));

        mime4jMessage.addAttachment(new FileDataSource(TestConstants.ATTACHMENT_IMAGE_1_PNG));
        assertThat(mime4jMessage.getAttachments(), hasSize(3));
    }

    @Test
    public void testSetPlain9() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);
        assertThat(mime4jMessage.getAttachments(), hasSize(2));

        mime4jMessage.deleteAttachment("2.png");
        assertThat(mime4jMessage.getAttachments(), hasSize(1));
    }

    @Test
    public void testNoDSN() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);
        assertThat(mime4jMessage.hasDispositionNotifications(), is(false));
    }

    @Test
    public void testDSN() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN_DSN_HEADER_1);
        assertThat(mime4jMessage.hasDispositionNotifications(), is(true));
        assertThat(mime4jMessage.isDSN(), is(true));
    }

    @Test
    public void testDSNReceipt() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.PLAIN_DSN_HEADER_2);
        assertThat(mime4jMessage.hasDispositionNotifications(), is(true));
        assertThat(mime4jMessage.isReturnReceipt(), is(true));
    }

    @Test
    public void testAddTo() throws Exception {
        Mime4jMessage mime4j = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_ATTACHMENT);
        assertThat(Arrays.toString(mime4j.toMessage().getRecipients(TO)), is("[testuser@localhost]"));
    }

    @Test
    public void testMime4jAttachment() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_ATTACHMENT_BINARY);
        assertThat(mime4jMessage.getAttachments(), hasSize(1));

        Mime4jAttachment attachment = mime4jMessage.getAttachments().get(0);
        assertThat(attachment.getId().getId(), is("folder|<6080306@localhost>|umlaut ä.png"));
    }

    @Test
    public void testMime4jNestedMessage() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.NESTED_MESSAGE);

        assertThat(mime4jMessage.getAttachments(), hasSize(1));

        Mime4jAttachment attachment = mime4jMessage.getAttachment("Disposition Notification Test.eml");
        String text = new Scanner(attachment.getData()).useDelimiter("\\A").next();

        assertThat(attachment.getMimeType(), is("text/plain"));
        assertThat(text, equalToIgnoringWhiteSpace("Body nested"));
    }

    @Test
    public void shouldSetPlainBody() throws Exception {
        Mime4jMessage mime4jMessage = aMime4jMessage();

        mime4jMessage.setPlain("plain body");

        assertMimeMessage(
                mime4jMessage.toMessage(),
                mixed(
                    alternative(
                        textBody(is("plain body"))
                    )
                )
        );
    }

    @Test
    public void shouldSetHtmlBody() throws Exception {
        Mime4jMessage mime4jMessage = aMime4jMessage();

        mime4jMessage.setHtml("html body");

        assertThat(
                mime4jMessage.toMessage(),
                mixed(
                    alternative(
                        textBody(isEmptyString()),
                        htmlBody(is("html body"))
                    )
                )
        );
    }

    @Test
    public void shouldSetPlainAndHtmlBody() throws Exception {
        Mime4jMessage mime4jMessage = aMime4jMessage();

        mime4jMessage.setPlain("plain body");
        mime4jMessage.setHtml("html body");

        assertThat(
                mime4jMessage.toMessage(),
                mixed(
                    alternative(
                        textBody(is("plain body")),
                        htmlBody(is("html body"))
                    )
                )
        );
    }

    @Test
    public void shouldAddAttachment() throws Exception {
        Mime4jMessage mime4jMessage = aMime4jMessage();

        mime4jMessage.addAttachment(new StringDataSource("attachment"));

        assertMimeMessage(mime4jMessage.toMessage(),
                mixed(
                    textBody(isEmptyString()),
                    octetStreamBody(is("attachment"))
                )
        );
    }

    @Test
    public void shouldAddAttachmentAndSetHtmlBody() throws Exception {
        Mime4jMessage mime4jMessage = aMime4jMessage();

        mime4jMessage.addAttachment(new StringDataSource("attachment"));
        mime4jMessage.setHtml("html body");

        assertThat(
            mime4jMessage.toMessage(),
            mixed(
                octetStreamBody(is("attachment")),
                alternative(
                    textBody(isEmptyString()),
                    htmlBody(is("html body"))
                )
            )
        );
    }

    @Test
    public void shouldSetPlainAndHtmlBodyAndAddAttachment() throws Exception {
        Mime4jMessage mime4jMessage = aMime4jMessage();

        mime4jMessage.setPlain("plain body");
        mime4jMessage.setHtml("html body");
        mime4jMessage.addAttachment(new StringDataSource("attachment"));

        assertMimeMessage(
                mime4jMessage.toMessage(),
                mixed(
                    alternative(
                        textBody(is("plain body")),
                        htmlBody(is("html body"))
                    ),
                    octetStreamBody(is("attachment"))
                )
        );
    }

    @Test
    public void shouldAddAttachmentAndSetPlainAndHtmlBody() throws Exception {
        Mime4jMessage mime4j = aMime4jMessage();

        mime4j.addAttachment(new StringDataSource("attachment"));
        mime4j.setPlain("plain body");
        mime4j.setHtml("html body");

        assertMimeMessage(
                mime4j.toMessage(),
                mixed(
                    octetStreamBody(is("attachment")),
                    alternative(
                        textBody(is("plain body")),
                        htmlBody(is("html body"))
                    )
                )
        );
    }

    @Test
    public void shouldASetHtmlAndPlainBodyAndAddAttachment() throws Exception {
        Mime4jMessage mime4jMessage = aMime4jMessage();

        mime4jMessage.addAttachment(new StringDataSource("attachment"));
        mime4jMessage.setHtml("html body");
        mime4jMessage.setPlain("plain body");

        assertMimeMessage(
                mime4jMessage.toMessage(),
                mixed(
                    octetStreamBody(is("attachment")),
                    alternative(
                        htmlBody(is("html body")),
                        textBody(is("plain body"))
                    )
                )
        );
    }

    @Test
    public void shouldRetainMultipartRelated() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);
        mime4jMessage.addAttachment(new StringDataSource("attachment"));

        assertMimeMessage(
                mime4jMessage.toMessage(),
                mixed(
                    alternative(
                        textBody(startsWith("Pingdom Monthly")),
                        related(
                                htmlBody(startsWith("<!DOCTYPE html PUBLIC")),
                                inlineAttachment(attachmentName(is("logo.png"))),
                                inlineAttachment(attachmentName(is("bg2.png"))),
                                inlineAttachment(attachmentName(is("bg1.png")))
                        )
                    ),
                    octetStreamBody(is("attachment"))
                )
        );
    }

    @Test
    public void shouldRemoveMultipartRelated() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_PLAIN_AND_HTML);
        mime4jMessage.addAttachment(new StringDataSource("attachment"));
        mime4jMessage.setHtml("html");

        assertMimeMessage(
                mime4jMessage.toMessage(),
                mixed(
                    alternative(
                        textBody(startsWith("Pingdom Monthly")),
                        htmlBody(startsWith("html"))
                    ),
                    octetStreamBody(is("attachment"))
                )
        );
    }

    @Test
    public void shouldAddAttachmentToExistingMessage() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_HTML_AND_ATTACHMENT);
        mime4jMessage.addAttachment(new StringDataSource("attachment"));

        assertMimeMessage(
                mime4jMessage.toMessage(),
                mixed(
                    htmlBody(startsWith("<html>")),
                    attachment(attachmentName(is("1.png"))),
                    octetStreamBody(is("attachment"))
                )
        );
    }

    @Test
    public void shouldReplacePlainBody() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_WITH_HTML_AND_ATTACHMENT);
        mime4jMessage.addAttachment(new StringDataSource("attachment"));
        mime4jMessage.setPlain("plain");

        assertMimeMessage(
                mime4jMessage.toMessage(),
                mixed(
                    attachment(attachmentName(is("1.png"))),
                    octetStreamBody(is("attachment")),
                    alternative(
                            htmlBody(startsWith("<html>")),
                            textBody(startsWith("plain"))
                    )
                )
        );
    }

    @Test
    public void shouldRetainMixedBodyWithHtmlBodyPartAndAttachment() throws Exception {
        Mime4jMessage mime4jMessage = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_RFC_2231);

        assertMimeMessage(
                mime4jMessage.toMessage(),
                mixed(
                    htmlBody(startsWith("<html>")),
                    attachment(attachmentName(is("umlaut ä.png")))
                )
        );
    }

    @Test
    public void shouldClearToAddresses() throws Exception {
        Mime4jMessage mime4j = aMime4jMessage();

        mime4j.addRecipient("test@example");
        assertThat(mime4j.toMessage().getRecipients(TO), arrayWithSize(1));

        mime4j.clearRecipients();
        assertThat(mime4j.toMessage().getRecipients(TO), nullValue());
    }

    @Test
    public void shouldClearBccAddresses() throws Exception {
        Mime4jMessage mime4j = aMime4jMessage();

        mime4j.addBcc("test@example");
        assertThat(mime4j.toMessage().getRecipients(BCC), arrayWithSize(1));

        mime4j.clearBcc();
        assertThat(mime4j.toMessage().getRecipients(BCC), nullValue());
    }

    @Test
    public void shouldClearCcAddresses() throws Exception {
        Mime4jMessage mime4j = aMime4jMessage();

        mime4j.addCc("test@example");
        assertThat(mime4j.toMessage().getRecipients(CC), arrayWithSize(1));

        mime4j.clearCc();
        assertThat(mime4j.toMessage().getRecipients(CC), nullValue());
    }

    @Test
    public void shouldDeleteAttachment() throws Exception {
        Mime4jMessage mime4jMessage = aMime4jMessage();

        mime4jMessage.addAttachment(new StringDataSource("attachment1"));
        mime4jMessage.addAttachment(new StringDataSource("attachment2"));

        assertThat(mime4jMessage.getAttachments(), hasSize(2));

        mime4jMessage.deleteAttachment("attachment2");
        assertThat(mime4jMessage.getAttachments(), contains(hasProperty("filename", is("attachment1"))));
    }


    @Test
    public void shouldContainToAddresses() throws Exception {
        Mime4jMessage mime4j = aMime4jMessage();

        mime4j.addTo("test1@example");
        mime4j.addTo("test2@example");

        assertThat(Arrays.toString(mime4j.toMessage().getRecipients(TO)), is("[test1@example, test2@example]"));
    }

    @Test
    public void shouldContainBccAddresses() throws Exception {
        Mime4jMessage mime4j = aMime4jMessage();

        mime4j.addBcc("test1@example");
        mime4j.addBcc("test2@example");

        assertThat(Arrays.toString(mime4j.toMessage().getRecipients(BCC)), is("[test1@example, test2@example]"));
    }

    @Test
    public void shouldContainCcAddresses() throws Exception {
        Mime4jMessage mime4j = aMime4jMessage();

        mime4j.addCc("test1@example");
        mime4j.addCc("test2@example");

        assertThat(Arrays.toString(mime4j.toMessage().getRecipients(CC)), is("[test1@example, test2@example]"));
    }

    @Test
    public void shouldContainFromAddress() throws Exception {
        Mime4jMessage mime4j = aMime4jMessage();

        mime4j.setFrom("test1@example");
        mime4j.setFrom("test2@example");

        assertThat(Arrays.toString(mime4j.toMessage().getFrom()), is("[test2@example]"));
    }

    @Test
    public void shouldSetSenderWhenSettingFromAddress() throws Exception {
        Mime4jMessage mime4j = aMime4jMessage();

        mime4j.setFrom("test1@example");

        assertThat(mime4j.toMessage().getSender().toString(), is("test1@example"));
    }

    @Test
    public void shouldContainSubject() throws Exception {
        Mime4jMessage mime4jMessage = aMime4jMessage();

        mime4jMessage.setSubject("test");

        assertThat(mime4jMessage.toMessage().getSubject(), is("test"));
    }

    @Test
    public void testBinaryAttachment() throws Exception {
        Mime4jMessage message = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_ATTACHMENT_BINARY);

        List<Mime4jAttachment> attachments = message.getAttachments();
        assertThat(attachments, hasSize(1));

        Mime4jAttachment attachment = attachments.get(0);

        assertThat(attachment.getMimeType(), CoreMatchers.is("image/png"));
        assertThat(attachment.getFilename(), CoreMatchers.is("umlaut ä.png"));
    }

    @Test
    public void testPlaintextAttachment() throws Exception {
        Mime4jMessage message = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_ATTACHMENT_PLAINTEXT);

        List<Mime4jAttachment> attachments = message.getAttachments();
        assertThat(attachments, hasSize(1));

        Mime4jAttachment attachment = attachments.get(0);

        assertThat(attachment.getMimeType(), CoreMatchers.is("text/plain"));
        assertThat(attachment.getFilename(), CoreMatchers.is("lyrics.txt"));
    }

    @Test
    public void testRFC2231() throws Exception {
        Mime4jMessage message = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_RFC_2231);

        List<Mime4jAttachment> attachments = message.getAttachments();
        assertThat(attachments, hasSize(1));

        Mime4jAttachment attachment = attachments.get(0);

        assertThat(attachment.getMimeType(), CoreMatchers.is("image/png"));
        assertThat(attachment.getFilename(), CoreMatchers.is("umlaut ä.png"));
    }

    @Test
    public void testRFC2231_2() throws Exception {
        Mime4jMessage message = Mime4jTestHelper.freshMime4jMessage(TestConstants.MULTIPART_RFC_2231_2);

        List<Mime4jAttachment> attachments = message.getAttachments();
        assertThat(attachments, hasSize(1));

        Mime4jAttachment attachment = attachments.get(0);

        assertThat(attachment.getMimeType(), CoreMatchers.is("image/png"));
        assertThat(attachment.getFilename(), CoreMatchers.is("umlaut ä veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery long.png"));
    }

    private static Mime4jMessage aMime4jMessage() {
        return new Mime4jMessage(new CompositeId("aFolder", "aMessageId"));
    }
}
