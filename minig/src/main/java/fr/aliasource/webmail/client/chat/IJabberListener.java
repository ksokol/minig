package fr.aliasource.webmail.client.chat;

import com.calclab.emite.core.client.xmpp.stanzas.Presence;

public interface IJabberListener {

	void onMessage(String from, String text);

	void onPresence(Presence p);

	void onConversationStart(String jid);

}
