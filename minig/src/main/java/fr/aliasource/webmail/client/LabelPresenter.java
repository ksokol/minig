package fr.aliasource.webmail.client;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.IFolder;

public class LabelPresenter {

    private IFolder f;
    private Anchor link;
    private String defaultText;
    private int idx;
    private RowFormatter rowFormatter;
    private boolean activeFilter;
    private HandlerRegistration reg;

    public LabelPresenter(IFolder f, int idx, RowFormatter rowFormatter) {
        this.f = f;
        this.idx = idx;
        this.rowFormatter = rowFormatter;
        defaultText = f.getId();
        createLink();
    }

    private void createLink() {
        Anchor ret = new Anchor(defaultText);
        ret.addStyleName("noWrap");
        ret.setTitle(f.getId());
        this.link = ret;
    }

    public Anchor getLink() {
        return link;
    }

    private void setVisible(boolean visible) {
        rowFormatter.setVisible(idx, visible);
    }

    public void applyFilter(String text) {
        if (text == null || text.length() == 0) {
            this.activeFilter = false;
            // setUnreadOnly(unreadOnly);
        } else {
            this.activeFilter = true;
            String name = f.getId();
            if (WebmailController.get().isSystemFolder(name)) {
                name = WebmailController.get().displayName(f);
            }

            String[] parts = name.toLowerCase().split("/");
            String t = text.toLowerCase();
            for (String s : parts) {
                boolean match = s.startsWith(t);
                setVisible(match);
                if (match) {
                    break;
                }
            }
        }
    }

    public void registerClickHandler(ClickHandler createHandler) {
        reg = getLink().addClickHandler(createHandler);
    }

    public void destroy() {
        if (reg != null) {
            reg.removeHandler();
            reg = null;
        }
    }
}
