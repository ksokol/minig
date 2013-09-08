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
 * Folder used by the folder cloud. This a folder decorated with the unread
 * conversations count.
 * 
 * @author tom
 * 
 */
public class CloudyFolder extends Folder implements Serializable {

	private static final long serialVersionUID = -1718592289937611472L;

	private int unreadCount;

	public CloudyFolder() {
		this("XOBNI", "XOBNI", 0);
	}

	public CloudyFolder(String name, String displayName, int unreadCount) {
		super(name, displayName);
		this.unreadCount = unreadCount;
	}

	public Folder getFolder() {
		return this;
	}

	public int getUnreadCount() {
		return unreadCount;
	}

}
