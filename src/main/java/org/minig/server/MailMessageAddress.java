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

package org.minig.server;

import org.minig.server.service.impl.helper.mime.Mime4jAddress;

import static java.util.Objects.requireNonNull;

public class MailMessageAddress {

	private static final String UNDISCLOSED_ADDRESS = "undisclosed address";

	private String email;
	private String displayName;

	public MailMessageAddress() {
		this(UNDISCLOSED_ADDRESS, "");
	}

	public MailMessageAddress(String displayName, String email) {
		if (displayName == null || displayName.isEmpty()) {
			this.displayName = email;
		} else {
			this.displayName = displayName;
		}
		this.email = email;
	}

    public MailMessageAddress(Mime4jAddress mime4jAddress) {
        requireNonNull(mime4jAddress);
        this.email = mime4jAddress.getAddress();
        this.displayName = mime4jAddress.getPersonal();
    }

	public MailMessageAddress(String email) {
		this.displayName = email;
		this.email = email;
	}

	public String getDisplay() {
		return displayName;
	}

	public String getEmail() {
		return email;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MailMessageAddress other = (MailMessageAddress) obj;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}
}
