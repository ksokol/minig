package org.minig.server.service.impl.helper;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.search.FlagTerm;
import javax.mail.search.SearchTerm;

public class UnreadMessageSearch extends SearchTerm {

    private static final long serialVersionUID = 1L;

    private FlagTerm term = new FlagTerm(new Flags(Flags.Flag.DELETED), false);

    @Override
    public boolean match(Message msg) {
        return term.match(msg);
    }

}
