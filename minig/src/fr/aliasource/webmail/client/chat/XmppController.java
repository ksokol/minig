package fr.aliasource.webmail.client.chat;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.calclab.emite.core.client.bosh.BoshSettings;
import com.calclab.emite.core.client.bosh.Connection;
import com.calclab.emite.core.client.xmpp.session.Session;
import com.calclab.emite.core.client.xmpp.stanzas.Message;
import com.calclab.emite.core.client.xmpp.stanzas.Presence;
import com.calclab.emite.core.client.xmpp.stanzas.XmppURIFactory;
import com.calclab.suco.client.Suco;
import com.calclab.suco.client.events.Listener;
import com.google.gwt.core.client.GWT;

import fr.aliasource.webmail.client.rpc.GetSettings;

public class XmppController {

	private static XmppController inst;

	public static final XmppController get() {
		return inst;
	}

	public static final void init(HashMap<String, String> settings) {
		inst = new XmppController(settings);
	}

	private XmppURIFactory uriFactory;
	private ChatViewManager viewMgr;
	private List<IJabberListener> listeners;
	private Session session;

	private XmppController(HashMap<String, String> settings) {
		GWT.log("XmppController created", null);
		uriFactory = new XmppURIFactory();
		listeners = new LinkedList<IJabberListener>();

		viewMgr = new ChatViewManager();
		addJabberListener(viewMgr);

		String l = settings.get("frontend.login");
		String d = settings.get("frontend.domain");
		if (!l.contains("@") && d != null) {
			l = l + "@" + d;
		}
		String me = l;
		GWT.log("xmpp username: " + me, null);

		createJabberLink(settings, me);
	}

	private void createJabberLink(HashMap<String, String> settings, String me) {
		// Suco is a facade that give access to every emite component we need
		// ******** 0. Configure connection settings *********
		Connection connection = Suco.get(Connection.class);
		connection.setSettings(new BoshSettings("proxy", "localhost"));
		// ...but there's a module, BrowserModule, that allows to configure
		// the connections settings in the html directly

		// ******** 1. Session *********
		// Session is the emite component that allows us to login/logout among
		// other things
		session = Suco.get(Session.class);

		// Session.onStateChanged allows us to know the state of the session
		session.onStateChanged(new Listener<Session>() {
			@Override
			public void onEvent(Session s) {
				GWT.log("Session state: " + s.getState(), null);
			}
		});

		session.onMessage(new Listener<Message>() {
			@Override
			public void onEvent(final Message message) {
				if (message.getBody() != null) {
					GWT.log("Message arrived: " + message.getBody(), null);
					for (IJabberListener jl : listeners) {
						jl.onMessage(message.getFromAsString(), message
								.getBody());
					}
				}
			}
		});

		session.onPresence(new Listener<Presence>() {
			@Override
			public void onEvent(Presence p) {
				for (IJabberListener jl : listeners) {
					jl.onPresence(p);
				}
			}
		});

		session.login(uriFactory.parse(me), settings
				.get(GetSettings.XMPP_PASSWORD));
	}

	public void addJabberListener(IJabberListener jl) {
		listeners.add(jl);
	}

	public ChatViewManager getView() {
		return viewMgr;
	}

	public void sendMessage(String destination, String msg) {
		// TODO Auto-generated method stub
		GWT.log("=> " + destination + ": " + msg, null);
		session.send(new Message(msg, uriFactory.parse(destination)));
	}

}
