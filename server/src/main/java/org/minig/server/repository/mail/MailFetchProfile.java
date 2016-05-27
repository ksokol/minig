package org.minig.server.repository.mail;

import javax.mail.FetchProfile;

/**
 * @author Kamill Sokol
 */
public final class MailFetchProfile {
    private MailFetchProfile() {}

    public static FetchProfile overview() {
        FetchProfile fp = new FetchProfile();
        fp.add(FetchProfile.Item.ENVELOPE);
        fp.add(FetchProfile.Item.FLAGS);
        fp.add(FetchProfile.Item.CONTENT_INFO);
        fp.add("X-Mozilla-Draft-Info");
        fp.add("$MDNSent");
        fp.add("$Forwarded");
        fp.add("X-PRIORITY");
        fp.add("Message-ID");
        return fp;
    }

    public static FetchProfile details() {
        FetchProfile fp = overview();
        fp.add("Disposition-Notification-To");
        fp.add("In-Reply-To");
        fp.add("X-Forwarded-Message-Id");
        fp.add("User-Agent");
        return fp;
    }

}
