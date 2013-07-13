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
 * Object representation of a mail folder.
 * 
 * @author tom
 * 
 */
public class Folder implements Serializable {

	private static final long serialVersionUID = -7153146982382544686L;

	private String name;
	private String displayName;
	private boolean subscribed = false;
	private boolean editable = true;
	private boolean shared;

	public Folder() {
		this("defaultfolderctor", "defaultctor");
	}

	public Folder(String name, String displayName) {
		super();
		String fDisplayName = null;
		if (displayName.contains("%d")) {
			String[] splitName = displayName.split("%d");
			fDisplayName = splitName[splitName.length - 1];
		} else {
			fDisplayName = displayName;
		}
		String fName = null;
		if (name.contains("%d")) {
			fName = name.replace("%d", "/");
		} else {
			fName = name;
		}
		this.name = fName;
		this.displayName = fDisplayName;
	}

	public Folder(String name) {
		this(name, name);
	}

	public Folder(String name, String displayName, boolean subscribed, boolean shared) {
		super();
		this.name = name;
		this.displayName = displayName;
		this.subscribed = subscribed;
		this.shared = shared;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String toString() {
		return name;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public boolean equals(Object obj) {
		return name.equals(((Folder) obj).name);
	}

	public boolean isEditable() {
		return editable;
	}

	public boolean isSearch() {
		return name.startsWith("search:");
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public boolean isShared() {
		return shared;
	}
	
	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public boolean canCreateSubfolder() {
		return editable;
	}
	
	public boolean canChangeSubscription() {
		return editable;
	}
	
	public boolean canRename() {
		if (editable) {
			return !shared;
		} else {
			return false;
		}
	}
	
	public boolean canDelete() {
		if (editable) {
			return !shared;
		} else {
			return false;
		}
	}
}
