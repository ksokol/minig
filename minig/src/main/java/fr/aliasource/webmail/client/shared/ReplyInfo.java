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


/**
 * Informations about the email we are replying to
 * 
 * @author tom
 * 
 */
public class ReplyInfo implements Serializable {

	private static final long serialVersionUID = -5609663487941248832L;

	private Folder origFolder;
	private MessageId id;
	private ConversationId convId;

	public ReplyInfo() {

	}

	public ReplyInfo(Folder origFolder, MessageId id, ConversationId convId) {
		this.origFolder = origFolder;
		this.id = id;
		this.convId = convId;
	}

	public MessageId getId() {
		return id;
	}

	public Folder getOrigFolder() {
		return origFolder;
	}

	public void setId(MessageId id) {
		this.id = id;
	}

	public void setOrigFolder(Folder origFolder) {
		this.origFolder = origFolder;
	}

	public ConversationId getConvId() {
		return convId;
	}

}
