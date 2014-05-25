package org.minig.server.service.impl.helper.mime;

import java.io.InputStream;

/**
 * @author Kamill Sokol
 */
final class Mime4jAttachmentData {
	private String filename;
	private long size;
	private String mimeType;
	private InputStream data;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
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
