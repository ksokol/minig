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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.IDestroyable;
import fr.aliasource.webmail.client.TailCall;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.reader.AnsweredWidget;
import fr.aliasource.webmail.client.reader.ForwardedAnsweredWidget;
import fr.aliasource.webmail.client.reader.ForwardedWidget;
import fr.aliasource.webmail.client.shared.IClientMessage;
import fr.aliasource.webmail.client.shared.IClientMessageList;
import fr.aliasource.webmail.client.shared.IDeleteMessagesRequest;
import fr.aliasource.webmail.client.shared.IFolder;
import fr.aliasource.webmail.client.shared.IMessageId;
import fr.aliasource.webmail.client.shared.Id;
import fr.aliasource.webmail.client.shared.MessageCopyOrMoveRequest;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxCallback;
import fr.aliasource.webmail.client.test.AjaxFactory;
import fr.aliasource.webmail.client.test.BeanFactory;

/**
 * The grid widgets with the list of conversations
 * 
 * @author tom
 * 
 */
public class DataGrid extends Grid {

    private ConversationListPanel clp;
    private List<IClientMessage> curConvData;
    private View ui;

    public int cListFullLength;

    private List<IConversationSelectionChangedListener> selectionListeners;

    private Set<String> selectedIds;
    private List<HandlerRegistration> regs;

    public DataGrid(ConversationListPanel clp, View ui) {
        super(1, 7);
        this.regs = new LinkedList<HandlerRegistration>();
        this.clp = clp;
        this.ui = ui;
        setStyleName("conversationTable");
        styleRow(0);

        selectionListeners = new ArrayList<IConversationSelectionChangedListener>();
        selectedIds = new HashSet<String>();
    }

    private void styleRow(int row) {
        CellFormatter cf = getCellFormatter();
        int i = 0;
        int j = 0;
        String highPriorityClass = "dummy";
        String unreadClass = "";
        String strikeThrough = "";

        if (curConvData != null) {
            IClientMessage conversation = curConvData.get(row);

            if (conversation.getHighPriority()) {
                highPriorityClass = " high-priority ";
            }

            if (!conversation.getRead()) {
                unreadClass = " bold ";
            }

            if (conversation.getDeleted()) {
                strikeThrough = " deleted ";
            }
        }

        cf.setStyleName(row, j++, "convCb");
        cf.setStyleName(row, j++, "convStar");
        cf.setStyleName(row, j++, "convStar");
        cf.setStyleName(row, j++, "convRecip");
        cf.setStyleName(row, j++, "conversationAndPreviewCol");
        cf.setStyleName(row, j++, "convAttach");
        cf.setStyleName(row, j++, "convDate");

        cf.addStyleName(row, i++, highPriorityClass + unreadClass);
        cf.addStyleName(row, i++, highPriorityClass + unreadClass);
        cf.addStyleName(row, i++, highPriorityClass + unreadClass);
        cf.addStyleName(row, i++, highPriorityClass + unreadClass + strikeThrough);
        cf.addStyleName(row, i++, highPriorityClass + unreadClass + strikeThrough);
        cf.addStyleName(row, i++, highPriorityClass + unreadClass);
        cf.addStyleName(row, i++, highPriorityClass + unreadClass + strikeThrough);
    }

    public void updateGrid(IClientMessageList cList) {
        DateFormatter dtf = new DateFormatter(new Date());
        curConvData = cList.getMailList();
        cListFullLength = cList.getFullLength();

        int rc = curConvData.size();
        destroyCurrentWidgets();
        if (rc == 0) {
            showEmptyList();
        } else {
            if (getRowCount() != rc) {
                resizeRows(rc);
            }
            for (int i = 0; i < rc; i++) {
                try {
                    fillRow(dtf, curConvData.get(i), i);
                    styleRow(i);
                } catch (Throwable t) {
                    GWT.log("error drawing row", t);
                }
            }
        }
    }

    private void destroyCurrentWidgets() {
        for (HandlerRegistration hr : regs) {
            hr.removeHandler();
        }
        regs.clear();

        for (int i = 0; i < getRowCount(); i++) {
            for (int j = 0; j < getColumnCount(); j++) {
                Widget w = getWidget(i, j);
                if (w != null) {
                    if (w instanceof IDestroyable) {
                        ((IDestroyable) w).destroy();
                    }
                    w.removeFromParent();
                }
            }
        }
    }

    private void showEmptyList() {
        clear();
        resizeRows(0);
        resizeRows(1);
        // clear does not handle column set with "setText"
        setHTML(0, 2, "&nbsp;");
        setWidget(0, 3, new Label(I18N.strings.noAvailableConversations(), false));
        clp.destroySelectAllPanel();
        clp.destroyEmptyFolderPanel();
    }

    private void fillRow(DateFormatter dtf, IClientMessage data, int i) {
        int col = 0;

        CheckBox selector = new CheckBox();
        if (selectedIds.contains(data.getId())) {
            selector.setValue(true);
        }
        regs.add(selector.addClickHandler(getCheckListener(data.getId(), selector)));
        setWidget(i, col++, selector);

        setWidget(i, col++, new StarWidget(data.getStarred(), data.getId()));

        if (data.getAnswered() && data.getForwarded()) {
            setWidget(i, col++, new ForwardedAnsweredWidget());
        } else if (data.getAnswered()) {
            setWidget(i, col++, new AnsweredWidget());
        } else if (data.getForwarded()) {
            setWidget(i, col++, new ForwardedWidget());
        } else {
            setWidget(i, col++, new HTML("&nbsp"));
        }

        ClickHandler cl = newShowConversationListener(data, i);

        ParticipantsWidget pw = new ParticipantsWidget(ui, data, cl);
        regs.add(pw.getRegistration());
        setWidget(i, col++, pw);

        Widget convWidget = null;
        try {
            convWidget = getConversationWidget(data, cl);
        } catch (Throwable t) {
            GWT.log("crash on email", t);
            HTML h = new HTML("[" + data.getSubject() + "]");
            regs.add(h.addClickHandler(cl));
            convWidget = h;
        }

        setWidget(i, col++, convWidget);

        if (!data.getAttachments().isEmpty()) {
            setWidget(i, col++, new Image("minig/images/paperclip.gif"));
        } else {
            setHTML(i, col++, "&nbsp;");
        }

        String formatSmall = null;

        if (data.getDate() != null) {
            formatSmall = dtf.formatSmall(data.getDate());
        }

        Label dlbl = new Label(formatSmall, false);

        if (data.getDate() != null) {
            DateFormatter df = new DateFormatter(data.getDate());
            dlbl.setTitle(df.formatDetails(data.getDate()));
        }
        setWidget(i, col++, dlbl);
    }

    private Widget getConversationWidget(IClientMessage conversation, ClickHandler cl) {
        ConversationWidget ret = new ConversationWidget(conversation);
        regs.add(ret.addClickHandler(cl));
        return ret;
    }

    private ClickHandler newShowConversationListener(final IClientMessage conv, final int row) {
        ClickHandler cl = new ClickHandler() {
            public void onClick(ClickEvent sender) {
                if (sender.getNativeEvent().getCtrlKey()) {
                    switchSelected(row);
                    return;
                }

                // TODO
                if (conv.getId().startsWith("INBOX/Drafts")) {
                    // if
                    // (conv.getSourceFolder().equals(WebmailController.get().getSetting(GetSettings.DRAFTS_FOLDER)))
                    // {

                    clp.showComposer(conv.getId());
                } else {
                    clp.showConversation(conv.getId());
                }
            }
        };
        return cl;
    }

    public ClickHandler getCheckListener(final String convId, final CheckBox cb) {
        ClickHandler cl = new ClickHandler() {
            public void onClick(ClickEvent sender) {
                if (cb.getValue()) {
                    selectedIds.add(convId);
                } else {
                    selectedIds.remove(convId);
                    clp.destroySelectAllPanel();
                }
                notifySelectionListeners();
            }
        };
        return cl;
    }

    public void selectAllConversations() {
        notifySelectAllListeners();
    }

    public void selectAll() {
        for (int i = 0; i < curConvData.size(); i++) {
            setSelected(i, true);
        }
        notifySelectionListeners();
    }

    public void selectNone() {
        for (int i = 0; i < curConvData.size(); i++) {
            setSelected(i, false);
        }
        notifySelectionListeners();
    }

    public void clear() {
        selectedIds.clear();
        super.clear();
        notifySelectionListeners();
    }

    public void addSelectionChangedListener(IConversationSelectionChangedListener cscl) {
        selectionListeners.add(cscl);
    }

    private void notifySelectionListeners() {
        for (IConversationSelectionChangedListener cscl : selectionListeners) {
            cscl.selectionChanged(selectedIds);
        }
    }

    private void notifySelectAllListeners() {
        for (IConversationSelectionChangedListener cscl : selectionListeners) {
            cscl.selectionChanged("ALL");
        }
    }

    private void setSelected(int row, boolean selected) {
        CheckBox cb = (CheckBox) getWidget(row, 0);
        cb.setValue(selected);
        IClientMessage conv = curConvData.get(row);
        if (selected) {
            selectedIds.add(conv.getId());
        } else {
            selectedIds.remove(conv.getId());
        }
    }

    private void switchSelected(int row) {
        CheckBox cb = (CheckBox) getWidget(row, 1);
        setSelected(row, !cb.getValue());
        notifySelectionListeners();
    }

    public void clearAllSelected() {
        Set<String> temp = new HashSet<String>();
        for (IClientMessage conv : curConvData) {
            temp.add(conv.getId());
        }
        selectedIds.retainAll(temp);
        notifySelectionListeners();
    }

    public void deleteConversation() {
        ui.getSpinner().startSpinning();

        Ajax<IDeleteMessagesRequest> ajax = AjaxFactory.deleteMessages();
        IDeleteMessagesRequest request = BeanFactory.instance.deleteMessageRequest().as();
        request.setMessageIdList(new ArrayList<String>(selectedIds));

        try {
            ajax.send(request, new AjaxCallback<IDeleteMessagesRequest>() {

                @Override
                public void onSuccess(IDeleteMessagesRequest object) {
                    ui.getSpinner().stopSpinning();
                    clp.selectNone();
                    clp.showPage(clp.getCurrentPage());
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    ui.getSpinner().stopSpinning();

                    if (exception != null) {
                        ui.notifyUser(exception.getMessage());
                    } else {
                        ui.notifyUser("Error deleting messages");
                    }
                }
            });
        } catch (RequestException e) {
            ui.notifyUser(e.getMessage());
        }
    }

    private AsyncCallback<Void> deleteForeverCallback(final List<IMessageId> convIds) {
        return new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
                ui.log("Failed to delete conversation.");
                ui.getSpinner().stopSpinning();
            }

            public void onSuccess(Void result) {
                String message = I18N.strings.conversationDeletedForever();
                if (!convIds.isEmpty()) {
                    message = convIds.size() + I18N.strings.conversationsDeletedForever(Integer.toString(convIds.size()));
                }
                ui.notifyUser(message);
                clp.selectNone();
                clp.showPage(clp.getCurrentPage());
                ui.getSpinner().stopSpinning();
            }
        };
    }

    private AsyncCallback<List<IMessageId>> moveToTrashCallback() {
        return new AsyncCallback<List<IMessageId>>() {
            public void onFailure(Throwable caught) {
                ui.getSpinner().stopSpinning();
                ui.log("Error trashing conversations", caught);
            }

            public void onSuccess(List<IMessageId> newIds) {
                clp.selectNone();
                clp.showPage(clp.getCurrentPage());
                // ui.notifyUser(new UndoMoveWidget(ui,
                // "WebmailController.get().getSelector().getCurrent()
                // .getName(), newIds), 20);
                ui.getSpinner().stopSpinning();
            }
        };
    }

    private AsyncCallback<Void> purgeFolderCallback(final IFolder f) {
        final String folder = f.getName();
        return new AsyncCallback<Void>() {
            public void onFailure(Throwable caught) {
                ui.getSpinner().stopSpinning();
                ui.log("Failed to purge " + folder);
            }

            public void onSuccess(Void result) {
                ui.getSpinner().stopSpinning();
                ui.notifyUser(I18N.strings.allMessagesDeleted(folder));
                clp.showPage(clp.getCurrentPage());
                clp.destroySelectAllPanel();
            }
        };
    }

    public void moveConversation(String current, String f, boolean move, TailCall onSuccess) {
        moveSomeConversations(current, f, selectedIds, move, onSuccess);
    }

    public void moveSomeConversations(String current, final String f, Set<String> ids, boolean move, final TailCall onSuccess) {
        ui.getSpinner().startSpinning();

        if (move) {
            // IClientMessageList messageList =
            // BeanFactory.instance.clientMessageList().as();
            MessageCopyOrMoveRequest request = BeanFactory.instance.messagesCopyOrMoveRequest().as();
            List<Id> clientMessageList = new ArrayList<Id>();

            for (String messageId : ids) {
                Id id = BeanFactory.instance.id().as();
                id.setId(messageId);
                clientMessageList.add(id);
            }

            request.setFolder(f);
            request.setMessageIdList(clientMessageList);

            Ajax<MessageCopyOrMoveRequest> updateMessages = AjaxFactory.moveMessages();

            try {
                updateMessages.send(request, new AjaxCallback<MessageCopyOrMoveRequest>() {
                    @Override
                    public void onSuccess(MessageCopyOrMoveRequest object) {
                        ui.getSpinner().stopSpinning();
                        clp.selectNone();
                        clp.showPage(clp.getCurrentPage());
                        ui.notifyUser(I18N.strings.moveMessage(WebmailController.get().displayName(f)));
                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {
                        ui.getSpinner().stopSpinning();
                        String cause = null;

                        if (exception != null) {
                            cause = " :" + exception.getMessage();
                        }

                        ui.notifyUser(I18N.strings.errorMovingConv() + cause);
                    }
                });
            } catch (RequestException e) {
                ui.getSpinner().stopSpinning();
                ui.notifyUser(I18N.strings.errorMovingConv());
            }
        } else {
            try {
                MessageCopyOrMoveRequest copyMessagesRequest = BeanFactory.instance.messagesCopyOrMoveRequest().as();
                Ajax<MessageCopyOrMoveRequest> copyMessages = AjaxFactory.copyMessages();

                copyMessagesRequest.setFolder(f);
                List<Id> l = new ArrayList<Id>();

                for (String messageId : ids) {
                    Id id = BeanFactory.instance.id().as();
                    id.setId(messageId);
                    l.add(id);
                }

                copyMessagesRequest.setMessageIdList(l);

                copyMessages.send(copyMessagesRequest, new AjaxCallback<MessageCopyOrMoveRequest>() {
                    @Override
                    public void onSuccess(MessageCopyOrMoveRequest object) {
                        ui.getSpinner().stopSpinning();
                        clp.selectNone();
                        ui.notifyUser(I18N.strings.copiedTo(WebmailController.get().displayName(f)));
                    }

                    @Override
                    public void onError(Request request, Throwable exception) {
                        ui.getSpinner().stopSpinning();
                        String cause = null;

                        if (exception != null) {
                            cause = " :" + exception.getMessage();
                        }

                        ui.notifyUser(I18N.strings.errorMovingConv() + cause);
                    }
                });
            } catch (RequestException e) {
                ui.getSpinner().stopSpinning();
                ui.notifyUser(I18N.strings.errorMovingConv());
            } finally {
            }
        }
    }

    public void selectSome(Set<String> sel) {
        HashSet<String> tmp = new HashSet<String>(2 * sel.size());
        tmp.addAll(sel);
        selectNone();
        selectedIds = tmp;
    }
}
