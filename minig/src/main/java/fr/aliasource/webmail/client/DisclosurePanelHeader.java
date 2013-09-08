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

import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class DisclosurePanelHeader extends Widget implements HasText,
		OpenHandler<DisclosurePanel>, CloseHandler<DisclosurePanel> {

	/**
	 * imageTD holds the image for the icon, not null. labelTD holds the text
	 * for the label.
	 */
	private final Element labelTD;
	private Element imageTD;
	private final Image open;
	private final Image close;
	private Image iconImage;
	private DisclosurePanel disclosurePanel;

	public DisclosurePanelHeader(String text, DisclosurePanel dp) {

		this.disclosurePanel = dp;
		this.open = new Image("minig/images/open.png");
		this.close = new Image("minig/images/close.png");
		iconImage = this.disclosurePanel.isOpen() ? open : close;

		Element root = DOM.createTable();
		root.setAttribute("cellspacing", "0");
		root.setAttribute("cellpadding", "0");
		Element tbody = DOM.createTBody();
		Element tr = DOM.createTR();
		imageTD = DOM.createTD();
		labelTD = DOM.createTD();

		setElement(root);

		DOM.appendChild(root, tbody);
		DOM.appendChild(tbody, tr);
		DOM.appendChild(tr, imageTD);
		DOM.appendChild(tr, labelTD);

		// set image TD to be same width as image.
		DOM.setElementProperty(imageTD, "align", "center");
		DOM.setElementProperty(imageTD, "valign", "middle");
		DOM.setStyleAttribute(imageTD, "width", iconImage.getWidth() + "px");

		DOM.appendChild(imageTD, iconImage.getElement());

		setText(text);

		dp.addOpenHandler(this);
		dp.addCloseHandler(this);
		setStyle();
	}

	public final String getText() {
		return DOM.getInnerText(labelTD);
	}

	@Override
	public final void onClose(CloseEvent<DisclosurePanel> event) {
		setStyle();
	}

	@Override
	public final void onOpen(OpenEvent<DisclosurePanel> event) {
		setStyle();
	}

	public final void setText(String text) {
		DOM.setInnerText(labelTD, text);
	}

	private void setStyle() {
		if (disclosurePanel.isOpen()) {
			imageTD.removeChild(iconImage.getElement());
			iconImage = open;
			imageTD.appendChild(iconImage.getElement());
		} else {
			imageTD.removeChild(iconImage.getElement());
			iconImage = close;
			imageTD.appendChild(iconImage.getElement());
		}
	}
}
