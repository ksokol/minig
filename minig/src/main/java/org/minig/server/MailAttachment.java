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

package org.minig.server;

import org.minig.server.service.CompositeAttachmentId;

public class MailAttachment extends CompositeAttachmentId {

	private long size;
	// private String fileName;
	private String mime;

	// private String id;

	public MailAttachment() {
	}

	// public MailAttachment(String id) {
	// this.id = id;
	// }

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	//
	// public String getFileName() {
	// return fileName;
	// }
	//
	// public void setFileName(String fileName) {
	// this.fileName = fileName;
	// }

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
		this.id = id;
	}
}
