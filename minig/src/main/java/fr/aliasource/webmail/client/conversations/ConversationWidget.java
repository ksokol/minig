package fr.aliasource.webmail.client.conversations;

import com.google.gwt.user.client.ui.HTML;

import fr.aliasource.webmail.client.XssUtils;
import fr.aliasource.webmail.client.shared.IClientMessage;

public final class ConversationWidget extends HTML {

	public static final String createHTML(IClientMessage conversation) {
		StringBuilder b = new StringBuilder(100);

		// Folder f = new Folder(conversation.getSourceFolder());

		// FIXME
		// if (usedFolders.size() > 1 &&
		// !conversation.getSourceFolder().equalsIgnoreCase("inbox")) {
		// b.append("<span class=\"convFolderTag\" style=\"background-color: " +
		// WebSafeColors.htmlColor(f)
		// + "; color: " + "#ff7d7d" + ";\">");
		// b.append(WebmailController.get().displayName(f));
		// b.append("</span>");
		// }

		b.append("<span class=\"");
		b.append(conversation.getRead() ? "conversationReadLabel"
				: "conversationUnreadLabel");
		b.append("\" ");

		b.append(">");
		b.append(XssUtils.safeHtml(conversation.getSubject()));
		b.append("</span>");

		return b.toString();
	}

	public ConversationWidget(IClientMessage c) {
		super(createHTML(c), false);
	}

}
