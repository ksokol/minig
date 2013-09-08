package fr.aliasource.webmail.client.chat;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface PresenceIcons extends ClientBundle {

	@Source("away.png")
	public ImageResource away();

	@Source("online.png")
	public ImageResource online();

	@Source("dnd.png")
	public ImageResource dnd();

	@Source("connecting.png")
	public ImageResource unknown();

	@Source("xa.png")
	public ImageResource xa();

	@Source("chat.png")
	public ImageResource chat();

	@Source("sylvain.png")
	public ImageResource sylvain();
}
