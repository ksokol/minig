package fr.aliasource.webmail.client.reader;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ReaderImages extends ClientBundle {

	public static final ReaderImages imgs = GWT.create(ReaderImages.class);

	@Source("dropDown.gif")
	ImageResource dropDown();

	@Source("answered.gif")
	ImageResource answered();

	@Source("highPriority.gif")
	ImageResource highPriority();
}
