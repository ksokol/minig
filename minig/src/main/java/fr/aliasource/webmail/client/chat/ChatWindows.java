package fr.aliasource.webmail.client.chat;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.chat.History;

/**
 * Manages MiniG active chat sessions and their screen positionning.
 * 
 * @author tom
 * 
 */
public class ChatWindows {

	private Map<String, ChatSession> sessions;
	private HorizontalPanel hp;

	public ChatWindows() {
		super();
		sessions = new HashMap<String, ChatSession>();
		hp = new HorizontalPanel();
		hp.setSpacing(5);
		RootPanel rp = RootPanel.get();
		rp.add(hp);
		DOM.setStyleAttribute(hp.getElement(), "position", "fixed");
		DOM.setStyleAttribute(hp.getElement(), "bottom", "0");
		DOM.setStyleAttribute(hp.getElement(), "right", "0");
	}

	public ChatSession openChat(String jid, ITypingListener iTypingListener) {

		ChatSession cs = sessions.get(jid);
		if (cs == null) {
			cs = new ChatSession(jid, this);
			sessions.put(jid, cs);
			cs.setTypingListener(iTypingListener);
			hp.add(cs);
			hp.setVisible(true);
		}
		return cs;
	}

	public void closeChat(String jid, ChatSession cs) {
		History history = cs.getHistory();
		if (!history.isEmpty()) {
			AjaxCall.chatService.storeHistory(history,
					new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							WebmailController.get().getView().notifyUser(
									I18N.strings.chatHistorySaved());
						}

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("error", caught);
							WebmailController.get().getView().notifyUser(
									"Error: " + caught.getMessage());
						}
					});
		}

		sessions.remove(jid);
		hp.remove(cs);
		hp.setVisible(hp.getWidgetCount() != 0);
	}

}
