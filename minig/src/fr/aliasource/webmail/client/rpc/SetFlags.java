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

package fr.aliasource.webmail.client.rpc;

import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import fr.aliasource.webmail.client.shared.ConversationId;

/**
 * RPC interface for setting flags like "READ" on a given set of conversation
 * id(s)
 * 
 * @author tom
 * 
 */
@RemoteServiceRelativePath("flags")
public interface SetFlags extends RemoteService {

	public static final String READ = "read";
	public static final String STAR = "star";

	/**
	 * Set or unset a flag on a given set of conversation id(s)
	 */
	void setFlags(Set<ConversationId> conversationIds, String flag, boolean set);

	/**
	 * Set or unset a flag on a the current folder's conversations
	 */
	void setFlags(String query, String flag, boolean set);

}
