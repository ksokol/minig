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

package fr.aliasource.webmail.client;

import java.util.Comparator;

import fr.aliasource.webmail.client.shared.Folder;

public class FolderComparator implements Comparator<Folder> {

	public int compare(Folder o1, Folder o2) {
		if ("inbox".equalsIgnoreCase(o1.getName())) {
			return -1;
		}
		if ("inbox".equalsIgnoreCase(o2.getName())) {
			return +1;
		}

		return o1.getName().compareToIgnoreCase(o2.getName());
	}

}
