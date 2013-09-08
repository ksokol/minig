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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.XssUtils;
import fr.aliasource.webmail.client.conversations.ConversationListActionsPanel.Position;
import fr.aliasource.webmail.client.conversations.ConversationListPanel;
import fr.aliasource.webmail.client.conversations.DateFormatter;
import fr.aliasource.webmail.client.shared.IClientMessage;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;
import fr.aliasource.webmail.client.test.BeanFactory;

/**
 * Widget used to display all the messages in a conversation
 * 
 * @author tom
 * 
 */
public class ConversationDisplay extends DockPanel {

    private List<MessageWidget> messages;
    private ConversationActions southToolbar;
    private ConversationActions northToolbar;
    private IClientMessage conversationContent;
    private HTML title;
    private VerticalPanel vp;
    private View ui;

    public ConversationDisplay(View ui, ConversationListPanel listPanel, int page) {
        this.ui = ui;
        northToolbar = new ConversationActions(this, ui, listPanel, page, Position.North);
        add(northToolbar, DockPanel.NORTH);

        vp = new VerticalPanel();
        vp.setStyleName("conversationDisplay");

        title = new HTML(I18N.strings.loadingConversation() + "...");
        title.setStyleName("conversationTitle");
        title.setWidth("100%");
        vp.add(title);

        southToolbar = new ConversationActions(this, ui, listPanel, page, Position.South);
        add(southToolbar, DockPanel.SOUTH);
        setWidth("100%");

        vp.setWidth("100%");
        add(vp, DockPanel.CENTER);

    }

    public void setConversationContent(IClientMessage cc) {
        if (!cc.getRead()) {
            Ajax<IClientMessage> ajax = AjaxFactory.updateMessageFlags(cc.getId());
            IClientMessage m = BeanFactory.instance.clientMessage().as();
            m.setRead(true);

            try {
                ajax.send(m, new AjaxCallback<IClientMessage>() {

                    @Override
                    public void onSuccess(IClientMessage object) {
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {
                        if (exception != null) {
                            ui.notifyUser(exception.getMessage());
                        } else {
                            ui.notifyUser("something goes wrong.");
                        }
                    }
                });
            } catch (RequestException e) {
                ui.notifyUser(e.getMessage());
            }
        }

        if (conversationContent != null) {
            // TODO
            // conversationContent.destroy();
        }

        this.conversationContent = cc;
        vp.clear();
        vp.add(title);
        title.setHTML("<b>" + XssUtils.safeHtml(cc.getSubject()));

        // ClientMessage[] cm = cc; //cc.getMessages();
        messages = new ArrayList<MessageWidget>(2); // (cm.length + 1);
        DateFormatter df = new DateFormatter(new Date());
        RecipientsStyleHandler rsh = new RecipientsStyleHandler(cc);
        // for (int i = 0; i < cm.length; i++) {
        MessageWidget mw = null;
        // if (i == cm.length - 1) {
        // show last message expanded with quick reply enabled
        mw = new MessageWidget(ui, this, df, cc, true, rsh);
        mw.setOpen(true);
        mw.setLastMessage(true);
        // } else {
        // mw = new MessageWidget(ui, this, df, cm[i], false, rsh);
        // mw.setOpen(!cm[i].isRead());
        // }
        mw.setWidth("100%");
        messages.add(mw);
        vp.add(mw);
    }

    public void shutdown() {
        northToolbar.shutdown();
        southToolbar.shutdown();

        if (messages != null) {
            for (MessageWidget mw : messages) {
                mw.destroy();
            }

            messages.clear();
        }
        // TODO
        // conversationContent.destroy();
        conversationContent = null;
    }

    public void setExpanded(boolean b) {
        southToolbar.enable();
        northToolbar.enable();

        // for (MessageWidget mw : messages) {
        // mw.setOpen(b);
        // }
    }

    public IClientMessage getConversationContent() {
        return conversationContent;
    }
}
