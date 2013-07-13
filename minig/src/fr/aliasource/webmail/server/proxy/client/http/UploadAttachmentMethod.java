/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 2.0
 *
 * The contents of this file are subject to the GNU General Public
 * License Version 2 or later (the "GPL").
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is
 *   MiniG.org project members
 *
 * ***** END LICENSE BLOCK ***** */

package fr.aliasource.webmail.server.proxy.client.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;

import fr.aliasource.webmail.client.shared.AttachmentMetadata;

/**
 * Pushes an attachment and its metadata to the backend
 * 
 * @author tom
 * 
 */
public class UploadAttachmentMethod extends AbstractClientMethod {

	protected String token;

	UploadAttachmentMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/uploadAttachment.do");
		this.token = token;
	}

	public void upload(String attachementId, AttachmentMetadata meta,
			InputStream attachement) {
		logger.info("Pushing attachment to backend. " + attachementId + " fn: "
				+ meta.getFileName() + " size: " + meta.getSize() + " mime: "
				+ meta.getMime());
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);

		params.put("id", attachementId);
		params.put("filename", meta.getFileName());
		params.put("size", "" + meta.getSize());
		params.put("mime", meta.getMime());

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			transfer(attachement, out, true);
			Base64 encoder = new Base64();
			String encoded = new String(encoder.encode(out.toByteArray()));
			out = null;
			params.put("content", encoded);
			executeVoid(params);
		} catch (Exception e) {
			logger.error("Error encoding attachment as base64", e);
		}
	}

	/**
	 * Fast stream transfer method
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void transfer(InputStream in, OutputStream out,
			boolean closeIn) throws IOException {
		final byte[] buffer = new byte[2048];

		try {
			while (true) {
				int amountRead = in.read(buffer);
				if (amountRead == -1) {
					break;
				}
				out.write(buffer, 0, amountRead);
			}
		} finally {
			if (closeIn) {
				in.close();
			}
			out.flush();
			out.close();
		}
	}
}
