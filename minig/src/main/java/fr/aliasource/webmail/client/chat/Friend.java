package fr.aliasource.webmail.client.chat;

import com.calclab.emite.core.client.xmpp.stanzas.Presence.Show;

public class Friend implements Comparable<Friend> {

	private String jid;
	private Show status;

	public Friend(String jid, Show status) {
		this.jid = XmppUtils.cutSessionIdFromJID(jid);
		this.status = status;
	}

	@Override
	public boolean equals(Object obj) {
		return jid.equals(((Friend) obj).jid);
	}

	@Override
	public int hashCode() {
		return jid.hashCode();
	}

	public Show getStatus() {
		return status;
	}

	public String getJabberId() {
		return jid;
	}

	@Override
	public int compareTo(Friend o) {
		return jid.compareTo(o.jid);
	}

}
