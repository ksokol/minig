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

package fr.aliasource.webmail.server.proxy.client.http.folder;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.proxy.client.http.AbstractClientMethod;
import fr.aliasource.webmail.server.proxy.client.http.DOMUtils;

public class AbstractFolderMethod extends AbstractClientMethod {

	protected AbstractFolderMethod(HttpClient hc, String backendUrl,
			String action) {
		super(hc, backendUrl, action);
	}

	protected Document getFolderAsXML(Folder f)
			throws ParserConfigurationException, FactoryConfigurationError {
		Document doc = DOMUtils.createDoc(
				"http://obm.aliasource.fr/xsd/folder_list", "folderList");
		Element root = doc.getDocumentElement();
		Element fe = DOMUtils.createElement(root, "folder");
		fe.setAttribute("displayName", f.getDisplayName());
		fe.setAttribute("name", f.getName());
		fe.setAttribute("subscribed", String.valueOf(f.isSubscribed()));
		fe.setAttribute("shared", String.valueOf(f.isShared()));

		return doc;
	}
}
