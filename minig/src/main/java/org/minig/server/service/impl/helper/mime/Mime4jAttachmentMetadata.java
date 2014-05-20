package org.minig.server.service.impl.helper.mime;

/**
 * @author Kamill Sokol
 */
final class Mime4jAttachmentMetadata {
	private String filename;
	private long size;
	private String mimeType;

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
}
