package fr.aliasource.webmail.client.chat;

import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.google.gwt.user.client.ui.Widget;

public class ChatViewManager implements IJabberListener, ITypingListener {

	private JabberRoster roster;
	private ChatWindows chat;

	public ChatViewManager() {
		this.roster = new JabberRoster(this);
		this.chat = new ChatWindows();
	}

	public Widget getRoster() {
		return roster;
	}

	@Override
	public void onMessage(String from, String text) {
		String jid = XmppUtils.cutSessionIdFromJID(from);
		ChatSession cs = chat.openChat(jid, (ITypingListener) this);
		cs.appendMessage(jid, text);
	}

	@Override
	public void onPresence(Presence p) {
		roster.onPresenceChange(p);
	}

	@Override
	public void messageComposed(ChatSession orig, String msg) {
		XmppController ctrl = XmppController.get();
		String destination = orig.getJID();
		ctrl.sendMessage(destination, msg);
	}

	@Override
	public void onConversationStart(String jid) {
		String who = XmppUtils.cutSessionIdFromJID(jid);
		chat.openChat(who, (ITypingListener) this);
	}

}
