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

package fr.aliasource.webmail.server;

import fr.aliasource.webmail.client.rpc.AttachementsManager;
import fr.aliasource.webmail.client.shared.AttachmentList;
import fr.aliasource.webmail.client.shared.AttachmentMetadata;
import fr.aliasource.webmail.client.shared.Folder;

public class AttachmentsManagerImpl extends SecureAjaxCall implements
		AttachementsManager {

	private static final long serialVersionUID = -9146036887977798186L;

	@Override
	public String allocateAttachementId() {
		return getAccount().allocateAttachementId();
	}

	@Override
	public void dropAttachement(String[] attachementId) {
		getAccount().dropAttachements(attachementId);
	}

	@Override
	public AttachmentMetadata[] getAttachementMetadata(String[] attachementId) {
		return getAccount().getAttachementsMetadata(attachementId);
	}

	@Override
	public AttachmentList list(Folder f, int page, int pageLength) {
		return getAccount().getAttachmentList(f, page, pageLength);
	}

}
