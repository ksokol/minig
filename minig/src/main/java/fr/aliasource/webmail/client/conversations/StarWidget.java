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

import java.util.HashSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

import fr.aliasource.webmail.client.IDestroyable;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.rpc.SetFlags;
import fr.aliasource.webmail.client.shared.ConversationId;

public class StarWidget extends Image implements IDestroyable {

	private boolean starred;
	private ConversationId id;
	private HandlerRegistration reg;

	public interface Stars extends ClientBundle {
		@Source("starred.gif")
		ImageResource starred();

		@Source("unstarred.gif")
		ImageResource unstarred();
	}

	public static Stars stars = GWT.create(Stars.class);

	public StarWidget(boolean starred, ConversationId id) {
		super();
		this.id = id;
		setStarred(starred);
		if (id.hasFolder()) {
			reg = addClickHandler(getStarListener());
		}
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
		AsyncCallback<Void> ac = new AsyncCallback<Void>() {
			public void onFailure(Throwable caught) {
				GWT.log("star failure", caught);
			}

			public void onSuccess(Void result) {
				GWT.log("starred mail: " + starred, null);
			}
		};
		if (id.hasFolder()) {
			HashSet<ConversationId> ids = new HashSet<ConversationId>();
			ids.add(id);
			AjaxCall.flags.setFlags(ids, SetFlags.STAR, starred, ac);
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

}
