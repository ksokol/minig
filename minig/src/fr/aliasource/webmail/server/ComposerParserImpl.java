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

import java.util.Arrays;

import fr.aliasource.webmail.client.rpc.ComposerParser;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.proxy.client.IAccount;

public class ComposerParserImpl extends SecureAjaxCall implements
		ComposerParser {

	private static final long serialVersionUID = 7606487399761601216L;

	@Override
	public ClientMessage prepareForward(ClientMessage cm) {
		IAccount ac = getAccount();
		logger.warn("should forward " + cm.getSubject() + " date: "
				+ cm.getDate() + " [" + cm.getUid() + "]");
		ClientMessage ret = ac.prepareForward(new Folder(cm.getFolderName()),
				Arrays.asList(cm.getUid()));
		return ret;
	}

	@Override
	public String beautifyText(String txt) {
		return new MessageBeautifier().beautify(txt);
	}

}
