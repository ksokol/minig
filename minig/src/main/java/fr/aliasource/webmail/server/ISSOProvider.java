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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Should allow pluggable authentification with various SSO subsystems.
 * 
 * @author tom
 * 
 */
public interface ISSOProvider {

	/**
	 * @param myUrl
	 *            the url used to access minig
	 * @param settings
	 *            /etc/minig/frontend_conf.ini variables
	 * @param req
	 *            used to read request parameters & headers
	 * @param resp
	 *            usable to send redirects
	 * @return true a redirect was done. false if none was needed
	 */
	boolean redirectToSSOServer(String myUrl, Map<String, String> settings,
			HttpServletRequest req, HttpServletResponse resp);

	/**
	 * @param req
	 *            the request to allow a decision based on headers & query
	 * @return true if the sso server was already involded in the
	 *         authentification process
	 */
	boolean wentToSSOServer(HttpServletRequest req);

	/**
	 * @param settings
	 * @param req
	 * @return the credentials that should be used to authentify on the minig
	 *         backend.
	 */
	Credentials obtainCredentials(Map<String, String> settings,
			HttpServletRequest req);

}
