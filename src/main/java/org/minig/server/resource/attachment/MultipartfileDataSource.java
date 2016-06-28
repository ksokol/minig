package org.minig.server.resource.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import org.springframework.web.multipart.MultipartFile;

public class MultipartfileDataSource implements DataSource {

	private MultipartFile file;

	public MultipartfileDataSource(MultipartFile file) {
		this.file = file;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return this.file.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getContentType() {
		return file.getContentType();
	}

	@Override
	public String getName() {
		return this.file.getName();
	}

	// private String removePathElementsFromFilename(String filename) {
	// int startOfFilename = filename.lastIndexOf("\\");
	// if (startOfFilename != -1) {
	// return filename.substring(startOfFilename + 1);
	// }
	// return filename;
	// }
}
