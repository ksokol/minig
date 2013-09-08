package fr.aliasource.webmail.client.conversations;

import java.util.Set;

import com.google.gwt.user.client.ui.HTML;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.WebSafeColors;
import fr.aliasource.webmail.client.XssUtils;
import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.Conversation;
import fr.aliasource.webmail.client.shared.Folder;

public final class ConversationWidget extends HTML {

	public static final String createHTML(Set<Folder> usedFolders,
			Conversation conversation) {
		StringBuilder b = new StringBuilder(100);

		Folder f = new Folder(conversation.getSourceFolder());
		if (usedFolders.size() > 1
				&& !conversation.getSourceFolder().equalsIgnoreCase("inbox")) {
			b.append("<span class=\"convFolderTag\" style=\"background-color: "
					+ WebSafeColors.htmlColor(f) + "; color: "
					+ WebSafeColors.fgColor(f) + ";\">");
			b.append(WebmailController.get().displayName(f));
			b.append("</span>");
		}

		b.append("<span class=\"");
		b.append(conversation.isUnread() ? "conversationUnreadLabel"
				: "conversationReadLabel");
		b.append("\" ");
		b.append(conversation.isHighPriority() ? "style=\"color: red;\"" : "");
		b.append(">");
		b.append(XssUtils.safeHtml(conversation.getTitle()));
		b.append("</span>&nbsp;-&nbsp;<span class=\"conversationPreview\" ");
		b.append(conversation.isHighPriority() ? "style=\"color: #ff7d7d;\""
				: "");
		b.append(">");
		if (conversation.getPreview().contains(I18N.strings.noPreview())) {
			b.append(I18N.strings.noPreview());
		} else {
			b.append(XssUtils.safeHtml(conversation.getPreview()));
		}
		b.append("</span>");

		return b.toString();
	}

	public ConversationWidget(Set<Folder> usedFolders, Conversation c) {
		super(createHTML(usedFolders, c), false);
	}

}
