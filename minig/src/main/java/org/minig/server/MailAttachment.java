
package org.minig.server;

import org.minig.server.service.CompositeAttachmentId;

/**
 * @author Kamill Sokol
 */
public class MailAttachment extends CompositeAttachmentId {

	private String mime;

	public MailAttachment() {
        //empty
    }

	public String getMime() {
		return mime;
	}

	public void setMime(String mime) {
		this.mime = mime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		super.setId(id);
	}
}
