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

package fr.aliasource.webmail.client.reader;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.shared.ClientMessage;

public class MessageActions extends DockPanel {

	private Anchor showImagesLink;
	private VerticalPanel vp;
	private VerticalPanel cleanMessageBody;
	private VerticalPanel actionPanel;
	private HTML body;
	private ClientMessage cm;
	private HandlerRegistration reg;

	public MessageActions(final ClientMessage cm) {
		this.cm = cm;

		setWidth("100%");
		this.cleanMessageBody = createCleanMessageBody();
		cleanMessageBody.setWidth("100%");

		VerticalPanel vsp = new VerticalPanel();
		vsp.setWidth("100%");
		vsp.add(cleanMessageBody);
		add(vsp, DockPanel.NORTH);

		body = new HTML(cm.getBody().getCleanHtml());
		body.addStyleName("messageText");
		vsp.add(body);
	}

	private VerticalPanel createCleanMessageBody() {

		vp = new VerticalPanel();
		actionPanel = new VerticalPanel();

		Label imagesNotDisplayed = new Label(I18N.strings
				.imagesAreNotDisplayed());
		imagesNotDisplayed.addStyleName("bold");

		showImagesLink = new Anchor(I18N.strings.displayImagesBelow());

		reg = showImagesLink.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent ce) {
				showImages();
			}
		});

		actionPanel.add(imagesNotDisplayed);
		actionPanel.add(showImagesLink);
		actionPanel.setWidth("100%");
		actionPanel.addStyleName("contentInfo");

		vp.add(actionPanel);

		return vp;
	}

	private void showImages() {
		HTML partialCleanBody = new HTML(cm.getBody().getPartialCleanHtml());
		partialCleanBody.setWidth("100%");
		partialCleanBody.addStyleName("messageText");
		body.removeFromParent();
		add(partialCleanBody, DockPanel.CENTER);
		cleanMessageBody.removeFromParent();
		setHeight("100%");
	}

	public void destroy() {
		if (reg != null) {
			reg.removeHandler();
		}
		cm = null;
	}

}
