package fr.aliasource.webmail.client.ctrl;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

import fr.aliasource.webmail.client.FolderSelector;
import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.shared.IFolder;

public class WebmailController {

    private static final WebmailController ctrl = new WebmailController();

    public static WebmailController get() {
        return ctrl;
    }

    private View view;
    private FolderSelector selector;
    private HashMap<String, String> systemFolders = new HashMap<String, String>();

    private WebmailController() {
        this.selector = new FolderSelector();
        GWT.log("controller created", null);
    }

    public void start(RootPanel rp) {
        GWT.log("starting minig", null);

        try {
            initView(rp);
        } catch (Throwable t) {
            GWT.log("initView failed", t);
        }
        createSystemFoldersMap();

        GWT.log("after init view");
    }

    private void createSystemFoldersMap() {
        systemFolders.put("inbox", I18N.strings.inbox());

        // if (settings.containsKey(GetSettings.SENT_FOLDER)) {
        // systemFolders.put(settings.get(GetSettings.SENT_FOLDER).toLowerCase().replace("%d",
        // "/"),
        // I18N.strings.sent());
        // }
        //
        // if (settings.containsKey(GetSettings.DRAFTS_FOLDER)) {
        // systemFolders.put(settings.get(GetSettings.DRAFTS_FOLDER).toLowerCase().replace("%d",
        // "/"),
        // I18N.strings.drafts());
        // }
        // if (settings.containsKey(GetSettings.TEMPLATES_FOLDER)) {
        // systemFolders.put(settings.get(GetSettings.TEMPLATES_FOLDER).toLowerCase().replace("%d",
        // "/"),
        // I18N.strings.templates());
        // }
        // if (settings.containsKey(GetSettings.TRASH_FOLDER)) {
        // systemFolders.put(settings.get(GetSettings.TRASH_FOLDER).toLowerCase().replace("%d",
        // "/"),
        // I18N.strings.trash());
        // }
        // if (settings.containsKey(GetSettings.SPAM_FOLDER)) {
        // systemFolders.put(settings.get(GetSettings.SPAM_FOLDER).toLowerCase().replace("%d",
        // "/"),
        // I18N.strings.spam());
        // }
    }

    public boolean isSystemFolder(String name) {
        return systemFolders.containsKey(name.toLowerCase());
    }

    private void initView(RootPanel rp) {
        // this.settings = settings;
        this.view = new View();
        selector.addListener(view);
        rp.clear();
        rp.add(view);
        view.startTimers();
    }

    // TODO remove
    public FolderSelector getSelector() {
        return selector;
    }

    public String displayName(IFolder f) {
        return displayName(f.getId());
    }

    public String displayName(String fName) {
        String sourceFolder = fName.toLowerCase();
        if (systemFolders.containsKey(sourceFolder)) {
            return systemFolders.get(sourceFolder);
        }

        int idx = sourceFolder.lastIndexOf("/");
        if (idx > 0) {
            return sourceFolder.substring(idx + 1);
        } else {
            return sourceFolder;
        }
    }

    public View getView() {
        return view;
    }

}
