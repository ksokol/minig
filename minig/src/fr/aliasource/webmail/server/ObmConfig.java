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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Loads frontend config
 * 
 * @author tom
 * 
 */
public class ObmConfig {

	public static final String OBM_CONFIG = "/etc/obm/obm_conf.ini";
	private Log logger = LogFactory.getLog(getClass());

	private Map<String, String> values;
	private String externalUrl;

	public ObmConfig() {
		values = new HashMap<String, String>();
		File f = new File(OBM_CONFIG);
		if (f.exists()) {
			loadIniFile(f);
		} else {
			logger.error(OBM_CONFIG + " does not exist.");
		}

		StringBuilder extUrl = new StringBuilder();
		extUrl.append(values.get("external-protocol"));
		extUrl.append("://");
		extUrl.append(values.get("external-url"));
		extUrl.append(values.get("obm-prefix"));

		this.externalUrl = extUrl.toString();
	}

	private void loadIniFile(File f) {
		FileInputStream in = null;
		try {
			Properties p = new Properties();
			in = new FileInputStream(f);
			p.load(in);
			for (Object key : p.keySet()) {
				values.put((String) key, p.getProperty((String) key));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public Map<String, String> get() {
		return values;
	}

	public String getExternalUrl() {
		return externalUrl;
	}

}
