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

import java.util.List;

import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;

/**
 * Abstract class for actions returning a list of conversations
 * 
 * @author tom
 * 
 */
public abstract class ConversationListAjaxCall extends SecureAjaxCall {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8698341193078421927L;

	protected Conversation[] createShortlist(List<ConversationReference> cl,
			int rc) {
		Conversation[] shortList = new Conversation[rc];
		ReferenceConverter rConv = new ReferenceConverter();

		for (int i = 0; i < rc; i++) {
			ConversationReference cr = cl.get(i);
			shortList[i] = rConv.referenceToConversation(cr);
		}

		return shortList;
	}

	public String getLastSeenVersion() {
		String val = (String) getThreadLocalRequest().getSession()
				.getAttribute("last_seen_version");

		return val;
	}

	public void setLastSeenVersion(Folder f, int page, long version) {
		String val = f.getName().toLowerCase() + "/" + version;
		// logger.info("setting last seen to val: "+val);
		getThreadLocalRequest().getSession().setAttribute("last_seen_version",
				val);
	}
}
