package fr.aliasource.webmail.client.conversations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

import fr.aliasource.webmail.client.IDestroyable;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.ConversationId;

public class GripImage extends Image implements IDestroyable {

	private ConversationId convId;
	public static Image img = GWT.create(Image.class);

	public interface Image extends ClientBundle {
		@Source("grippy.gif")
		public ImageResource grippy();
	}

	public GripImage(Conversation data) {
		setUrl(img.grippy().getURL());
		this.convId = data.getId();
		WebmailController.get().getView().getDragController().makeDraggable(this);
	}

	public ConversationId getConvId() {
		return convId;
	}

	public void destroy() {
		this.convId = null;
		WebmailController.get().getView().getDragController().makeNotDraggable(
				this);
	}

}
