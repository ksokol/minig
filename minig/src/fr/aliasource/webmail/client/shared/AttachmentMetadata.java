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

package fr.aliasource.webmail.client.shared;

import java.io.Serializable;

public class AttachmentMetadata implements Serializable {

	private static final long serialVersionUID = -8238611251390052343L;

	private long size;
	private String fileName;
	private String mime;
	private String id;
	private String conversationId;
	private String conversationTitle;
	private long conversationDate;
	private boolean withPreview;
	private String previewMime;

	public AttachmentMetadata() {

	}

	public AttachmentMetadata(String id) {
		this.id = id;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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
		this.id = id;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	public String getConversationTitle() {
		return conversationTitle;
	}

	public void setConversationTitle(String conversationTitle) {
		this.conversationTitle = conversationTitle;
	}

	public long getConversationDate() {
		return conversationDate;
	}

	public void setConversationDate(long conversationDate) {
		this.conversationDate = conversationDate;
	}

	public boolean isWithPreview() {
		return withPreview;
	}

	public void setWithPreview(boolean withPreview) {
		this.withPreview = withPreview;
	}

	public String getPreviewMime() {
		return previewMime;
	}

	public void setPreviewMime(String previewMime) {
		this.previewMime = previewMime;
	}

}
