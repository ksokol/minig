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

package fr.aliasource.webmail.client.reader.invitation;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class InvitationAgendaLine extends Widget {

	public InvitationAgendaLine(String date, String title) {
		Element div = DOM.createElement("ul");
		div.setAttribute("class", "agendaLine");
		Element spanDate = DOM.createElement("li");
		spanDate.setAttribute("class", "agendaDate");
		spanDate.setInnerText(date);
		div.appendChild(spanDate);

		Element spanTitre = DOM.createElement("li");
		spanTitre.setAttribute("class", "agendaTitre");
		spanTitre.setInnerText(title);
		div.appendChild(spanTitre);

		this.setElement(div);
	}
}
