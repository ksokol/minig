package org.minig.server.service.impl.helper.mime;

import java.io.InputStream;

/**
 * @author Kamill Sokol
 */
final class Mime4jAttachmentData {
	private String filename;
	private String mimeType;
	private InputStream data;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public InputStream getData() {
		return data;
	}

	public void setData(InputStream data) {
		this.data = data;
	}
}
