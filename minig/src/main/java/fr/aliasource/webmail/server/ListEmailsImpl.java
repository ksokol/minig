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

import java.util.ArrayList;
import java.util.List;

import fr.aliasource.webmail.client.rpc.ListEmails;
import fr.aliasource.webmail.client.shared.MailSuggestion;
import fr.aliasource.webmail.server.proxy.client.Completion;
import fr.aliasource.webmail.server.proxy.client.IAccount;

/**
 * Fetches emails autocompletions in composer
 * 
 * @author tom
 * 
 */
public class ListEmailsImpl extends SecureAjaxCall implements ListEmails {

	private static final long serialVersionUID = -3372157931171427808L;

	public MailSuggestion[] listEmails(String query) {

		ArrayList<MailSuggestion> ret = new ArrayList<MailSuggestion>();
		int idx = query.lastIndexOf(",");
		String valueStart = "";
		String shortQuery = query;
		if (idx > 0 && query.length() > idx + 1) {
			shortQuery = query.substring(idx + 1).trim();
			valueStart = query.substring(0, idx + 1) + " ";
			if (shortQuery.length() == 0) {
				shortQuery = query;
			}
		}
		if (logger.isInfoEnabled()) {
			logInfo("[" + shortQuery + "] ");
		}

		IAccount account = getAccount();

		List<Completion> completions = account.getPossibleCompletions("emails",
				shortQuery, 10);
		for (Completion c : completions) {
			ret.add(new MailSuggestion(valueStart + c.getValue(), c
					.getDisplayName()
					+ " <" + c.getValue() + ">"));
		}

		MailSuggestion[] ms = new MailSuggestion[ret.size()];

		if (logger.isInfoEnabled()) {
			logInfo("returning " + ms.length + " mail suggestions.");
		}
		return ret.toArray(ms);
	}

}
