package org.minig.server.service.impl.helper.mime;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.dom.MessageWriter;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.message.BasicBodyFactory;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.DefaultMessageWriter;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.message.MessageServiceFactoryImpl;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.MimeConfig;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.service.MailRepository;
import org.minig.server.service.MimeMessageBuilder;
import org.minig.server.service.ServiceTestConfig;
import org.minig.server.service.SmtpAndImapMockServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceTestConfig.class })
@ActiveProfiles("test")
public class TestContentHandlerTest {

    @Autowired
    private SmtpAndImapMockServer mockServer;

    @Autowired
    MailRepository repo;

    private StringBuffer txtBody;
    private StringBuffer htmlBody;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    // @Test
    public void testStartHeader2() throws Exception {

        MimeMessage m = new MimeMessageBuilder().build("src/test/resources/testBody.mail");

        mockServer.prepareMailBox("INBOX", m);

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.store.protocol", "imap");
        javaMailProperties.put("mail.smtp.auth", "true");
        javaMailProperties.put("mail.imap.port", "143");
        javaMailProperties.put("mail.transport.protocol", "smtp");
        javaMailProperties.put("mail.smtp.port", "3125");

        Session session = Session.getInstance(javaMailProperties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("drp15@sokol-web.de", "yeartwok1");
            }
        });

        Store store = session.getStore("imap");
        store.connect("sokol-web.de", "drp15@sokol-web.de", "yeartwok1");

        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);

        Message[] search = folder.getMessages(); // search(new
                                                 // MessageIDTerm(m.getMessageID()));

        MessageBuilder newMessageBuilder = new MessageServiceFactoryImpl().newMessageBuilder();
        MessageWriter writer = new DefaultMessageWriter();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        search[4].writeTo(output);

        InputStream decodedInput = new ByteArrayInputStream((output).toByteArray());

        MessageImpl parseMessage = (MessageImpl) newMessageBuilder.parseMessage(decodedInput);

        MessageWriter newMessageWriter = new MessageServiceFactoryImpl().newMessageWriter();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        newMessageWriter.writeMessage(parseMessage, out);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(out.toByteArray());

        MimeMessage mimeMessage = new MimeMessage(session, byteArrayInputStream);

        mimeMessage.setSubject("duplicate 1");

        folder.appendMessages(new Message[] { mimeMessage });

    }

    @Test
    public void testStartHeader() throws Exception {
        ContentHandler handler = new MyContentHandler();
        MimeConfig config = new MimeConfig();

        MimeStreamParser parser = new MimeStreamParser(config);
        parser.setContentHandler(handler);
        parser.setContentDecoding(true);

        InputStream instream = new FileInputStream("src/test/resources/spring_template.mail"); // AttachmentId.mail");
        try {
            parser.parse(instream);
        } finally {
            instream.close();
        }
    }

    // @Test
    public void testStartHeader1() throws Exception {
        MessageBuilder newMessageBuilder = new MessageServiceFactoryImpl().newMessageBuilder();
        MessageWriter writer = new DefaultMessageWriter();
        MessageImpl parseMessage = (MessageImpl) newMessageBuilder.parseMessage(new FileInputStream(new File(
                "src/test/resources/testBody.mail")));
        System.out.println(parseMessage.isMultipart());

        // System.out.println("\n\nBefore message:\n--------------------\n");
        writer.writeMessage(parseMessage, System.out);
        // System.out.println("\n\n--------------------\n");
        if (parseMessage.isMultipart()) {
            Multipart body = (Multipart) parseMessage.getBody();

            Body body1 = new BasicBodyFactory().binaryBody(new FileInputStream("src/test/resources/1.png"));

            BodyPart bodyPart = new BodyPart();
            bodyPart.setBody(body1, "image/png");
            bodyPart.setContentTransferEncoding("base64");
            bodyPart.setContentDisposition("attachment");
            bodyPart.setFilename("1.png");

            body.addBodyPart(bodyPart);

            List<Entity> bodyParts = body.getBodyParts();

            for (Entity e : bodyParts) {
                BodyPart bp = (BodyPart) e;

                System.out.println(bp.getMimeType());
                System.out.println(e.getClass() + " -> " + e);

            }

        } else {
            Body removeBody = parseMessage.removeBody();
            Multipart newMultipart = newMessageBuilder.newMultipart("mixed");
            System.out.println("body: " + removeBody.getClass());

            // String txtPart = getTxtPart(parseMessage);
            BodyPart p1 = new BodyPart();
            // Header h1 = new Header();
            // h1.addField(Fields.contentType("text/plain"));
            // p1.setHeader(h1);

            // removeBody.

            TextBody body = new BasicBodyFactory().textBody(removeBody.toString(), "UTF-8");

            BodyPart bodyPart = new BodyPart();
            bodyPart.setText(body);

            newMultipart.addBodyPart(p1);
            // newMultipart.addBodyPart(removeBody);
            // newMultipart.addBodyPart(bodyPart)

            parseMessage.setMultipart(newMultipart);
        }

        // System.out.println(parseMessage.isMultipart());

        // Print transformed message.
        // System.out.println("\n\nTransformed message:\n--------------------\n");
        writer.writeMessage(parseMessage, new FileOutputStream("test.eml"));
        parseMessage.dispose();
        // Messages should be disposed of when they are no longer needed.
        // Disposing of a message also disposes of all child elements (e.g. body
        // parts) of the message.
        // transformed.dispose();
    }

    // private void parseBodyParts(Multipart multipart) throws IOException {
    // List<Entity> bodyParts = multipart.getBodyParts();
    //
    // for (Entity part : bodyParts) {
    //
    // if (part.isMimeType("text/plain")) {
    // String txt = getTxtPart(part);
    // txtBody.append(txt);
    // } else if (part.isMimeType("text/html")) {
    // String html = getTxtPart(part);
    // htmlBody.append(html);
    // } else if (part.getDispositionType() != null &&
    // !part.getDispositionType().equals("")) {
    // // If DispositionType is null or empty, it means that it's
    // // multipart, not attached file
    // // attachments.add(part);
    // }
    //
    // // If current part contains other, parse it again by recursion
    // if (part.isMultipart()) {
    // parseBodyParts((Multipart) part.getBody());
    // }
    // }
    // }

    private String getTxtPart(Entity part) throws IOException {
        // Get content from body
        TextBody tb = (TextBody) part.getBody();

        if (tb == null) {
            return "";
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            tb.writeTo(baos);
            return new String(baos.toByteArray());
        }
    }

}
