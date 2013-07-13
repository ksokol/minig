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
 * A group of contact (e.g. "Acme.com users", "Collected contacts"), which could
 * be implemented on the server side by ldap directories, jdbc, etc.
 * 
 * @author tom
 * 
 */
public class ContactGroup implements Serializable, Comparable<ContactGroup> {

	private static final long serialVersionUID = 7807625913360654896L;

	private String id;
	private String displayName;
	private int size;

	public ContactGroup() {

	}

	/**
	 * Creates a new contact group with given server side id & displayName.
	 * 
	 * @param id
	 * @param displayName
	 */
	public ContactGroup(String id, String displayName) {
		this();
		this.id = id;
		this.displayName = displayName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int compareTo(ContactGroup o) {
		if ("all".equals(id) && "all".equals(o.id)) {
			return 0;
		}
		if ("all".equals(id)) {
			return -1;
		}
		if ("all".equals(o.id)) {
			return +1;
		}
		return displayName.toLowerCase().compareTo(o.displayName.toLowerCase());
	}

}
