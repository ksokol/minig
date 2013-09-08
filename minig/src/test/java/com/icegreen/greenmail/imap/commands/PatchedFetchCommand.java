package com.icegreen.greenmail.imap.commands;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.mail.Flags;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.icegreen.greenmail.imap.ImapRequestLineReader;
import com.icegreen.greenmail.imap.ImapResponse;
import com.icegreen.greenmail.imap.ImapSession;
import com.icegreen.greenmail.imap.ImapSessionFolder;
import com.icegreen.greenmail.imap.ProtocolException;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.MessageFlags;
import com.icegreen.greenmail.store.SimpleStoredMessage;
import com.icegreen.greenmail.util.PatchedGreenMailUtil;

public class PatchedFetchCommand extends FetchCommand {

    public static final String NAME = "FETCH";
    public static final String ARGS = "<message-set> <fetch-profile>";

    private FetchCommandParser parser = new FetchCommandParser();

    /**
     * @see CommandTemplate#doProcess
     */
    protected void doProcess(ImapRequestLineReader request, ImapResponse response, ImapSession session) throws ProtocolException,
            FolderException {
        doProcess(request, response, session, false);
    }

    public void doProcess(ImapRequestLineReader request, ImapResponse response, ImapSession session, boolean useUids)
            throws ProtocolException, FolderException {
        IdRange[] idSet = parser.parseIdRange(request);
        FetchRequest fetch = parser.fetchRequest(request);
        parser.endLine(request);

        if (useUids) {
            fetch.uid = true;
        }

        ImapSessionFolder mailbox = session.getSelected();
        long[] uids = mailbox.getMessageUids();
        for (int i = 0; i < uids.length; i++) {
            long uid = uids[i];
            int msn = mailbox.getMsn(uid);

            if ((useUids && includes(idSet, uid)) || (!useUids && includes(idSet, msn))) {
                SimpleStoredMessage storedMessage = mailbox.getMessage(uid);
                String msgData = outputMessage(fetch, storedMessage, mailbox, useUids);
                response.fetchResponse(msn, msgData);
            }
        }

        boolean omitExpunged = (!useUids);
        session.unsolicitedResponses(response, omitExpunged);
        response.commandComplete(this);
    }

    private String outputMessage(FetchRequest fetch, SimpleStoredMessage message, ImapSessionFolder folder, boolean useUids)
            throws FolderException, ProtocolException {
        // Check if this fetch will cause the "SEEN" flag to be set on this
        // message
        // If so, update the flags, and ensure that a flags response is included
        // in the response.
        boolean ensureFlagsResponse = false;
        if (fetch.isSetSeen() && !message.getFlags().contains(Flags.Flag.SEEN)) {
            folder.setFlags(new Flags(Flags.Flag.SEEN), true, message.getUid(), folder, useUids);
            message.getFlags().add(Flags.Flag.SEEN);
            ensureFlagsResponse = true;
        }

        StringBuffer response = new StringBuffer();

        // FLAGS response
        if (fetch.flags || ensureFlagsResponse) {
            response.append(" FLAGS ");
            response.append(MessageFlags.format(message.getFlags()));
        }

        // INTERNALDATE response
        if (fetch.internalDate) {
            response.append(" INTERNALDATE \"");
            // TODO format properly
            response.append(message.getAttributes().getInternalDateAsString());
            response.append("\"");

        }

        // RFC822.SIZE response
        if (fetch.size) {
            response.append(" RFC822.SIZE ");
            response.append(message.getAttributes().getSize());
        }

        // ENVELOPE response
        if (fetch.envelope) {
            response.append(" ENVELOPE ");
            response.append(message.getAttributes().getEnvelope());
        }

        // BODY response
        if (fetch.body) {
            response.append(" BODY ");
            response.append(message.getAttributes().getBodyStructure(false));
        }

        // BODYSTRUCTURE response
        if (fetch.bodyStructure) {
            response.append(" BODYSTRUCTURE ");
            response.append(message.getAttributes().getBodyStructure(true));
        }

        // UID response
        if (fetch.uid) {
            response.append(" UID ");
            response.append(message.getUid());
        }

        // BODY part responses.
        Collection elements = fetch.getBodyElements();
        for (Iterator iterator = elements.iterator(); iterator.hasNext();) {
            BodyFetchElement fetchElement = (BodyFetchElement) iterator.next();
            response.append(SP);
            response.append(fetchElement.getResponseName());
            if (null == fetchElement.getPartial()) {
                response.append(SP);
            }

            // Various mechanisms for returning message body.
            String sectionSpecifier = fetchElement.getParameters();

            MimeMessage mimeMessage = message.getMimeMessage();
            try {
                handleBodyFetch(mimeMessage, sectionSpecifier, fetchElement.getPartial(), response);
            } catch (Exception e) {
                // TODO chain exceptions
                throw new FolderException(e.getMessage());
            }
        }

        if (response.length() > 0) {
            // Remove the leading " ".
            return response.substring(1);
        } else {
            return "";
        }
    }

    private void handleBodyFetch(MimeMessage mimeMessage, String sectionSpecifier, String partial, StringBuffer response) throws Exception {
        if (sectionSpecifier.length() == 0) {
            // TODO - need to use an InputStream from the response here.
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            mimeMessage.writeTo(bout);
            byte[] bytes = bout.toByteArray();
            bytes = doPartial(partial, bytes, response);
            addLiteral(bytes, response);
        } else if (sectionSpecifier.equalsIgnoreCase("HEADER")) {
            Enumeration inum = mimeMessage.getAllHeaderLines();
            addHeaders(inum, response);
        } else if (sectionSpecifier.startsWith("HEADER.FIELDS.NOT")) {
            String[] excludeNames = extractHeaderList(sectionSpecifier, "HEADER.FIELDS.NOT".length());
            Enumeration inum = mimeMessage.getNonMatchingHeaderLines(excludeNames);
            addHeaders(inum, response);
        } else if (sectionSpecifier.startsWith("HEADER.FIELDS ")) {
            String[] includeNames = extractHeaderList(sectionSpecifier, "HEADER.FIELDS ".length());
            Enumeration inum = mimeMessage.getMatchingHeaderLines(includeNames);
            addHeaders(inum, response);
        } else if (sectionSpecifier.endsWith("MIME")) {
            String[] strs = sectionSpecifier.trim().split("\\.");
            int partNumber = Integer.parseInt(strs[0]) - 1;
            MimeMultipart mp = (MimeMultipart) mimeMessage.getContent();
            byte[] bytes = PatchedGreenMailUtil.getHeaderAsBytes(mp.getBodyPart(partNumber));
            bytes = doPartial(partial, bytes, response);
            addLiteral(bytes, response);
        } else if (sectionSpecifier.equalsIgnoreCase("TEXT")) {
            // TODO - need to use an InputStream from the response here.
            // TODO - this is a hack. To get just the body content, I'm using a
            // null
            // input stream to take the headers. Need to have a way of ignoring
            // headers.

            byte[] bytes = PatchedGreenMailUtil.getBodyAsBytes(mimeMessage);
            bytes = doPartial(partial, bytes, response);
            addLiteral(bytes, response);
        } else {
            String split[] = sectionSpecifier.split("\\.");
            List<Integer> sections = new ArrayList<Integer>();
            Part thePart = null;

            for (String s : split) {
                sections.add(Integer.valueOf(s) - 1);
            }

            MimeMultipart mp = (MimeMultipart) mimeMessage.getContent();
            byte[] bytes = null;

            for (Integer section : sections) {
                thePart = mp.getBodyPart(section);
                Object content = thePart.getContent();

                if (content instanceof MimeMultipart) {
                    mp = (MimeMultipart) content;
                } else if (content instanceof String) {
                    bytes = ((String) content).getBytes();
                }
            }

            MimeBodyPart bodyPart = (MimeBodyPart) mp.getBodyPart(0);
            Object content = bodyPart.getContent();

            if (bytes == null) {
                bytes = PatchedGreenMailUtil.getBodyAsBytes(thePart);
            }

            bytes = doPartial(partial, bytes, response);
            addLiteral(bytes, response);
        }
    }

    private byte[] doPartial(String partial, byte[] bytes, StringBuffer response) {
        if (null != partial) {
            String[] strs = partial.split("\\.");
            int start = Integer.parseInt(strs[0]);
            int len;
            if (2 == strs.length) {
                len = Integer.parseInt(strs[1]);
            } else {
                len = bytes.length;
            }

            start = Math.min(start, bytes.length);
            len = Math.min(len, bytes.length - start);
            byte[] newBytes = new byte[len];
            System.arraycopy(bytes, start, newBytes, 0, len);
            bytes = newBytes;
            response.append("<");
            response.append(start);
            response.append("> ");
        }
        return bytes;
    }

    private void addLiteral(byte[] bytes, StringBuffer response) {
        response.append('{');
        response.append(bytes.length);
        response.append('}');
        response.append("\r\n");

        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            response.append((char) b);
        }
    }

    // TODO should do this at parse time.
    private String[] extractHeaderList(String headerList, int prefixLen) {
        // Remove the trailing and leading ')('
        String tmp = headerList.substring(prefixLen + 1, headerList.length() - 1);
        String[] headerNames = split(tmp, " ");
        return headerNames;
    }

    private String[] split(String value, String delimiter) {
        ArrayList strings = new ArrayList();
        int startPos = 0;
        int delimPos;
        while ((delimPos = value.indexOf(delimiter, startPos)) != -1) {
            String sub = value.substring(startPos, delimPos);
            strings.add(sub);
            startPos = delimPos + 1;
        }
        String sub = value.substring(startPos);
        strings.add(sub);

        return (String[]) strings.toArray(new String[0]);
    }

    private void addHeaders(Enumeration inum, StringBuffer response) {
        List lines = new ArrayList();
        int count = 0;
        while (inum.hasMoreElements()) {
            String line = (String) inum.nextElement();
            count += line.length() + 2;
            lines.add(line);
        }
        response.append('{');
        response.append(count + 2);
        response.append('}');
        response.append("\r\n");

        Iterator lit = lines.iterator();
        while (lit.hasNext()) {
            String line = (String) lit.next();
            response.append(line);
            response.append("\r\n");
        }
        response.append("\r\n");
    }

    /**
     * @see ImapCommand#getName
     */
    public String getName() {
        return NAME;
    }

    /**
     * @see CommandTemplate#getArgSyntax
     */
    public String getArgSyntax() {
        return ARGS;
    }

    private class FetchCommandParser extends CommandParser {

        public FetchRequest fetchRequest(ImapRequestLineReader request) throws ProtocolException {
            FetchRequest fetch = new FetchRequest();

            char next = nextNonSpaceChar(request);
            consumeChar(request, '(');

            next = nextNonSpaceChar(request);
            while (next != ')') {
                addNextElement(request, fetch);
                next = nextNonSpaceChar(request);
            }

            consumeChar(request, ')');

            return fetch;
        }

        private void addNextElement(ImapRequestLineReader command, FetchRequest fetch) throws ProtocolException {
            char next = nextCharInLine(command);
            StringBuffer element = new StringBuffer();
            while (next != ' ' && next != '[' && next != ')') {
                element.append(next);
                command.consume();
                next = nextCharInLine(command);
            }
            String name = element.toString();
            // Simple elements with no '[]' parameters.
            if (next == ' ' || next == ')') {
                if ("FAST".equalsIgnoreCase(name)) {
                    fetch.flags = true;
                    fetch.internalDate = true;
                    fetch.size = true;
                } else if ("FULL".equalsIgnoreCase(name)) {
                    fetch.flags = true;
                    fetch.internalDate = true;
                    fetch.size = true;
                    fetch.envelope = true;
                    fetch.body = true;
                } else if ("ALL".equalsIgnoreCase(name)) {
                    fetch.flags = true;
                    fetch.internalDate = true;
                    fetch.size = true;
                    fetch.envelope = true;
                } else if ("FLAGS".equalsIgnoreCase(name)) {
                    fetch.flags = true;
                } else if ("RFC822.SIZE".equalsIgnoreCase(name)) {
                    fetch.size = true;
                } else if ("ENVELOPE".equalsIgnoreCase(name)) {
                    fetch.envelope = true;
                } else if ("INTERNALDATE".equalsIgnoreCase(name)) {
                    fetch.internalDate = true;
                } else if ("BODY".equalsIgnoreCase(name)) {
                    fetch.body = true;
                } else if ("BODYSTRUCTURE".equalsIgnoreCase(name)) {
                    fetch.bodyStructure = true;
                } else if ("UID".equalsIgnoreCase(name)) {
                    fetch.uid = true;
                } else if ("RFC822".equalsIgnoreCase(name)) {
                    fetch.add(new BodyFetchElement("RFC822", ""), false);
                } else if ("RFC822.HEADER".equalsIgnoreCase(name)) {
                    fetch.add(new BodyFetchElement("RFC822.HEADER", "HEADER"), true);
                } else if ("RFC822.TEXT".equalsIgnoreCase(name)) {
                    fetch.add(new BodyFetchElement("RFC822.TEXT", "TEXT"), false);
                } else {
                    throw new ProtocolException("Invalid fetch attribute: " + name);
                }
            } else {
                consumeChar(command, '[');

                StringBuffer sectionIdentifier = new StringBuffer();
                next = nextCharInLine(command);
                while (next != ']') {
                    sectionIdentifier.append(next);
                    command.consume();
                    next = nextCharInLine(command);
                }
                consumeChar(command, ']');

                String parameter = sectionIdentifier.toString();

                String partial = null;
                next = nextCharInLine(command);
                if ('<' == next) {
                    partial = "";
                    consumeChar(command, '<');
                    next = nextCharInLine(command);
                    while (next != '>') {
                        partial += next;
                        command.consume();
                        next = nextCharInLine(command);
                    }
                    consumeChar(command, '>');
                    next = nextCharInLine(command);
                }

                if ("BODY".equalsIgnoreCase(name)) {
                    fetch.add(new BodyFetchElement("BODY[" + parameter + "]", parameter, partial), false);
                } else if ("BODY.PEEK".equalsIgnoreCase(name)) {
                    fetch.add(new BodyFetchElement("BODY[" + parameter + "]", parameter, partial), true);
                } else {
                    throw new ProtocolException("Invalid fetch attibute: " + name + "[]");
                }
            }
        }

        private char nextCharInLine(ImapRequestLineReader request) throws ProtocolException {
            char next = request.nextChar();
            if (next == '\r' || next == '\n') {
                throw new ProtocolException("Unexpected end of line.");
            }
            return next;
        }

        private char nextNonSpaceChar(ImapRequestLineReader request) throws ProtocolException {
            char next = request.nextChar();
            while (next == ' ') {
                request.consume();
                next = request.nextChar();
            }
            return next;
        }

    }

    private static class FetchRequest {
        boolean flags;
        boolean uid;
        boolean internalDate;
        boolean size;
        boolean envelope;
        boolean body;
        boolean bodyStructure;

        private boolean setSeen = false;

        private Set bodyElements = new HashSet();

        public Collection getBodyElements() {
            return bodyElements;
        }

        public boolean isSetSeen() {
            return setSeen;
        }

        public void add(BodyFetchElement element, boolean peek) {
            if (!peek) {
                setSeen = true;
            }
            bodyElements.add(element);
        }
    }

    private class BodyFetchElement {
        private String name;
        private String sectionIdentifier;
        private String partial;

        public BodyFetchElement(String name, String sectionIdentifier) {
            this(name, sectionIdentifier, null);
        }

        public BodyFetchElement(String name, String sectionIdentifier, String partial) {
            this.name = name;
            this.sectionIdentifier = sectionIdentifier;
            this.partial = partial;
        }

        public String getParameters() {
            return this.sectionIdentifier;
        }

        public String getResponseName() {
            return this.name;
        }

        public String getPartial() {
            return partial;
        }
    }
}
