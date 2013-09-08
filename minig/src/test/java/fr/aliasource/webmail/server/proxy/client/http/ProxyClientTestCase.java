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

package fr.aliasource.webmail.server.proxy.client.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import junit.framework.TestCase;
import fr.aliasource.webmail.client.shared.Body;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.server.FrontEndConfig;
import fr.aliasource.webmail.server.proxy.client.IAccount;
import fr.aliasource.webmail.server.proxy.client.ProxyConfig;

public abstract class ProxyClientTestCase extends TestCase {

	public static final String MY_MAIL = "thomas@zz.com";

	protected Random random = new Random(System.currentTimeMillis());

	protected IAccount ac;

	protected void setUp() throws Exception {
		super.setUp();
		ProxyClientFactory pcf = new ProxyClientFactory();
		FrontEndConfig fec = new FrontEndConfig();
		ProxyConfig cfg = new ProxyConfig(Collections
				.synchronizedMap(fec.get()));
		ac = pcf.newProxyClient(cfg);
		ac.login(confValue("login"), confValue("domain"), confValue("password"));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		if (ac != null) {
			ac.logout();
		}
	}

	private String confValue(String key) {
		InputStream is = getClass().getClassLoader().getResourceAsStream(
				"test.properties");
		Properties props = new Properties();
		if (is != null) {
			try {
				props.load(is);
				return props.getProperty(key);
			} catch (IOException e) {
				return null;
			} finally {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		} else {
			return null;
		}
	}
	
	protected ClientMessage getDummyMessage(String[] to) {
		Body b = new Body();
		b.setPlain("rand body " + random.nextInt(99));
		ClientMessage m = new ClientMessage(null, null, "rand subject "
				+ random.nextInt(99), b, new String[0], new Date(),
				"MiniG Webmail", null);
		List<EmailAddress> recip = new ArrayList<EmailAddress>();
		for (int i = 0; i < to.length; i++) {
			recip.add(new EmailAddress("Random recip" + random.nextInt(99), to[i]));
		}
		m.setTo(recip);
		m.setSender(new EmailAddress("Myself", MY_MAIL));
		return m;
	}

}
