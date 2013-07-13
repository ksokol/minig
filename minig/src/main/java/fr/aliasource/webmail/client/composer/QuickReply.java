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

package fr.aliasource.webmail.client.composer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ReplyInfo;

public class QuickReply extends MailComposer {

	private IQuickReplyListener listener;

	private ReplyManager replyManager;

	private ReplyInfo replyInfo;

	public QuickReply(View ui, IQuickReplyListener showQuickReplyListener) {
		super(ui);
		this.listener = showQuickReplyListener;
		this.replyManager = new ReplyManager(ui);
		addStyleName("replyZone");
		focusComposer();
	}

	@Override
	protected ClickHandler undoDiscardListener(final ClientMessage cm,
			final Widget notification, final boolean switchTab) {
		listener.setNotification(notification);
		return new ClickHandler() {
			public void onClick(ClickEvent ev) {
				listener.onClick(ev);
				ui.clearNotification(notification);
				loadDraft(cm, null);
			}
		};
	}

	@Override
	public void discard() {
		discard(false);
		listener.discard();
	}

	public void sendMessage() {
		sendMessage(listener);
	}

	private void reply(ClientMessage message, boolean toAll) {
		ClientMessage cm = replyManager.prepareReply(message, toAll);
		replyInfo = replyManager.getInfo(message);
		loadDraft(cm, null);
		focusComposer();
	}

	public void reply(ClientMessage message) {
		reply(message, false);
	}

	public void replyAll(ClientMessage message) {
		reply(message, true);
	}

	public void forward(ClientMessage message) {
		ui.log("forward");
		replyManager.prepareForward(message, this);
	}

	protected ReplyInfo getReplyInfo() {
		return replyInfo;
	}

	protected void addTabPanelListener() {
		// no tab panel listener as we are displayed in the conversation panel
	}

	@Override
	protected void addWindowResizeHandler() {
		// no need to resize when browser size changes
	}

	@Override
	protected ClientMessage clearComposer() {
		ClientMessage ret = super.clearComposer();
		destroy();
		return ret;
	}

}
