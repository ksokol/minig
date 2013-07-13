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

package fr.aliasource.webmail.client.ctrl;

import com.google.gwt.core.client.GWT;

import fr.aliasource.webmail.client.rpc.AttachementsManager;
import fr.aliasource.webmail.client.rpc.AttachementsManagerAsync;
import fr.aliasource.webmail.client.rpc.ChatService;
import fr.aliasource.webmail.client.rpc.ChatServiceAsync;
import fr.aliasource.webmail.client.rpc.ComposerParser;
import fr.aliasource.webmail.client.rpc.ComposerParserAsync;
import fr.aliasource.webmail.client.rpc.DispositionNotification;
import fr.aliasource.webmail.client.rpc.DispositionNotificationAsync;
import fr.aliasource.webmail.client.rpc.FilterManager;
import fr.aliasource.webmail.client.rpc.FilterManagerAsync;
import fr.aliasource.webmail.client.rpc.FolderManager;
import fr.aliasource.webmail.client.rpc.FolderManagerAsync;
import fr.aliasource.webmail.client.rpc.GetQuota;
import fr.aliasource.webmail.client.rpc.GetQuotaAsync;
import fr.aliasource.webmail.client.rpc.GetSettings;
import fr.aliasource.webmail.client.rpc.GetSettingsAsync;
import fr.aliasource.webmail.client.rpc.GetToken;
import fr.aliasource.webmail.client.rpc.GetTokenAsync;
import fr.aliasource.webmail.client.rpc.Heartbeat;
import fr.aliasource.webmail.client.rpc.HeartbeatAsync;
import fr.aliasource.webmail.client.rpc.ContactsManager;
import fr.aliasource.webmail.client.rpc.ContactsManagerAsync;
import fr.aliasource.webmail.client.rpc.ListConversations;
import fr.aliasource.webmail.client.rpc.ListConversationsAsync;
import fr.aliasource.webmail.client.rpc.ListEmails;
import fr.aliasource.webmail.client.rpc.ListEmailsAsync;
import fr.aliasource.webmail.client.rpc.Login;
import fr.aliasource.webmail.client.rpc.LoginAsync;
import fr.aliasource.webmail.client.rpc.Logout;
import fr.aliasource.webmail.client.rpc.LogoutAsync;
import fr.aliasource.webmail.client.rpc.PushChannel;
import fr.aliasource.webmail.client.rpc.PushChannelAsync;
import fr.aliasource.webmail.client.rpc.Search;
import fr.aliasource.webmail.client.rpc.SearchAsync;
import fr.aliasource.webmail.client.rpc.SendMessage;
import fr.aliasource.webmail.client.rpc.SendMessageAsync;
import fr.aliasource.webmail.client.rpc.SetFlags;
import fr.aliasource.webmail.client.rpc.SetFlagsAsync;
import fr.aliasource.webmail.client.rpc.SettingManager;
import fr.aliasource.webmail.client.rpc.SettingManagerAsync;
import fr.aliasource.webmail.client.rpc.ShowConversation;
import fr.aliasource.webmail.client.rpc.ShowConversationAsync;
import fr.aliasource.webmail.client.rpc.StoreMessage;
import fr.aliasource.webmail.client.rpc.StoreMessageAsync;

public class AjaxCall {

	public static final ListConversationsAsync listConversations = GWT
			.create(ListConversations.class);

	public static final LoginAsync login = GWT.create(Login.class);

	public static final ChatServiceAsync chatService = GWT
			.create(ChatService.class);

	public static final FolderManagerAsync folderManager = GWT
			.create(FolderManager.class);

	public static final SettingManagerAsync settingsManager = GWT
			.create(SettingManager.class);

	public static final ComposerParserAsync composerParser = GWT
			.create(ComposerParser.class);

	public static final ListEmailsAsync lemails = GWT.create(ListEmails.class);

	public static final SearchAsync search = GWT.create(Search.class);

	public static final ShowConversationAsync sca = GWT
			.create(ShowConversation.class);

	public static final SetFlagsAsync flags = GWT.create(SetFlags.class);

	public static final DispositionNotificationAsync dispositionNotification = 
		GWT.create(DispositionNotification.class);
	
	public static final HeartbeatAsync heartbeat = GWT.create(Heartbeat.class);

	public static final GetSettingsAsync settings = GWT
			.create(GetSettings.class);

	public static final FilterManagerAsync filters = GWT
			.create(FilterManager.class);

	public static final StoreMessageAsync store = GWT
			.create(StoreMessage.class);

	public static final LogoutAsync logout = GWT.create(Logout.class);

	public static final ContactsManagerAsync contacts = GWT
			.create(ContactsManager.class);
	
	public static final AttachementsManagerAsync atMgr = GWT
			.create(AttachementsManager.class);

	public static final SendMessageAsync send = GWT.create(SendMessage.class);

	public static final GetQuotaAsync quota = GWT.create(GetQuota.class);
	public static final GetTokenAsync token = GWT.create(GetToken.class);

	public static final PushChannelAsync push = GWT.create(PushChannel.class);

	/**
	 * Entry point for ajax calls
	 */
	private AjaxCall() {

	}

}
