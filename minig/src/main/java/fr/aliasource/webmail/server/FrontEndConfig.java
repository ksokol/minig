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
public class FrontEndConfig {

	public static final String FRONTEND_CONFIG = "/etc/minig/frontend_conf.ini";

	private Map<String, String> values;
	private Log logger = LogFactory.getLog(getClass());


	public FrontEndConfig() {
		values = new HashMap<String, String>();
		File f = new File(FRONTEND_CONFIG);
		if (f.exists()) {
			loadIniFile(f);
		} else {
			logger.error(FRONTEND_CONFIG + " does not exist.");
		}
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

}
