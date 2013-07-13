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

package fr.aliasource.webmail.server.export;

import java.io.FileOutputStream;
import java.net.URL;
import java.util.Collections;

import junit.framework.TestCase;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.server.FrontEndConfig;
import fr.aliasource.webmail.server.proxy.client.ConversationReference;
import fr.aliasource.webmail.server.proxy.client.IAccount;
import fr.aliasource.webmail.server.proxy.client.ProxyConfig;
import fr.aliasource.webmail.server.proxy.client.http.ProxyClientFactory;

public class ConversationExporterTests extends TestCase {

	private ConversationExporter exporter;
	private ConversationReference cr;
	private ClientMessage[] cm;
	private IAccount account;

	protected void setUp() throws Exception {
		super.setUp();
		ProxyClientFactory pcf = new ProxyClientFactory();
		FrontEndConfig fec = new FrontEndConfig();
		ProxyConfig cfg = new ProxyConfig(
				Collections.synchronizedMap(fec.get()));
		account = pcf.newProxyClient(cfg);
		ConversationId convId = new ConversationId("INBOX/1257758465041-955");
		account.login("david", "coucou.com", "david");
		cr = account.findConversation(convId);
		String folder = convId.getSourceFolder();
		Folder f = new Folder(folder, folder);

		cm = null;
		cm = account.fetchMessages(f, cr.getMessageIds());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testExportToPdf() throws Exception {
		exporter = new ConversationExporter(getLogoUrl().getPath());
		exporter.exportToPdf(account, cr, cm, new FileOutputStream(
				"/tmp/test.pdf"));
	}

	public void testExportToHtml() throws Exception {
		exporter = new ConversationExporter(getLogoUrl().getPath());
		exporter.exportToHtml(account, cr, cm, new FileOutputStream(
				"/tmp/test.html"));
	}

	private URL getLogoUrl() {
		return Thread.currentThread().getContextClassLoader()
				.getResource("logo_print.jpg");
	}

}