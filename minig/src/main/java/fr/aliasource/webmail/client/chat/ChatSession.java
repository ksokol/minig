package fr.aliasource.webmail.client.chat;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.shared.chat.History;
import fr.aliasource.webmail.client.shared.chat.HistoryItem;

public class ChatSession extends FlexTable {

	private FlexTable main;
	private HandlerRegistration reg;
	private String jid;
	private ITypingListener tl;
	private HandlerRegistration kp;
	private ScrollPanel sp;

	private History history;

	private static final DateTimeFormat dateFormat = DateTimeFormat
			.getFormat("hh:mm:ss");

	public ChatSession(final String jid, final ChatWindows chatWindow) {
		addStyleName("chatSession");

		history = new History();

		this.jid = jid;
		HorizontalPanel header = new HorizontalPanel();
		header.setSpacing(2);
		Label title = new Label(jid);
		header.add(title);
		Anchor close = new Anchor("X", true);
		header.add(close);
		setWidget(0, 0, header);

		sp = new ScrollPanel();
		DOM.setStyleAttribute(sp.getElement(), "overflowX", "hidden");
		DOM.setStyleAttribute(sp.getElement(), "overflowY", "scroll");
		setWidget(1, 0, sp);
		sp.setHeight("150px");
		main = new FlexTable();
		sp.add(main);
		main.addStyleName("whiteBackground");
		main.setSize("100%", "100%");

		HTML spacer = new HTML("&nbsp;");
		spacer.setHeight("100%");
		main.setWidget(0, 0, spacer);
		main.getCellFormatter().setHeight(0, 0, "100%");

		final TextArea typing = new TextArea();
		setWidget(2, 0, typing);
		typing.setWidth("100%");
		typing.setVisibleLines(2);

		this.kp = typing.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					typing.cancelKey();
					String t = typing.getText();
					tl.messageComposed(ChatSession.this, t);
					typing.setText("");
					appendMessage("me", t);
				}
			}
		});

		this.reg = close.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				reg.removeHandler();
				kp.removeHandler();
				chatWindow.closeChat(jid, ChatSession.this);
			}
		});

		setWidth("300px");
		DOM.setStyleAttribute(main.getElement(), "tableLayout", "fixed");
		DOM.setStyleAttribute(getElement(), "tableLayout", "fixed");

		typing.setFocus(true);
	}

	public void appendMessage(final String from, String text) {
		history.add(new HistoryItem(from, text));

		final StringBuilder formatted = new StringBuilder();
		String f = from;
		int idx = f.indexOf("@");
		if (idx > 0) {
			f = f.substring(0, idx);
		}
		Date d = new Date();
		formatted.append("[");
		formatted.append(dateFormat.format(d));
		formatted.append("]&nbsp;<b>");
		formatted.append(f);
		formatted.append(":</b>&nbsp;");

		AjaxCall.composerParser.beautifyText(text, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				formatted.append(result);

				if (!"me".equals(from)) {
					notifyUser(formatted);
				}

				HTML msg = new HTML(formatted.toString());
				DOM.setStyleAttribute(msg.getElement(), "wordWrap",
						"break-word");
				int newRow = main.getRowCount() - 1;
				main.insertRow(newRow);
				main.setWidget(newRow, 0, msg);
				sp.scrollToBottom();
			}

			private void notifyUser(final StringBuilder formatted) {
				Window.setTitle(I18N.strings.newChatMessage());

				formatted
						.append("<embed src=\"msg_received.wav\" visible=\"false\" hidden=\"true\" autostart=\"true\" loop=\"false\"/>");
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	public String getJID() {
		return jid;
	}

	public void setTypingListener(ITypingListener iTypingListener) {
		this.tl = iTypingListener;
	}

	public History getHistory() {
		return history;
	}

}
