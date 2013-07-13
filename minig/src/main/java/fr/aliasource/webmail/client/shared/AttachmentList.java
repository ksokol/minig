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
import java.util.List;

public class AttachmentList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5826375721041293207L;
	private int fullLength;
	private AttachmentMetadata[] data;

	public AttachmentList() {
		this(0, new AttachmentMetadata[0]);
	}

	public AttachmentList(int fullLength, AttachmentMetadata[] data) {
		super();
		this.fullLength = fullLength;
		this.data = data;
	}

	public AttachmentList(List<AttachmentMetadata> list, int size) {
		this.fullLength = size;
		this.data = (AttachmentMetadata[]) list
				.toArray(new AttachmentMetadata[list.size()]);
	}

	public int getFullLength() {
		return fullLength;
	}

	public AttachmentMetadata[] getData() {
		return data;
	}

}
