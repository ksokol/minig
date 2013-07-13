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

package fr.aliasource.webmail.server.proxy.client;

import java.util.List;

public class ConversationReferenceList {

	private List<ConversationReference> crl;
	private int fullLength;
	private long version;

	public ConversationReferenceList(List<ConversationReference> crl,
			int fullLength) {
		this.fullLength = fullLength;
		this.crl = crl;
		this.version = 0;
	}

	public int getFullLength() {
		return fullLength;
	}

	public List<ConversationReference> getPage() {
		return crl;
	}

	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

}
