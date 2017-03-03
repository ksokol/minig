package org.minig.server.service.impl.helper.mime;

import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.codec.DecoderUtil;
import org.apache.james.mime4j.dom.BinaryBody;
import org.apache.james.mime4j.dom.Body;
import org.apache.james.mime4j.dom.Entity;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.dom.Multipart;
import org.apache.james.mime4j.dom.SingleBody;
import org.apache.james.mime4j.dom.TextBody;
import org.apache.james.mime4j.dom.address.Address;
import org.apache.james.mime4j.dom.address.Mailbox;
import org.apache.james.mime4j.field.Fields;
import org.apache.james.mime4j.message.BodyPart;
import org.apache.james.mime4j.message.BodyPartBuilder;
import org.apache.james.mime4j.message.DefaultMessageBuilder;
import org.apache.james.mime4j.message.DefaultMessageWriter;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.message.MessageServiceFactoryImpl;
import org.apache.james.mime4j.message.MultipartBuilder;
import org.apache.james.mime4j.message.SingleBodyBuilder;
import org.apache.james.mime4j.stream.Field;
import org.apache.james.mime4j.stream.RawField;
import org.minig.server.service.CompositeId;
import org.springframework.util.StringUtils;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.apache.james.mime4j.message.MessageBuilder.createCopy;
import static org.minig.MinigConstants.MESSAGE_ID;
import static org.minig.MinigConstants.MIME_TYPE_MESSAGE_RFC_822;
import static org.minig.MinigConstants.MIME_TYPE_MULTIPART_ALTERNATIVE;
import static org.minig.MinigConstants.MIME_TYPE_MULTIPART_MIXED;
import static org.minig.MinigConstants.MIME_TYPE_MULTIPART_RELATED;
import static org.minig.MinigConstants.MIME_TYPE_TEXT_HTML;
import static org.minig.MinigConstants.MIME_TYPE_TEXT_PLAIN;
import static org.minig.MinigConstants.PRIMARY_TYPE_TEXT;
import static org.minig.MinigConstants.SUB_TYPE_ALTERNATIVE;
import static org.minig.MinigConstants.SUB_TYPE_HTML;
import static org.minig.MinigConstants.SUB_TYPE_MIXED;
import static org.minig.MinigConstants.SUB_TYPE_PLAIN;

/**
 * @author Kamill Sokol
 */
final class MessageTransformer {

    private Message message;
    private CompositeId compositeId;

    MessageTransformer(CompositeId compositeId) {
        Objects.requireNonNull(compositeId, "compositeId is null");
        this.message = newMime4jMessage();
        this.compositeId = compositeId;
    }

    MessageTransformer(javax.mail.Message message) {
        Objects.requireNonNull(message, "message is null");
        this.message = withDefaults(toMime4jMessage(message));
        this.compositeId = createCompositeId(message);
    }

    CompositeId getCompositeId() {
        return compositeId;
    }

    void setText(String text) {
        Multipart alternative = getAlternative();
        removeBodyPartFrom(alternative, MIME_TYPE_TEXT_PLAIN);
        alternative.addBodyPart(toBodyPart(text, SUB_TYPE_PLAIN));
    }

    void setHtml(String html) {
        Multipart alternative = getAlternative();
        Integer bodyPartIndex = getIndexOfBodyPart(alternative.getBodyParts(), MIME_TYPE_MULTIPART_RELATED);

        if (bodyPartIndex != null) {
            //TODO Right now we do not support inline attachments for html body part
            alternative.removeBodyPart(bodyPartIndex);
        }

        removeBodyPartFrom(alternative, MIME_TYPE_TEXT_HTML);
        alternative.addBodyPart(toBodyPart(html, SUB_TYPE_HTML));
    }

    String getText() {
        return getBodyContent(MIME_TYPE_TEXT_PLAIN);
    }

    String getHtml() {
        return getBodyContent(MIME_TYPE_TEXT_HTML);
    }

    void setHeader(String key, String value) {
        if (!StringUtils.isEmpty(key)) {
            message.getHeader().setField(new RawField(key, value));
        }
    }

    String getHeader(String key) {
        Field field = message.getHeader().getField(key);
        return field == null ? null : field.getBody();
    }

    void removeHeader(String key) {
        message.getHeader().removeFields(key);
    }

    void setSubject(String value) {
        message = createCopy(message).setSubject(value).build();
    }

    String getSubject() {
        return message.getSubject();
    }

    void setDate(Date value) {
        message = createCopy(message).setDate(value).build();
    }

    String getFrom() {
        // we are always ensuring only one from/sender address
        return message.getFrom().get(0).getAddress();
    }

    void setFrom(String email) {
        Mailbox mailbox = toMailbox(email);
        message = createCopy(message).setFrom(mailbox).setSender(mailbox).build();
    }

    void addTo(String email) {
        message = createCopy(message).setTo(addToList(message.getTo(), toMailbox(email))).build();
    }

    void addCc(String email, String name) {
        message = createCopy(message).setCc(addToList(message.getCc(), toMailbox(email, name))).build();
    }

    void addBcc(String email, String name) {
        message = createCopy(message).setBcc(addToList(message.getBcc(), toMailbox(email, name))).build();
    }

    void clearTo() {
        message = createCopy(message).setTo((Address) null).build();
    }

    void clearCc() {
        message = createCopy(message).setCc(Collections.emptyList()).build();
    }

    void clearBcc() {
        message = createCopy(message).setBcc(Collections.emptyList()).build();
    }

    List<Mime4jAttachment> getAttachments() {
        return message.isMultipart() ? extractFromMultipart((Multipart) message.getBody()) : Collections.emptyList();
    }

    void addAttachment(DataSource dataSource) {
        transformToMixed().addBodyPart(toBinaryBodyPart(dataSource));
    }

    void deleteAttachment(String filename) {
        if (message.isMultipart()) {
            deleteAttachment((Multipart) message.getBody(), filename);
        }
    }

    MimeMessage toMessage() {
        return rethrowCheckedAsUnchecked(() -> {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            new DefaultMessageWriter().writeMessage(message, outputStream);
            return new MimeMessage(null, new ByteArrayInputStream(outputStream.toByteArray()));
        });
    }

    private Multipart transformToMixed() {
        if(MIME_TYPE_MULTIPART_MIXED.equalsIgnoreCase(message.getMimeType())) {
            return (Multipart) message.getBody();
        }

        MultipartBuilder mixedBuilder = MultipartBuilder.create(SUB_TYPE_MIXED);

        if (MIME_TYPE_MULTIPART_ALTERNATIVE.equalsIgnoreCase(message.getMimeType())) {
            mixedBuilder.addBodyPart(BodyPartBuilder.create()
                    .setBody((Multipart) message.removeBody())
                    .build());
        } else {
            mixedBuilder.addBodyPart(BodyPartBuilder.create()
                    .setBody((TextBody) message.removeBody())
                    .setContentType(message.getMimeType())
                    .build());
        }

        Multipart mixed = mixedBuilder.build();
        message = createCopy(message).setBody(mixed).build();
        return mixed;
    }

    private Multipart transformToAlternative(Multipart multipart) {
        MultipartBuilder alternativeBuilder = MultipartBuilder.create(SUB_TYPE_ALTERNATIVE);

        for (int i = 0; i < multipart.getBodyParts().size(); i++) {
            Entity entity = multipart.getBodyParts().get(i);
            if (entity.getMimeType().startsWith(PRIMARY_TYPE_TEXT)) {
                alternativeBuilder.addBodyPart(multipart.removeBodyPart(i));
            }
        }

        Multipart alternative = alternativeBuilder.build();
        multipart.addBodyPart(BodyPartBuilder.create().setBody(alternative).build());
        return alternative;
    }

    private Multipart getAlternative() {
        Multipart multipart = transformToMixed();
        Integer alternativePartIndex = getIndexOfBodyPart(multipart.getBodyParts(), MIME_TYPE_MULTIPART_ALTERNATIVE);

        if (alternativePartIndex != null) {
            return (Multipart) multipart.getBodyParts().get(alternativePartIndex).getBody();
        }

        return transformToAlternative(multipart);
    }

    private String getBodyContent(String mimeType) {
        if (mimeType.equalsIgnoreCase(message.getMimeType())) {
            return getReadablePart((TextBody) message.getBody());
        } else if (message.isMultipart()) {
            return getReadablePart(getBodyPart((Multipart) message.getBody(), mimeType));
        }
        return "";
    }

    private static CompositeId createCompositeId(javax.mail.Message message) {
        return rethrowCheckedAsUnchecked(() -> new CompositeId(message.getFolder().getFullName(), message.getHeader(MESSAGE_ID)[0]));
    }

    private static Message withDefaults(Message message) {
        if (message.getBody() == null) {
            message.setBody(emptyTextBody());
        }
        return message;
    }

    private static void removeBodyPartFrom(Multipart multipart, String contentType) {
        Integer bodyPartIndex = getIndexOfBodyPart(multipart.getBodyParts(), contentType);
        if (bodyPartIndex != null) {
            multipart.removeBodyPart(bodyPartIndex);
        }
    }

    private static List<Address> addToList(List<Address> source, Mailbox mailbox) {
        List<Address> target = new ArrayList<>(source == null ? new ArrayList<>() : source);
        target.add(mailbox);
        return target;
    }

    private static Mailbox toMailbox(String localPart, String domain) {
        return domain == null ? toMailbox(localPart) : new Mailbox(localPart, domain);
    }

    private static Mailbox toMailbox(String address) {
        if (countMatches(address, '@') != 1) {
            throw new IllegalArgumentException("not a valid address");
        }
        String[] split = address.split("@");
        return new Mailbox(split[0], split[1]);
    }

    private static void deleteAttachment(Multipart multipart, String filename) {
        List<Entity> e = multipart.getBodyParts();

        for (int i = 0; i < e.size(); i++) {
            BodyPart part = (BodyPart) e.get(i);

            if ("attachment".equalsIgnoreCase(part.getDispositionType()) && filename.equals(part.getFilename())) {
                multipart.removeBodyPart(i);
            }

            if (part.isMultipart()) {
                deleteAttachment((Multipart) part.getBody(), filename);
            }
        }
    }

    private static Integer getIndexOfBodyPart(List<Entity> bodyParts, String mimeType) {
        for (int partIndex = 0; partIndex < bodyParts.size(); partIndex++) {
            if (mimeType.equals(bodyParts.get(partIndex).getMimeType())) {
                return partIndex;
            }
        }
        return null;
    }

    private static TextBody getBodyPart(Multipart multipart, String mimeType) {
        List<Entity> bodyParts = multipart.getBodyParts();

        for (Entity entity : bodyParts) {
            TextBody textBody = null;

            if (mimeType.equals(entity.getMimeType())) {
                textBody = (TextBody) entity.getBody();
            }

            if (entity.isMultipart()) {
                textBody = getBodyPart((Multipart) entity.getBody(), mimeType);
            }

            if (textBody != null) {
                return textBody;
            }
        }

        return null;
    }

    private static String getReadablePart(TextBody part) {
        if (part == null) {
            return "";
        }
        return rethrowCheckedAsUnchecked(() -> {
            String mimeCharset = part.getMimeCharset() == null || "us-ascii".equalsIgnoreCase(part.getMimeCharset()) ? UTF_8.name() : part.getMimeCharset();
            return IOUtils.toString(part.getInputStream(), mimeCharset);
        });
    }

    private static BodyPart toBodyPart(String text, String subtype) {
        return rethrowCheckedAsUnchecked(() -> BodyPartBuilder.create().setBody(text == null ? "" : text, subtype, UTF_8).setContentType("text/" + subtype).build());
    }

    private static Body emptyTextBody() {
        return rethrowCheckedAsUnchecked(() -> SingleBodyBuilder.create().setText("").setCharset(UTF_8).build());
    }

    private static BodyPart toBinaryBodyPart(DataSource dataSource) {
        return BodyPartBuilder.create()
                .setContentTransferEncoding("base64")
                .setField(Fields.contentDisposition("attachment", dataSource.getName()))
                .setContentType(dataSource.getContentType())
                .setBody(toBinaryBody(dataSource))
                .build();
    }

    private static BinaryBody toBinaryBody(DataSource dataSource) {
        return rethrowCheckedAsUnchecked(() -> SingleBodyBuilder.create().readFrom(dataSource.getInputStream()).buildBinary());
    }

    private static Message newMime4jMessage() {
        return withDefaults(new MessageServiceFactoryImpl().newMessageBuilder().newMessage());
    }

    private static Message toMime4jMessage(javax.mail.Message message) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            message.writeTo(output);
            return new DefaultMessageBuilder().parseMessage(new ByteArrayInputStream(output.toByteArray()));
        } catch (MessagingException | IOException exception) {
            throw new IllegalArgumentException(exception.getMessage(), exception);
        }
    }

    private static List<Mime4jAttachment> extractFromMultipart(Multipart multipart) {
        List<BodyPart> rawAttachments = getAttachments(multipart);
        List<Mime4jAttachment> attachments = new ArrayList<>(rawAttachments.size());
        for (BodyPart bodyPart : rawAttachments) {
            attachments.addAll(toMime4jAttachment(bodyPart));
        }
        return attachments;
    }

    private static List<BodyPart> getAttachments(Multipart multipart) {
        List<BodyPart> attachments = new ArrayList<>();

        for (Entity entity : multipart.getBodyParts()) {
            if ("attachment".equalsIgnoreCase(entity.getDispositionType())) {
                attachments.add((BodyPart) entity);
            }
            if (entity.isMultipart()) {
                List<BodyPart> getAttachments = getAttachments((Multipart) entity.getBody());
                attachments.addAll(getAttachments);
            }
            if (MIME_TYPE_MESSAGE_RFC_822.equals(entity.getMimeType())) {
                attachments.add((BodyPart) entity);
            }
        }

        return attachments;
    }

    private static List<Mime4jAttachment> toMime4jAttachment(BodyPart bodyPart) {
        if (bodyPart.getBody() instanceof SingleBody) {
            return Collections.singletonList(extractFromSingleBody(bodyPart));
        }
        if (bodyPart.getBody() instanceof MessageImpl) {
            Mime4jAttachment mime4jAttachmentData = extractFromMessage((MessageImpl) bodyPart.getBody());
            if (mime4jAttachmentData.getFilename() == null) {
                mime4jAttachmentData.setFilename(getFileName(bodyPart));
            }
            return Collections.singletonList(mime4jAttachmentData);
        }
        throw new IllegalArgumentException("unknown bodyPart " + bodyPart.getClass());
    }

    private static Mime4jAttachment extractFromSingleBody(BodyPart bodyPart) {
        SingleBody source = (SingleBody) bodyPart.getBody();
        String mimeType = bodyPart.getMimeType();
        String filename = getFileName(bodyPart);
        return rethrowCheckedAsUnchecked(() -> new Mime4jAttachment(filename, mimeType, source.getInputStream()));
    }

    private static Mime4jAttachment extractFromMessage(Message message) {
        return MIME_TYPE_TEXT_PLAIN.equals(message.getMimeType()) ? extractFromSingleBody(message) : null;
    }

    private static Mime4jAttachment extractFromSingleBody(Message message) {
        SingleBody source = (SingleBody) message.getBody();
        String mimeType = message.getMimeType();
        String filename = String.format("%s.eml", message.getSubject());
        return rethrowCheckedAsUnchecked(() -> new Mime4jAttachment(filename, mimeType, source.getInputStream()));
    }

    private static String getFileName(BodyPart bodyPart) {
        if (bodyPart.getFilename() != null) {
            return DecoderUtil.decodeEncodedWords(bodyPart.getFilename(), (DecodeMonitor) null);
        } else {
            //TODO remove me after https://issues.apache.org/jira/browse/MIME4J-109 has been implemented
            Field field = bodyPart.getHeader().getField("Content-Disposition");
            return new RFC2231Decoder().parse(field.getBody());
        }
    }

    @FunctionalInterface
    interface SupplierWithExceptions<T, E extends Exception> {
        T get() throws E;
    }

    private static <R, E extends Exception> R rethrowCheckedAsUnchecked(SupplierWithExceptions<R, E> supplier) {
        try {
            return supplier.get();
        }
        catch (Exception exception) {
            throwAsUnchecked(exception);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static <E extends Throwable> void throwAsUnchecked(Exception exception) throws E {
        throw (E) exception;
    }
}
