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

import java.util.ArrayList;
import java.util.List;

public class MailAttachmentList {

	private List<MailAttachment> attachmentMetadata;

	public MailAttachmentList() {
		this.attachmentMetadata = new ArrayList<MailAttachment>();
	}

	public MailAttachmentList(List<MailAttachment> attachmentMetadata) {
		this.attachmentMetadata = attachmentMetadata;
	}

	public List<MailAttachment> getAttachmentMetadata() {
		return attachmentMetadata;
	}

	public void setAttachmentMetadata(List<MailAttachment> attachmentMetadata) {
		this.attachmentMetadata = attachmentMetadata;
	}

}
