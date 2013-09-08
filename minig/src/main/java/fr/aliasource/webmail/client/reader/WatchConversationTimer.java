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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.shared.ConversationContent;

public class WatchConversationTimer {

	private Timer t;
	private ConversationContent cc;
	private View ui;

	public WatchConversationTimer(View ui, ConversationContent cc) {
		this.cc = cc;
		this.ui = ui;
		t = new Timer() {
			public void run() {
				timedRefresh();
			}
		};
	}

	private void timedRefresh() {
		ui.getSpinner().startSpinning();
		ui.log("timed conversation refresh");
		AsyncCallback<ConversationContent> ac = new AsyncCallback<ConversationContent>() {
			public void onFailure(Throwable caught) {
				ui.log("refresh failure", caught);
				ui.getSpinner().stopSpinning();
			}

			public void onSuccess(ConversationContent serverConv) {
				ui.getSpinner().stopSpinning();
				ui.log("refresh success");
				if (serverConv.getMessages().length > cc.getMessages().length) {
					ui.log("conv reader need refresh with new message");
				} else {
					ui.log("no reader refresh needed");
				}
			}
		};
		AjaxCall.sca.show(cc.getConversation().getId(), ac);
	}

	public void start() {
		GWT.log("startWatchConvTimer", null);
		t.scheduleRepeating(20 * 1000);
	}

	public void stop() {
		GWT.log("stopWatchConvTimer", null);
		t.cancel();
	}

}
