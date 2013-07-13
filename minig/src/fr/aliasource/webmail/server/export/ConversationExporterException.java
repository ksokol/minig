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

package fr.aliasource.webmail.server.export;

/**
 * 
 * @author matthieu
 * 
 */
public class ConversationExporterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9088096323157021939L;

	public ConversationExporterException() {
		super();
	}

	public ConversationExporterException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConversationExporterException(String message) {
		super(message);
	}

	public ConversationExporterException(Throwable cause) {
		super(cause);
	}

}
