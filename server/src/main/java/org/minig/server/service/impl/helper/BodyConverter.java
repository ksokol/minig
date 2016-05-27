package org.minig.server.service.impl.helper;

import org.apache.james.mime4j.codec.DecoderUtil;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

//TODO
public class BodyConverter {

    public enum BodyType {
        TEXT, HTML, ATTACHMENT
    };

    public static Object get(Message msg, BodyType type, String... params) throws MessagingException, IOException {
        switch(type) {
        case TEXT:
            return getText(msg);
        case HTML:
            return getHtml(msg);
        case ATTACHMENT:
            return getAttachment(msg, params[0]);
        default:
            return "";
        }
    }

    /**
     * http://www.oracle.com/technetwork/java/javamail/faq/index.html#mainbody
     */
    private static String getText(Part p) throws MessagingException, IOException {
        // TODO
        if (p.isMimeType("text/plain")) {
            String s = (String) p.getContent();
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null) {
                        text = getText(bp);
                        if (text != null) {
                            return text;
                        }
                    }
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null) return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null) return s;
            }
        }
        return null;
    }

    /**
     * http://www.oracle.com/technetwork/java/javamail/faq/index.html#mainbody
     */
    private static String getHtml(Part p) throws MessagingException, IOException {
        // TODO
        if (p.isMimeType("text/html")) {
            String s = (String) p.getContent();
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart) p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null) {
                        text = getHtml(bp);
                        // if (text != null) {
                        // return text;
                        // }
                    }
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getHtml(bp);
                    if (s != null) return s;
                } else {
                    return getHtml(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getHtml(mp.getBodyPart(i));
                if (s != null) return s;
            }
        }

        return null;
    }

    /**
     * http://www.oracle.com/technetwork/java/javamail/faq/index.html#hasattach
     */
    private static Part getAttachment(Part p, String fileNameIn) throws MessagingException, IOException {
        // TODO
        String disp = p.getDisposition();

        if (disp == null || disp.equalsIgnoreCase(Part.ATTACHMENT)) {
            String fileName = p.getFileName();

            if (fileName != null) {
                String decodedFileName = DecoderUtil.decodeEncodedWords(fileName, null);
                if (decodedFileName.equals(fileNameIn)) {
                    return p;
                }
            }
        }

        if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {

                Part p1 = getAttachment(mp.getBodyPart(i), fileNameIn);

                if (p1 != null) {
                    return p1;
                }
            }
        }

        return null;
    }

}
