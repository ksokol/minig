package org.minig.server.service.impl.helper.mime;

import org.apache.james.mime4j.message.MessageImpl;
import org.junit.Test;
import org.minig.server.TestConstants;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * @author Kamill Sokol
 */
public class Mime4jAttachmentMetadataExtractorTest {

	@Test
	public void testBinaryAttachment() throws Exception {
		MessageImpl message = Mime4jTestHelper.freshMessageImpl(TestConstants.MULTIPART_ATTACHMENT_BINARY);

		List<Mime4jAttachmentMetadata> attachments = Mime4jAttachmentMetadataExtractor.extract(message);
		assertThat(attachments, hasSize(1));

		Mime4jAttachmentMetadata attachment = attachments.get(0);

		assertThat(attachment.getSize(), is(150L));
		assertThat(attachment.getMimeType(), is("image/png"));
		assertThat(attachment.getFilename(), is("umlaut ä.png"));
	}

	@Test
	public void testPlaintextAttachment() throws Exception {
		MessageImpl message = Mime4jTestHelper.freshMessageImpl(TestConstants.MULTIPART_ATTACHMENT_PLAINTEXT);

		List<Mime4jAttachmentMetadata> attachments = Mime4jAttachmentMetadataExtractor.extract(message);
		assertThat(attachments, hasSize(1));

		Mime4jAttachmentMetadata attachment = attachments.get(0);

		assertThat(attachment.getSize(), is(greaterThan(0L)));
		assertThat(attachment.getMimeType(), is("text/plain"));
		assertThat(attachment.getFilename(), is("lyrics.txt"));
	}

	@Test
	public void testRFC2231() throws Exception {
		MessageImpl message = Mime4jTestHelper.freshMessageImpl(TestConstants.MULTIPART_RFC_2231);

		List<Mime4jAttachmentMetadata> attachments = Mime4jAttachmentMetadataExtractor.extract(message);
		assertThat(attachments, hasSize(1));

		Mime4jAttachmentMetadata attachment = attachments.get(0);

		assertThat(attachment.getSize(), is(150L));
		assertThat(attachment.getMimeType(), is("image/png"));
		assertThat(attachment.getFilename(), is("umlaut ä.png"));
	}

	@Test
	public void testRFC2231_2() throws Exception {
		MessageImpl message = Mime4jTestHelper.freshMessageImpl(TestConstants.MULTIPART_RFC_2231_2);

		List<Mime4jAttachmentMetadata> attachments = Mime4jAttachmentMetadataExtractor.extract(message);
		assertThat(attachments, hasSize(1));

		Mime4jAttachmentMetadata attachment = attachments.get(0);

		assertThat(attachment.getSize(), is(150L));
		assertThat(attachment.getMimeType(), is("image/png"));
		assertThat(attachment.getFilename(), is("umlaut ä veeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeery long.png"));
	}
}
