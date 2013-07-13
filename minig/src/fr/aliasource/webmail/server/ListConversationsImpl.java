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

import fr.aliasource.webmail.client.rpc.ListConversations;
import fr.aliasource.webmail.client.rpc.UseCachedData;
import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.ConversationList;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;
import fr.aliasource.webmail.server.proxy.client.ConversationReferenceList;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class ListConversationsImpl extends ConversationListAjaxCall implements
		ListConversations {

	private static final long serialVersionUID = 6047143700528875723L;

	public ConversationList list(Folder f, int page, int pageLength) {
		IAccount account = getAccount();

		String lastSeenVersion = getLastSeenVersion();
		long version = 0;
		if (lastSeenVersion != null
				&& lastSeenVersion.contains(f.getName().toLowerCase())) {
			int idx = lastSeenVersion.lastIndexOf('/');
			version = Long.parseLong(lastSeenVersion.substring(idx + 1));
		}

		ConversationList ret = null;
		try {
			if (account != null) {
				ConversationReferenceList conversations = account
						.listConversations(version, new Folder(f.getName(), f
								.getDisplayName()), page, pageLength);
				setLastSeenVersion(f, page, conversations.getVersion());
				int fullLen = conversations.getFullLength();
				List<ConversationReference> cl = conversations.getPage();
				int rc = cl.size();
				Conversation[] shortList = createShortlist(cl, rc);
				ret = new ConversationList(fullLen, shortList);
			}
		} catch (UseCachedData ucd) {
			logger.warn("use cached data !!!");
			throw ucd;
		} catch (Exception e) {
			logger.error("Error listing conversations (" + f.getName() + ", "
					+ page + ", " + pageLength + ")", e);
			ret = new ConversationList();
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
		}
		return ret;

	}

}
