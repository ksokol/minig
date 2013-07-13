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

package fr.aliasource.webmail.client.shared;

import java.io.Serializable;

/**
 * Email address representation.
 * 
 * @author tom
 * 
 */
public class EmailAddress implements Serializable {

	private static final long serialVersionUID = -5631275178790643970L;

	private String email;
	private String displayName;
	private String signature;

	public EmailAddress() {
		this("defaultDisplayName", "default@default.com", "defaultSignature");
	}

	public EmailAddress(String displayName, String email) {
		this.displayName = displayName;
		this.email = email;
		this.signature = "signature";
	}

	public EmailAddress(String email) {
		this.displayName = email;
		this.email = email;
		this.signature = "signature";
	}

	public EmailAddress(String displayName, String email, String signature) {
		this.displayName = displayName;
		this.email = email;
		this.signature = signature;
	}

	public String getDisplay() {
		return displayName;
	}

	public String getEmail() {
		return email;
	}

	public String getSignature() {
		return signature;
	}

	public boolean equals(Object obj) {
		return email.equalsIgnoreCase(((EmailAddress) obj).email);
	}

	public int hashCode() {
		return email.hashCode();
	}

}
