package fr.aliasource.webmail.client.composer;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

public interface IQuickReplyListener extends ClickHandler {

	void discard();

	void setNotification(Widget w);

}
