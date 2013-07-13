package fr.aliasource.webmail.client.ctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

import fr.aliasource.webmail.client.FolderSelector;
import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.chat.XmppController;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.settings.IServerSettingsListener;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.EmailAddress;

public class WebmailController {

	private static final WebmailController ctrl;

	static {
		ctrl = new WebmailController();
	}

	public static WebmailController get() {
		return ctrl;
	}

	private View view;
	private List<IServerSettingsListener> ssListeners;
	private boolean serverSettingsKnown;
	private HashMap<String, String> settings;
	private Timer heartbeat;
	private FolderSelector selector;
	private HashMap<String, String> systemFolders;
	private PushTimer pushTimer;

	private WebmailController() {
		this.ssListeners = new LinkedList<IServerSettingsListener>();
		this.selector = new FolderSelector();
		GWT.log("controller created", null);
	}

	public void start(RootPanel rp, HashMap<String, String> s) {
		GWT.log("starting minig", null);

		if (!GWT.isScript()) {
			for (String k : s.keySet()) {
				GWT.log("setting: " + k + " => '" + s.get(k) + "'", null);
			}
		}
		this.settings = s;

		enableFeatures();

		if (Features.XMPP) {
			XmppController.init(settings);
		}

		systemFolders = new HashMap<String, String>();

		try {
			initView(rp, settings);
		} catch (Throwable t) {
			GWT.log("initView failed", t);
		}
		createSystemFoldersMap();

		GWT.log("after init view", null);
		initTimers();
	}

	private void enableFeatures() {
		int major = 2;
		int minor = 2;
		int release = 0;
		try {
			major = Integer.parseInt(settings.get("obm/os-major"));
			minor = Integer.parseInt(settings.get("obm/os-minor"));
			release = Integer.parseInt(settings.get("obm/os-release"));
		} catch (Throwable t) {
			GWT.log("error parsing obm-sync version", t);
		}
		int vernum = major * 1000 + minor * 100 + release;

		if (vernum >= 2302) {
			Features.FILTERS = true;
		}

		if (vernum >= 2500) {
			Features.OBM_SECTIONS = true;
		}

		if (settings.containsKey(GetSettings.XMPP_BIND_URL)
				&& settings.containsKey(GetSettings.XMPP_PASSWORD)) {
			Features.XMPP = true;
		}
	}

	private void createSystemFoldersMap() {
		systemFolders.put("inbox", I18N.strings.inbox());
		systemFolders.put(settings.get(GetSettings.SENT_FOLDER).toLowerCase()
				.replace("%d", "/"), I18N.strings.sent());
		systemFolders.put(settings.get(GetSettings.DRAFTS_FOLDER).toLowerCase()
				.replace("%d", "/"), I18N.strings.drafts());
		systemFolders.put(settings.get(GetSettings.TEMPLATES_FOLDER)
				.toLowerCase().replace("%d", "/"), I18N.strings.templates());
		systemFolders.put(settings.get(GetSettings.TRASH_FOLDER).toLowerCase()
				.replace("%d", "/"), I18N.strings.trash());
		systemFolders.put(settings.get(GetSettings.SPAM_FOLDER).toLowerCase()
				.replace("%d", "/"), I18N.strings.spam());
	}

	public boolean isSystemFolder(String name) {
		return systemFolders.containsKey(name.toLowerCase());
	}

	private void initTimers() {
		heartbeat = new HeartbeatTimer();
		heartbeat.scheduleRepeating(1000 * 60);
		if (Features.PUSH) {
			pushTimer = new PushTimer();
			pushTimer.schedule(1);
		}
	}

	private void initView(RootPanel rp, HashMap<String, String> settings) {
		this.settings = settings;
		this.view = new View(settings, false);
		selector.setView(view);
		selector.addListener(view);
		rp.clear();
		rp.add(view);

		// ensures we have server settings
		notifyServerSettingsListeners();
		view.startTimers();
	}

	private void notifyServerSettingsListeners() {
		for (IServerSettingsListener ssl : ssListeners) {
			ssl.settingsReceived();
		}
		serverSettingsKnown = true;
	}

	public void updateServerSettings(Map<String, String> serverSettings) {
		this.settings.putAll(serverSettings);
		notifyServerSettingsListeners();
	}

	public void addServerSettingsListener(IServerSettingsListener ssl) {
		GWT.log("server settings listeners: " + ssListeners.size(), null);
		ssListeners.add(ssl);
		if (serverSettingsKnown) {
			ssl.settingsReceived();
		}
	}

	public void removeServerSettingsListener(IServerSettingsListener ssl) {
		ssListeners.remove(ssl);
	}

	public EmailAddress getIdentity() {
		return new EmailAddress(settings.get("identities/fullname"), settings
				.get("identities/email"), settings.get("identities/signature"));
	}

	public List<EmailAddress> getIdentities() {
		int nb_identities = Integer.parseInt(settings
				.get("identities/nb_identities"));
		List<EmailAddress> ads = new ArrayList<EmailAddress>();
		ads.add(getIdentity()); // add default identity
		if (nb_identities > 0) {
			for (int i = 1; i < nb_identities; i++) {
				ads.add(new EmailAddress(settings.get("identities/fullname_" + i),
						settings.get("identities/email_" + i), settings
								.get("identities/signature_" + i)));
			}
		}
		return ads;
	}

	public boolean hasIdentities() {
		String count = settings.get("identities/nb_identities");
		int nb_identities = 1;
		if (count != null && count.length() > 0) {
			nb_identities = Integer.parseInt(count);
		}
		return (nb_identities > 1);
	}

	public String getSetting(String setting) {
		return (String) settings.get(setting);
	}

	/**
	 * Gives access to frontend & backend settings
	 * 
	 * @return
	 */
	public Map<String, String> getSettings() {
		return settings;
	}

	public FolderSelector getSelector() {
		return selector;
	}

	/**
	 * This one is called when MiniG has detected that the session on the
	 * frontend has expired. It triggers a visible warning in the UI.
	 */
	public void tartiflette() {
		view.tartiflette();
	}

	public String displayName(Folder f) {
		return displayName(f.getName());
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
