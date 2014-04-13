package org.minig.server.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.search.MessageIDTerm;

import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.MessageBuilder;
import org.apache.james.mime4j.dom.MessageWriter;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.message.BasicBodyFactory;
import org.apache.james.mime4j.message.DefaultMessageWriter;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.message.MessageServiceFactoryImpl;
import org.apache.james.mime4j.message.MultipartImpl;
import org.apache.james.mime4j.util.MimeUtil;
import org.minig.server.MailAttachment;
import org.minig.server.MailAttachmentList;
import org.minig.server.MailMessage;
import org.minig.server.service.*;
import org.minig.server.service.impl.helper.BodyConverter;
import org.minig.server.service.impl.helper.BodyConverter.BodyType;
import org.minig.server.service.impl.helper.MessageMapper;
import org.minig.server.service.impl.helper.mime.Mime4jMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class AttachmentRepositoryImpl implements AttachmentRepository {

    @Autowired
    private MailContext mailContext;

    // TODO
    @Autowired
    private MessageMapper mapper;

    @Override
    public MailAttachmentList readMetadata(CompositeId id) {
        Assert.notNull(id);
        // Assert.hasText(message.getId());
        // Assert.notNull(message.getAttachments());
        // Assert.hasText(message.getFolder());

        List<MailAttachment> metaDataList = new ArrayList<MailAttachment>();
        // List<CompositeAttachmentId> attachmentIdList =
        // message.getAttachments();
        // List<String> parsedAttachmentIdList = new ArrayList<String>();
        //
        // for (CompositeAttachmentId attachmentId : attachmentIdList) {
        // // TODO
        // String[] split = attachmentId.split("\\|");
        //
        // if (split != null && split.length == 3) {
        // parsedAttachmentIdList.add(split[2]);
        // }
        // }

        // TODO
        // String id = message.getId();
        // int idx = id.lastIndexOf("|");
        // String messageId = id.substring(idx + 1, id.length());

        try {
            Folder folder = mailContext.getFolder(id.getFolder());

            if (!folder.exists()) {
                return new MailAttachmentList();
            }

            Message[] search = folder.search(new MessageIDTerm(id.getMessageId()));

            if (search != null && search[0] != null) {
                Message mm = search[0];

                MailMessage message = mapper.convertAttachments(mm);

                for (CompositeAttachmentId attachmentId : message.getAttachments()) {
                    // TODO see RestTemplate
                    Part attachment = (Part) BodyConverter.get(mm, BodyType.ATTACHMENT, attachmentId.getFileName());

                    if (attachment != null) {
                        // TODO
                        MailAttachment metaData = new MailAttachment(); // message.getId()
                                                                        // + "|"
                                                                        // +
                                                                        // attachmentId);
                        metaData.setCompositeAttachmentId(attachmentId);
                        metaData.setFileName(attachment.getFileName());
                        metaData.setSize(attachment.getSize());
                        metaData.setMime(attachment.getContentType());

                        metaDataList.add(metaData);
                    }
                }
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        return new MailAttachmentList(metaDataList);
    }

    @Override
    public MailAttachment read(CompositeAttachmentId id) {
        Assert.notNull(id);

        MailAttachment metaData = null;

        // String parsedAttachmentId = null;

        // TODO
        // String[] split = attachmentId.split("\\|");
        //
        // if (split == null || split.length != 3) {
        // // parsedAttachmentId = split[2];
        // throw new IllegalArgumentException();
        // }

        try {
            Folder folder = mailContext.getFolder(id.getFolder());
            Message[] search = folder.search(new MessageIDTerm(id.getMessageId()));

            if (search != null && search.length == 1 && search[0] != null) {
                Message mm = search[0];

                // TODO see RestTemplate
                Part p = (Part) BodyConverter.get(mm, BodyType.ATTACHMENT, id.getFileName());

                if (p != null) {
                    // TODO
                    metaData = new MailAttachment();
                    metaData.setCompositeAttachmentId(id);
                    metaData.setFileName(p.getFileName());
                    metaData.setSize(p.getSize());
                    metaData.setMime(p.getContentType());
                }
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        return metaData;
    }

    @Override
    public InputStream readAttachmentPayload(CompositeAttachmentId id) {
        Assert.notNull(id);

        // //
        // http://stackoverflow.com/questions/1921981/imap-javax-mail-fetching-only-body-without-attachment
        // TODO
        // String[] split = attachmentId.split("\\|");
        //
        // if (split.length != 3) {
        // throw new IllegalArgumentException();
        // }

        try {
            Folder folder = mailContext.getFolder(id.getFolder());
            Message[] search = folder.search(new MessageIDTerm(id.getMessageId()));

            if (search != null && search[0] != null) {
                Message mm = search[0];

                Part p = (Part) BodyConverter.get(mm, BodyType.ATTACHMENT, id.getFileName());

                if (p != null) {
                    return p.getInputStream();
                }
            }

        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        throw new NotFoundException();
    }

    @Override
    public CompositeId appendAttachment(CompositeId id, DataSource dataSource) {
        Assert.notNull(id);
        Assert.notNull(dataSource);

        try {
            Folder sourceFolder = mailContext.getFolder(id.getFolder());
            Message[] search = sourceFolder.search(new MessageIDTerm(id.getMessageId()));

            if (search != null && search.length == 1) {

                MessageBuilder newMessageBuilder = new MessageServiceFactoryImpl().newMessageBuilder();
                MessageWriter writer = new DefaultMessageWriter();

                ByteArrayOutputStream output = new ByteArrayOutputStream();
                search[0].writeTo(output);

                InputStream decodedInput = new ByteArrayInputStream((output).toByteArray());

                MessageImpl parseMessage = (MessageImpl) newMessageBuilder.parseMessage(decodedInput);

                if (parseMessage.isMultipart()) {
                    org.apache.james.mime4j.dom.Multipart body = (org.apache.james.mime4j.dom.Multipart) parseMessage.getBody();

                    Body body1 = new BasicBodyFactory().binaryBody(dataSource.getInputStream());

                    org.apache.james.mime4j.message.BodyPart bodyPart = new org.apache.james.mime4j.message.BodyPart();
                    bodyPart.setBody(body1, dataSource.getContentType());
                    bodyPart.setContentTransferEncoding("base64");
                    bodyPart.setContentDisposition("attachment");
                    bodyPart.setFilename(dataSource.getName());

                    body.addBodyPart(bodyPart);

                } else {
                    TextBody tb = (TextBody) parseMessage.removeBody();

                    BasicBodyFactory basicBodyFactory = new BasicBodyFactory();

                    TextBody textBody = basicBodyFactory.textBody(tb.getInputStream(), parseMessage.getCharset());

                    Body body1 = new BasicBodyFactory().binaryBody(dataSource.getInputStream());

                    org.apache.james.mime4j.message.BodyPart bodyPart = new org.apache.james.mime4j.message.BodyPart();
                    bodyPart.setBody(body1, dataSource.getContentType());
                    bodyPart.setContentTransferEncoding("base64");
                    bodyPart.setContentDisposition("attachment");
                    bodyPart.setFilename(dataSource.getName());

                    org.apache.james.mime4j.message.BodyPart textBodyPart = new org.apache.james.mime4j.message.BodyPart();

                    if (parseMessage.isMimeType("text/html")) {
                        textBodyPart.setText(textBody, "html");
                    } else {
                        textBodyPart.setText(textBody);
                    }

                    org.apache.james.mime4j.dom.Multipart newBody = new MultipartImpl("mixed");

                    newBody.addBodyPart(textBodyPart);
                    newBody.addBodyPart(bodyPart);

                    Map<String, String> map = new HashMap<String, String>();

                    map.put("boundary", MimeUtil.createUniqueBoundary());

                    parseMessage.setBody(newBody, "multipart/mixed", map);
                }

                ByteArrayOutputStream output2 = new ByteArrayOutputStream();
                writer.writeMessage(parseMessage, output2);
                parseMessage.dispose();

                InputStream decodedInput2 = new ByteArrayInputStream((output2).toByteArray());

                MimeMessage mm = new MimeMessage(mailContext.getSession(), decodedInput2);
                mm.saveChanges();

                sourceFolder.appendMessages(new Message[] { mm });
                sourceFolder.close(false);

                CompositeId compositeId = new CompositeId();
                compositeId.setFolder(id.getFolder());
                compositeId.setMessageId(mm.getHeader("Message-ID")[0]);

                return compositeId;
            }
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage(), e);
        }

        throw new RepositoryException();
    }
}
