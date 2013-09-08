package com.icegreen.greenmail.imap.commands;

import javax.mail.Flags;

import com.icegreen.greenmail.imap.ImapRequestLineReader;
import com.icegreen.greenmail.imap.ImapResponse;
import com.icegreen.greenmail.imap.ImapSession;
import com.icegreen.greenmail.imap.ImapSessionFolder;
import com.icegreen.greenmail.imap.ProtocolException;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.FolderListener;
import com.icegreen.greenmail.store.MessageFlags;
import com.icegreen.greenmail.store.SimpleStoredMessage;

public class PatchedStoreCommand extends SelectedStateCommand implements UidEnabledCommand {
    public static final String NAME = "STORE";
    public static final String ARGS = "<Message-set> ['+'|'-']FLAG[.SILENT] <flag-list>";

    private final StoreCommandParser parser = new StoreCommandParser();

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
        StoreDirective directive = parser.storeDirective(request);
        Flags flags = parser.flagList(request);
        parser.endLine(request);

        ImapSessionFolder mailbox = session.getSelected();
        // IdRange[] uidSet;
        // if (useUids) {
        // uidSet = idSet;
        // } else {
        // uidSet = mailbox.msnsToUids(idSet);
        // }
        // if (directive.getSign() < 0) {
        // mailbox.setFlags(flags, false, uidSet, directive.isSilent());
        // }
        // else if (directive.getSign() > 0) {
        // mailbox.setFlags(flags, true, uidSet, directive.isSilent());
        // }
        // else {
        // mailbox.replaceFlags(flags, uidSet, directive.isSilent());
        // }

        FolderListener silentListener = null;
        if (directive.isSilent()) {
            silentListener = mailbox;
        }

        // TODO do this in one hit.
        long[] uids = mailbox.getMessageUids();
        for (int i = 0; i < uids.length; i++) {
            long uid = uids[i];
            int msn = mailbox.getMsn(uid);

            if ((useUids && includes(idSet, uid)) || (!useUids && includes(idSet, msn))) {
                if (directive.getSign() < 0) {
                    mailbox.setFlags(flags, false, uid, silentListener, useUids);
                } else if (directive.getSign() > 0) {
                    mailbox.setFlags(flags, true, uid, silentListener, useUids);
                } else {
                    mailbox.replaceFlags(flags, uid, silentListener, useUids);
                }
            }
        }

        boolean omitExpunged = (!useUids);
        session.unsolicitedResponses(response, omitExpunged);
        response.commandComplete(this);
    }

    private void storeFlags(SimpleStoredMessage storedMessage, StoreDirective directive, Flags newFlags) {
        Flags messageFlags = storedMessage.getFlags();
        if (directive.getSign() == 0) {
            messageFlags.remove(MessageFlags.ALL_FLAGS);
            messageFlags.add(newFlags);
        } else if (directive.getSign() < 0) {
            messageFlags.remove(newFlags);
        } else if (directive.getSign() > 0) {
            messageFlags.add(newFlags);
        }
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

    private class StoreCommandParser extends PatchedCommandParser {
        StoreDirective storeDirective(ImapRequestLineReader request) throws ProtocolException {
            int sign = 0;
            boolean silent = false;

            char next = request.nextWordChar();
            if (next == '+') {
                sign = 1;
                request.consume();
            } else if (next == '-') {
                sign = -1;
                request.consume();
            } else {
                sign = 0;
            }

            String directive = consumeWord(request, new NoopCharValidator());
            if ("FLAGS".equalsIgnoreCase(directive)) {
                silent = false;
            } else if ("FLAGS.SILENT".equalsIgnoreCase(directive)) {
                silent = true;
            } else {
                throw new ProtocolException("Invalid Store Directive: '" + directive + "'");
            }
            return new StoreDirective(sign, silent);
        }
    }

    private class StoreDirective {
        private int sign;
        private boolean silent;

        public StoreDirective(int sign, boolean silent) {
            this.sign = sign;
            this.silent = silent;
        }

        public int getSign() {
            return sign;
        }

        public boolean isSilent() {
            return silent;
        }
    }
}