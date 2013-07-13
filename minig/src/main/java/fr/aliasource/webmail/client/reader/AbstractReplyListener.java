package fr.aliasource.webmail.client.reader;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.composer.IQuickReplyListener;
import fr.aliasource.webmail.client.composer.QuickReply;

public abstract class AbstractReplyListener implements IQuickReplyListener {

	protected Widget replyActions;
	protected VerticalPanel vp;
	protected MessageWidget mw;
	protected QuickReply qr;
	protected Widget notification;

	protected AbstractReplyListener(MessageWidget mw, Widget replyActions,
			VerticalPanel vp) {
		this.replyActions = replyActions;
		this.vp = vp;
		this.mw = mw;
	}

	@Override
	public void onClick(ClickEvent event) {
		mw.setReplyMode(true);
		mw.setOpen(true);

		vp.remove(replyActions);
		if (qr == null) {
			qr = new QuickReply(mw.getUi(), this);
		}
		vp.add(qr);
		if (notification != null) {
			mw.getUi().clearNotification(notification);
		}

		doClick(event);
	}

	public abstract void doClick(ClickEvent event);

	@Override
	public void discard() {
		vp.remove(qr);
		vp.add(replyActions);
		mw.setReplyMode(false);
	}

	@Override
	public void setNotification(Widget notification) {
		this.notification = notification;
	}
}
