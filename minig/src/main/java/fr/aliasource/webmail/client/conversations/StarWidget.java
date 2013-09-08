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

package fr.aliasource.webmail.client.conversations;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

import fr.aliasource.webmail.client.IDestroyable;
import fr.aliasource.webmail.client.ctrl.MinigEventBus;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.IClientMessage;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;
import fr.aliasource.webmail.client.test.BeanFactory;

public class StarWidget extends Image implements IDestroyable, StarredChangedEventHandler {

	private boolean starred;
	private String id;
	private HandlerRegistration reg;

	public interface Stars extends ClientBundle {
		@Source("starred.gif")
		ImageResource starred();

		@Source("unstarred.gif")
		ImageResource unstarred();
	}

	public static Stars stars = GWT.create(Stars.class);

	public StarWidget(boolean starred, String id) {
		super();
		this.id = id;
		setStarred(starred);
		// if (id.hasFolder()) {
		reg = addClickHandler(getStarListener());
		// }

		MinigEventBus.getEventBus().addHandler(StarredChangedEvent.TYPE, this);
	}

	private ClickHandler getStarListener() {
		ClickHandler cl = new ClickHandler() {
			public void onClick(ClickEvent event) {
				setStarred(!starred);
				setFlagOnServer(starred);
			}
		};
		return cl;
	}

	private void setFlagOnServer(final boolean starred) {
		IClientMessage m = BeanFactory.instance.clientMessage().as();
		m.setStarred(starred);

		Ajax<IClientMessage> ajax = AjaxFactory.updateMessageFlags(id);

		try {
			ajax.send(m, new AjaxCallback<IClientMessage>() {

				@Override
				public void onSuccess(IClientMessage object) {
					setStarred(starred);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					if (exception != null) {
						WebmailController.get().getView().notifyUser(exception.getMessage());
					} else {
						WebmailController.get().getView().notifyUser("something goes wrong.");
					}
				}
			});
		} catch (RequestException e) {
			WebmailController.get().getView().notifyUser(e.getMessage());
		}
	}

	private void setStarred(boolean starred) {
		this.starred = starred;
		if (starred) {
			setUrl(stars.starred().getURL());
		} else {
			setUrl(stars.unstarred().getURL());
		}
	}

	@Override
	public void destroy() {
		if (reg != null) {
			reg.removeHandler();
		}
	}

	@Override
	public void onMessageReceived(StarredChangedEvent event) {
		if (event.getIds().contains(id)) {
			setStarred(event.isStarred());
		}
	}

}
