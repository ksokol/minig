package org.minig.server.service.submission;

import com.sun.mail.dsn.DeliveryStatus;
import com.sun.mail.dsn.MultipartReport;
import org.minig.MailAuthentication;
import org.minig.server.service.CompositeId;
import org.minig.server.service.MailRepository;
import org.minig.server.service.impl.MailContext;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.MessageFormat;

/**
 * @author Kamill Sokol
 */
@Component
class DispositionServiceImpl implements DispositionService {

    // TODO
    private static final String TMPL = "This is a Return Receipt for the mail that you sent to {0}. \r\n\r\n"
            + "Note: This Return Receipt only acknowledges that the message was displayed on the recipients computer. There is no guarantee that the recipient has read or understood the message contents.";

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private MailContext mailContext;

    @Autowired
    private MailAuthentication mailAuthentication;

    @Autowired
    private JavaMailSenderFactory javaMailSenderFactory;

    @Override
    public void sendDisposition(CompositeId id) {
        Mime4jMessage message = mailRepository.read(id.getFolder(), id.getMessageId());

        if (message == null) {
            return;
        }
        MimeMessage mimeMessage = message.toMessage();

        try {
            MimeMessage msg = new MimeMessage(mailContext.getSession());
            String format = MessageFormat.format(TMPL, mailAuthentication.getEmailAddress());

            MultipartReport multipart = new MultipartReport(format, new DeliveryStatus(), mimeMessage);
            msg.setContent(multipart);
            msg.setSubject("Return Receipt (displayed) - " + mimeMessage.getSubject());
            msg.setRecipient(RecipientType.TO, mimeMessage.getFrom()[0]);

            submit(msg);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void submit(MimeMessage message) {
        JavaMailSender mailHelper = javaMailSenderFactory.newInstance(mailContext.getSession());

        try {
            // always set current authenticated user as sender for security reasons
            InternetAddress internetAddress = new InternetAddress(mailAuthentication.getEmailAddress());
            message.setFrom(internetAddress);
            mailHelper.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
