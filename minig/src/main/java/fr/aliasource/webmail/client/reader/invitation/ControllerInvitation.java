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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.shared.InvitationInfo;
import fr.aliasource.webmail.client.shared.MessageId;

/**
 * Event hub for invitation related things
 * 
 * @author adrienp
 * 
 */
public class ControllerInvitation {

	private InvitationInfoDataProvider invitationInfoDP;
	private GoingEventDataRequest goingEventDR;
	private InvitationPanel invPanel;

	public ControllerInvitation(View ui, final InvitationPanel invPanel) {

		this.invPanel = invPanel;

		AjaxCall.token.getToken(new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
			}

			public void onSuccess(String result) {
				invitationInfoDP = new InvitationInfoDataProvider(result,
						ControllerInvitation.this);
				goingEventDR = new GoingEventDataRequest(result,
						ControllerInvitation.this);
				invPanel.init();
			}
		});
	}

	public boolean requestGoingEvent(String extId, String going) {
		if (goingEventDR != null) {
			goingEventDR.requestGoing(extId, going);
			return true;
		} else {
			GWT.log("requestGoingEvent not possible yet", null);
			return false;
		}
	}

	public void goingReceived(String going) {
		invPanel.updateGoingLink(going);
	}

	public boolean requestGetInvitationData(MessageId messageId, String folder) {
		if (invitationInfoDP != null) {
			invitationInfoDP.requestInvitation(messageId, folder);
			return true;
		} else {
			GWT.log("invitationInfoDP not possible yet", null);
			return false;
		}
	}

	public void invitationReceived(InvitationInfo invitationInfo) {
		invPanel.update(invitationInfo);
	}
}
