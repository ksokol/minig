package fr.aliasource.webmail.client.chat;

public final class XmppUtils {

	public static String cutSessionIdFromJID(String orig) {
		String jid = orig;
		int idx = jid.indexOf("/");
		if (idx > 0) {
			jid = jid.substring(0, idx);
		}
		return jid;
	}

}
