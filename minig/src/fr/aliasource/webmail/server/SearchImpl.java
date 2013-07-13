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

import fr.aliasource.webmail.client.rpc.Search;
import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.ConversationList;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;
import fr.aliasource.webmail.server.proxy.client.ConversationReferenceList;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class SearchImpl extends ConversationListAjaxCall implements Search {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9191173373536804067L;

	public ConversationList search(String query, int page, int pageLength) {
		if (logger.isDebugEnabled()) {
			logger.debug("search(" + query + ", " + page + ", " + pageLength
					+ ")");
		}

		IAccount account = getAccount();

		// reset last_seen_version to force re-query
		getThreadLocalRequest().getSession().setAttribute("last_seen_version",
				"0");

		ConversationList ret = null;
		try {
			if (account != null) {
				ConversationReferenceList conversations = account.search(query,
						page, pageLength);
				int fullLen = conversations.getFullLength();
				List<ConversationReference> cl = conversations.getPage();
				int rc = cl.size();
				Conversation[] shortList = createShortlist(cl, rc);
				recalcNextPrevious(shortList);
				ret = new ConversationList(fullLen, shortList);
			}
		} catch (Exception e) {
			logger.error("search error (" + query + ", " + page + ", "
					+ pageLength + ")", e);
			ret = new ConversationList();
		}
		return ret;

	}

	private void recalcNextPrevious(Conversation[] shortList) {
		for (int i = 0; i < shortList.length; i++) {
			if (i > 0) {
				shortList[i].setPrev(shortList[i - 1].getId());
			} else {
				shortList[i].setPrev(null);
			}
			if (i < shortList.length - 1) {
				shortList[i].setNext(shortList[i + 1].getId());
			} else {
				shortList[i].setNext(null);
			}
		}
	}

	@Override
	public Conversation computePrevNext(Folder searchFolder, ConversationId currentId) {
		String search = searchFolder.getName().substring("search:".length());
		ConversationList ret = search(search, 1, Integer.MAX_VALUE);
		Conversation[] convs = ret.getData();
		Conversation conv = null;
		for (Conversation c : convs) {
			if (currentId.equals(c.getId())) {
				conv = c;
				break;
			}
		}
		return conv;
	}

}
