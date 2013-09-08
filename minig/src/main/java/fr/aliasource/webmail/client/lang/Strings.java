package fr.aliasource.webmail.client.lang;

public interface Strings extends com.google.gwt.i18n.client.Messages {

	/**
	 * Translated "Deliver into folder".
	 * 
	 * @return translated "Deliver into folder"
	 */
	@DefaultMessage("Deliver into folder")
	@Key("actionDeliverInto")
	String actionDeliverInto();

	/**
	 * Translated "Forward it to: ".
	 * 
	 * @return translated "Forward it to: "
	 */
	@DefaultMessage("Forward it to: ")
	@Key("actionForwardItTo")
	String actionForwardItTo();

	/**
	 * Translated "Add Bcc".
	 * 
	 * @return translated "Add Bcc"
	 */
	@DefaultMessage("Add Bcc")
	@Key("addBcc")
	String addBcc();

	/**
	 * Translated "Add Cc".
	 * 
	 * @return translated "Add Cc"
	 */
	@DefaultMessage("Add Cc")
	@Key("addCc")
	String addCc();

	/**
	 * Translated "Add star".
	 * 
	 * @return translated "Add star"
	 */
	@DefaultMessage("Add star")
	@Key("addStar")
	String addStar();

	/**
	 * Translated "Add {0} to Contacts list".
	 * 
	 * @return translated "Add {0} to Contacts list"
	 */
	@DefaultMessage("Add {0} to Contacts list")
	@Key("addToContactList")
	String addToContactList(String arg0);

	/**
	 * Translated "All".
	 * 
	 * @return translated "All"
	 */
	@DefaultMessage("All")
	@Key("all")
	String all();

	/**
	 * Translated "All {0} conversations in {1} are selected".
	 * 
	 * @return translated "All {0} conversations in {1} are selected"
	 */
	@DefaultMessage("All {0} conversations in {1} are selected")
	@Key("allFolderConversationsSelected")
	String allFolderConversationsSelected(String arg0, String arg1);

	/**
	 * Translated "All Mail".
	 * 
	 * @return translated "All Mail"
	 */
	@DefaultMessage("All Mail")
	@Key("allMail")
	String allMail();

	/**
	 * Translated "All messages in {0} have been deleted forever".
	 * 
	 * @return translated "All messages in {0} have been deleted forever"
	 */
	@DefaultMessage("All messages in {0} have been deleted forever")
	@Key("allMessagesDeleted")
	String allMessagesDeleted(String arg0);

	/**
	 * Translated "All conversations on this page are selected".
	 * 
	 * @return translated "All conversations on this page are selected"
	 */
	@DefaultMessage("All conversations on this page are selected")
	@Key("allPageConversationsSelected")
	String allPageConversationsSelected();

	/**
	 * Translated "MiniG".
	 * 
	 * @return translated "MiniG"
	 */
	@DefaultMessage("MiniG")
	@Key("appName")
	String appName();

	@DefaultMessage("Ask for a disposition notification")
	@Key("askForDispositionNotification")
	String askForDispositionNotification();

	/**
	 * Translated "Attach another file".
	 * 
	 * @return translated "Attach another file"
	 */
	@DefaultMessage("Attach another file")
	@Key("attachAnotherFile")
	String attachAnotherFile();

	/**
	 * Translated "Attach a file".
	 * 
	 * @return translated "Attach a file"
	 */
	@DefaultMessage("Attach a file")
	@Key("attachFile")
	String attachFile();

	/**
	 * Translated "Back to".
	 * 
	 * @return translated "Back to"
	 */
	@DefaultMessage("Back to")
	@Key("backTo")
	String backTo();

	/**
	 * Translated "Bcc".
	 * 
	 * @return translated "Bcc"
	 */
	@DefaultMessage("Bcc")
	@Key("bcc")
	String bcc();

	/**
	 * Translated "Calendars".
	 * 
	 * @return translated "Calendars"
	 */
	@DefaultMessage("Calendars")
	@Key("calendars")
	String calendars();

	/**
	 * Translated "Cancel".
	 * 
	 * @return translated "Cancel"
	 */
	@DefaultMessage("Cancel")
	@Key("cancel")
	String cancel();

	/**
	 * Translated "Canned responses".
	 * 
	 * @return translated "Canned responses"
	 */
	@DefaultMessage("Canned responses")
	@Key("cannedResponses")
	String cannedResponses();

	/**
	 * Translated "Cc".
	 * 
	 * @return translated "Cc"
	 */
	@DefaultMessage("Cc")
	@Key("cc")
	String cc();

	/**
	 * Translated "Clear selection".
	 * 
	 * @return translated "Clear selection"
	 */
	@DefaultMessage("Clear selection")
	@Key("clearSelection")
	String clearSelection();

	/**
	 * Translated "[Message is clipped] Download entire message".
	 * 
	 * @return translated "[Message is clipped] Download entire message"
	 */
	@DefaultMessage("[Message is clipped] Download entire message")
	@Key("clippedMessage")
	String clippedMessage();

	/**
	 * Translated "Collapse all".
	 * 
	 * @return translated "Collapse all"
	 */
	@DefaultMessage("Collapse all")
	@Key("collapseAll")
	String collapseAll();

	/**
	 * Translated "Mail composer".
	 * 
	 * @return translated "Mail composer"
	 */
	@DefaultMessage("Mail composer")
	@Key("compose")
	String compose();

	/**
	 * Translated "Move folder {0} to trash ?".
	 * 
	 * @return translated "Move folder {0} to trash ?"
	 */
	@DefaultMessage("Move folder {0} to trash ?")
	@Key("confirmDeleteFolder")
	String confirmDeleteFolder(String arg0);

	/**
	 * Translated
	 * "Folder {0} is in trash.\nDo you really want to delete it (no undo possible) ?"
	 * .
	 * 
	 * @return translated
	 *         "Folder {0} is in trash.\nDo you really want to delete it (no undo possible) ?"
	 */
	@DefaultMessage("Folder {0} is in trash.\nDo you really want to delete it (no undo possible) ?")
	@Key("confirmDirectDelete")
	String confirmDirectDelete(String arg0);

	/**
	 * Translated "Message has not been sent.\nDo you want to discard it ?".
	 * 
	 * @return translated
	 *         "Message has not been sent.\nDo you want to discard it ?"
	 */
	@DefaultMessage("Message has not been sent.\nDo you want to discard it ?")
	@Key("confirmDiscardMessage")
	String confirmDiscardMessage();

	/**
	 * Translated
	 * "This action will affect all {0} conversations in {1}.\nAre you sure you want to continue?"
	 * .
	 * 
	 * @return translated
	 *         "This action will affect all {0} conversations in {1}.\nAre you sure you want to continue?"
	 */
	@DefaultMessage("This action will affect all {0} conversations in {1}.\nAre you sure you want to continue?")
	@Key("confirmFolderAction")
	String confirmFolderAction(String arg0, String arg1);

	/**
	 * Translated
	 * "Converting this message to plain text will lose some formatting.\nAre you sure you want to continue?"
	 * .
	 * 
	 * @return translated
	 *         "Converting this message to plain text will lose some formatting.\nAre you sure you want to continue?"
	 */
	@DefaultMessage("Converting this message to plain text will lose some formatting.\nAre you sure you want to continue?")
	@Key("confirmRichToPlain")
	String confirmRichToPlain();

	/**
	 * Translated "of".
	 * 
	 * @return translated "of"
	 */
	@DefaultMessage("of")
	@Key("convCountof")
	String convCountof();

	/**
	 * Translated "conversations per page".
	 * 
	 * @return translated "conversations per page"
	 */
	@DefaultMessage("conversations per page")
	@Key("convPerPage")
	String convPerPage();

	/**
	 * Translated "The conversation has been moved to the {0}".
	 * 
	 * @return translated "The conversation has been moved to the {0}"
	 */
	@DefaultMessage("The conversation has been moved to the {0}")
	@Key("conversationDeleted")
	String conversationDeleted(String arg0);

	/**
	 * Translated "The conversation has been deleted".
	 * 
	 * @return translated "The conversation has been deleted"
	 */
	@DefaultMessage("The conversation has been deleted")
	@Key("conversationDeletedForever")
	String conversationDeletedForever();

	/**
	 * Translated "The conversation has been marked as spam.".
	 * 
	 * @return translated "The conversation has been marked as spam."
	 */
	@DefaultMessage("The conversation has been marked as spam.")
	@Key("conversationMarkedAsSpam")
	String conversationMarkedAsSpam();

	/**
	 * Translated "The conversation has been moved to {0}.".
	 * 
	 * @return translated "The conversation has been moved to {0}."
	 */
	@DefaultMessage("The conversation has been moved to {0}.")
	@Key("conversationMoveTo")
	String conversationMoveTo(String arg0);

	/**
	 * Translated "{0} conversations have been deleted".
	 * 
	 * @return translated "{0} conversations have been deleted"
	 */
	@DefaultMessage("{0} conversations have been deleted")
	@Key("conversationsDeletedForever")
	String conversationsDeletedForever(String arg0);

	/**
	 * Translated "{0} conversations have been marked as spam. ".
	 * 
	 * @return translated "{0} conversations have been marked as spam. "
	 */
	@DefaultMessage("{0} conversations have been marked as spam. ")
	@Key("conversationsMarkedAsSpam")
	String conversationsMarkedAsSpam(String arg0);

	/**
	 * Translated "{0} conversation have been moved to {1}.".
	 * 
	 * @return translated "{0} conversation have been moved to {1}."
	 */
	@DefaultMessage("{0} conversation have been moved to {1}.")
	@Key("conversationsMoveTo")
	String conversationsMoveTo(String arg0, String arg1);

	/**
	 * Translated "Copy to".
	 * 
	 * @return translated "Copy to"
	 */
	@DefaultMessage("Copy to")
	@Key("copyTo")
	String copyTo();

	/**
	 * Translated "Message has been copied to {0}".
	 * 
	 * @return translated "Message has been copied to {0}"
	 */
	@DefaultMessage("Message has been copied to {0}")
	@Key("copiedTo")
	String copiedTo(String arg0);

	/**
	 * Translated "Create".
	 * 
	 * @return translated "Create"
	 */
	@DefaultMessage("Create")
	@Key("create")
	String create();

	/**
	 * Translated "Create a folder".
	 * 
	 * @return translated "Create a folder"
	 */
	@DefaultMessage("Create a folder")
	@Key("createFolder")
	String createFolder();

	/**
	 * Translated "Create a subfolder".
	 * 
	 * @return translated "Create a subfolder"
	 */
	@DefaultMessage("Create a subfolder")
	@Key("createSubFolder")
	String createSubFolder();

	/**
	 * Translated "Create a subfolder in".
	 * 
	 * @return translated "Create a subfolder in"
	 */
	@DefaultMessage("Create a subfolder in")
	@Key("createSubFolderIn")
	String createSubFolderIn();

	/**
	 * Translated "Date".
	 * 
	 * @return translated "Date"
	 */
	@DefaultMessage("Date")
	@Key("date")
	String date();

	/**
	 * Translated "e.g. today, monday, 13 february, 2008-02-13, 13/02/2008".
	 * 
	 * @return translated
	 *         "e.g. today, monday, 13 february, 2008-02-13, 13/02/2008"
	 */
	@DefaultMessage("e.g. today, monday, 13 february, 2008-02-13, 13/02/2008")
	@Key("dateLegend")
	String dateLegend();

	/**
	 * Translated "MMM dd".
	 * 
	 * @return translated "MMM dd"
	 */
	@DefaultMessage("MMM dd")
	@Key("dateOldMail")
	String dateOldMail();

	/**
	 * Translated "EEE, MMM dd, yyy 'at' h:mm a".
	 * 
	 * @return translated "EEE, MMM dd, yyy 'at' h:mm a"
	 */
	@DefaultMessage("EEE, MMM dd, yyy 'at' h:mm a")
	@Key("dateOldMailDetails")
	String dateOldMailDetails();

	/**
	 * Translated "1 hour ago".
	 * 
	 * @return translated "1 hour ago"
	 */
	@DefaultMessage("1 hour ago")
	@Key("dateOneHourAgo")
	String dateOneHourAgo();

	/**
	 * Translated "1 minute ago".
	 * 
	 * @return translated "1 minute ago"
	 */
	@DefaultMessage("1 minute ago")
	@Key("dateOneMinuteAgo")
	String dateOneMinuteAgo();

	/**
	 * Translated "h:mm a".
	 * 
	 * @return translated "h:mm a"
	 */
	@DefaultMessage("h:mm a")
	@Key("dateTodayMail")
	String dateTodayMail();

	/**
	 * Translated "Date within".
	 * 
	 * @return translated "Date within"
	 */
	@DefaultMessage("Date within")
	@Key("dateWithin")
	String dateWithin();

	/**
	 * Translated "{0} hours ago".
	 * 
	 * @return translated "{0} hours ago"
	 */
	@DefaultMessage("{0} hours ago")
	@Key("dateXHoursAgo")
	String dateXHoursAgo(String arg0);

	/**
	 * Translated "{0} minutes ago".
	 * 
	 * @return translated "{0} minutes ago"
	 */
	@DefaultMessage("{0} minutes ago")
	@Key("dateXMinutesAgo")
	String dateXMinutesAgo(String arg0);

	/**
	 * Translated "Delete".
	 * 
	 * @return translated "Delete"
	 */
	@DefaultMessage("Delete")
	@Key("delete")
	String delete();

	/**
	 * Translated "Delete forever".
	 * 
	 * @return translated "Delete forever"
	 */
	@DefaultMessage("Delete forever")
	@Key("deleteForever")
	String deleteForever();

	/**
	 * Translated "Delete this message".
	 * 
	 * @return translated "Delete this message"
	 */
	@DefaultMessage("Delete this message")
	@Key("deleteThisMessage")
	String deleteThisMessage();

	/**
	 * Translated "Choose folder...".
	 * 
	 * @return translated "Choose folder..."
	 */
	@DefaultMessage("Choose folder...")
	@Key("deliverFolderPlaceholder")
	String deliverFolderPlaceholder();

	/**
	 * Translated "Discard".
	 * 
	 * @return translated "Discard"
	 */
	@DefaultMessage("Discard")
	@Key("discard")
	String discard();

	/**
	 * Translated "Display images below".
	 * 
	 * @return translated "Display images below"
	 */
	@DefaultMessage("Display images below")
	@Key("displayImagesBelow")
	String displayImagesBelow();

	@DefaultMessage("Accept")
	@Key("dispositionNotificationAccept")
	String dispositionNotificationAccept();

	@DefaultMessage("Later")
	@Key("dispositionNotificationLater")
	String dispositionNotificationLater();

	@DefaultMessage("{0} would like to be notified of this mail delivery :")
	@Key("dispositionNotificationMessage")
	String dispositionNotificationMessage(String recipients);

	@DefaultMessage("{0} would like to be notified of this mail delivery :")
	@Key("dispositionNotificationMessagePlural")
	String dispositionNotificationMessagePlural(String recipients);

	@DefaultMessage("Refuse")
	@Key("dispositionNotificationRefuse")
	String dispositionNotificationRefuse();

	/**
	 * Translated "<b>Drop on a folder to move all conversations there</b>".
	 * 
	 * @return translated
	 *         "<b>Drop on a folder to move all conversations there</b>"
	 */
	@DefaultMessage("<b>Drop on a folder to move all conversations there</b>")
	@Key("dndDropAll")
	String dndDropAll();

	/**
	 * Translated "<b>Drop this conversation on a folder to move it</b>".
	 * 
	 * @return translated "<b>Drop this conversation on a folder to move it</b>"
	 */
	@DefaultMessage("<b>Drop this conversation on a folder to move it</b>")
	@Key("dndDropThis")
	String dndDropThis();

	/**
	 * Translated
	 * "<b>Drop those {0} conversations on a folder to move them</b>".
	 * 
	 * @return translated
	 *         "<b>Drop those {0} conversations on a folder to move them</b>"
	 */
	@DefaultMessage("<b>Drop those {0} conversations on a folder to move them</b>")
	@Key("dndDropThose")
	String dndDropThose(String arg0);

	/**
	 * Translated "Doesn''t have".
	 * 
	 * @return translated "Doesn''t have"
	 */
	@DefaultMessage("Doesn''t have")
	@Key("doNotHave")
	String doNotHave();

	/**
	 * Translated "Download".
	 * 
	 * @return translated "Download"
	 */
	@DefaultMessage("Download")
	@Key("downloadAttachment")
	String downloadAttachment();

	/**
	 * Translated "Draft autosaved at {0}".
	 * 
	 * @return translated "Draft autosaved at {0}"
	 */
	@DefaultMessage("Draft autosaved at {0}")
	@Key("draftAutoSavedAt")
	String draftAutoSavedAt(String arg0);

	/**
	 * Translated "Draft saved".
	 * 
	 * @return translated "Draft saved"
	 */
	@DefaultMessage("Draft saved")
	@Key("draftSaved")
	String draftSaved();

	/**
	 * Translated "Drafts".
	 * 
	 * @return translated "Drafts"
	 */
	@DefaultMessage("Drafts")
	@Key("drafts")
	String drafts();

	/**
	 * Translated "Edit folders".
	 * 
	 * @return translated "Edit folders"
	 */
	@DefaultMessage("Edit folders")
	@Key("editFolders")
	String editFolders();

	/**
	 * Translated "Edit subject".
	 * 
	 * @return translated "Edit subject"
	 */
	@DefaultMessage("Edit subject")
	@Key("editSubject")
	String editSubject();

	/**
	 * Translated "email".
	 * 
	 * @return translated "email"
	 */
	@DefaultMessage("email")
	@Key("email")
	String email();

	/**
	 * Translated "Empty {0} now".
	 * 
	 * @return translated "Empty {0} now"
	 */
	@DefaultMessage("Empty {0} now")
	@Key("emptyFolder")
	String emptyFolder(String arg0);

	/**
	 * Translated "Please specify at least one recipient.".
	 * 
	 * @return translated "Please specify at least one recipient."
	 */
	@DefaultMessage("Please specify at least one recipient.")
	@Key("emptyRecipient")
	String emptyRecipient();

	/**
	 * Translated "Delete all spam messages now".
	 * 
	 * @return translated "Delete all spam messages now"
	 */
	@DefaultMessage("Delete all spam messages now")
	@Key("emptySpam")
	String emptySpam();

	/**
	 * Translated "Empty subject is not allowed.".
	 * 
	 * @return translated "Empty subject is not allowed."
	 */
	@DefaultMessage("Empty subject is not allowed.")
	@Key("emptySubject")
	String emptySubject();

	/**
	 * Translated "Error while moving conversation".
	 * 
	 * @return translated "Error while moving conversation"
	 */
	@DefaultMessage("Error while moving conversation")
	@Key("errorMovingConv")
	String errorMovingConv();

	/**
	 * Translated "Expand all".
	 * 
	 * @return translated "Expand all"
	 */
	@DefaultMessage("Expand all")
	@Key("expandAll")
	String expandAll();

	/**
	 * Translated "Export all".
	 * 
	 * @return translated "Export all"
	 */
	@DefaultMessage("Export all")
	@Key("exportAll")
	String exportAll();

	/**
	 * Translated "Export".
	 * 
	 * @return translated "Export"
	 */
	@DefaultMessage("Export")
	@Key("exportOne")
	String exportOne();

	/**
	 * Translated "Failed to save draft".
	 * 
	 * @return translated "Failed to save draft"
	 */
	@DefaultMessage("Failed to save draft")
	@Key("failedToSaveDraft")
	String failedToSaveDraft();

	/**
	 * Translated "Failed to save template".
	 * 
	 * @return translated "Failed to save template"
	 */
	@DefaultMessage("Failed to save template")
	@Key("failedToSaveTemplate")
	String failedToSaveTemplate();

	/**
	 * Translated "Folders".
	 * 
	 * @return translated "Folders"
	 */
	@DefaultMessage("Folders")
	@Key("folders")
	String folders();

	/**
	 * Translated "Forward".
	 * 
	 * @return translated "Forward"
	 */
	@DefaultMessage("Forward")
	@Key("forward")
	String forward();

	/**
	 * Translated "and".
	 * 
	 * @return translated "and"
	 */
	@DefaultMessage("and")
	@Key("forwardAnd")
	String forwardAnd();

	/**
	 * Translated "Delete MiniG''s copy".
	 * 
	 * @return translated "Delete MiniG''s copy"
	 */
	@DefaultMessage("Delete MiniG''s copy")
	@Key("forwardDelete")
	String forwardDelete();

	/**
	 * Translated "Forwarding:".
	 * 
	 * @return translated "Forwarding:"
	 */
	@DefaultMessage("Forwarding:")
	@Key("forwardDescription")
	String forwardDescription();

	/**
	 * Translated "Disable forwarding".
	 * 
	 * @return translated "Disable forwarding"
	 */
	@DefaultMessage("Disable forwarding")
	@Key("forwardDisable")
	String forwardDisable();

	/**
	 * Translated "Forward a copy of incoming mail to".
	 * 
	 * @return translated "Forward a copy of incoming mail to"
	 */
	@DefaultMessage("Forward a copy of incoming mail to")
	@Key("forwardEnable")
	String forwardEnable();

	/**
	 * Translated "Keep MiniG''s copy in the inbox".
	 * 
	 * @return translated "Keep MiniG''s copy in the inbox"
	 */
	@DefaultMessage("Keep MiniG''s copy in the inbox")
	@Key("forwardKeep")
	String forwardKeep();

	/**
	 * Translated "email address".
	 * 
	 * @return translated "email address"
	 */
	@DefaultMessage("email address")
	@Key("forwardPlaceholder")
	String forwardPlaceholder();

	/**
	 * Translated "Forwarded message".
	 * 
	 * @return translated "Forwarded message"
	 */
	@DefaultMessage("Forwarded message")
	@Key("forwardedMessage")
	String forwardedMessage();

	/**
	 * Translated "From".
	 * 
	 * @return translated "From"
	 */
	@DefaultMessage("From")
	@Key("from")
	String from();

	/**
	 * Translated "General".
	 * 
	 * @return translated "General"
	 */
	@DefaultMessage("General")
	@Key("general")
	String general();

	/**
	 * Translated "Has attachments".
	 * 
	 * @return translated "Has attachments"
	 */
	@DefaultMessage("Has attachments")
	@Key("hasAttachments")
	String hasAttachments();

	/**
	 * Translated "hide detail".
	 * 
	 * @return translated "hide detail"
	 */
	@DefaultMessage("hide detail")
	@Key("hideDetail")
	String hideDetail();

	/**
	 * Translated "Hide quoted text".
	 * 
	 * @return translated "Hide quoted text"
	 */
	@DefaultMessage("Hide quoted text")
	@Key("hideQuotedText")
	String hideQuotedText();

	/**
	 * Translated "Images are not displayed.".
	 * 
	 * @return translated "Images are not displayed."
	 */
	@DefaultMessage("Images are not displayed.")
	@Key("imagesAreNotDisplayed")
	String imagesAreNotDisplayed();

	/**
	 * Translated "Very important message".
	 * 
	 * @return translated "Very important message"
	 */
	@DefaultMessage("Very important message")
	@Key("importantMessage")
	String importantMessage();

	/**
	 * Translated "Return receipt".
	 * 
	 * @return translated "Return receipt"
	 */
	@DefaultMessage("Return receipt")
	@Key("receipt")
	String receipt();

	/**
	 * Translated "Inbox".
	 * 
	 * @return translated "Inbox"
	 */
	@DefaultMessage("Inbox")
	@Key("inbox")
	String inbox();

	/**
	 * Translated
	 * "The canned response will replace your current message. Are you sure you want to proceed?"
	 * .
	 * 
	 * @return translated
	 *         "The canned response will replace your current message. Are you sure you want to proceed?"
	 */
	@DefaultMessage("The canned response will replace your current message. Are you sure you want to proceed?")
	@Key("loadCannedResponse")
	String loadCannedResponse();

	/**
	 * Translated "Loading conversation".
	 * 
	 * @return translated "Loading conversation"
	 */
	@DefaultMessage("Loading conversation")
	@Key("loadingConversation")
	String loadingConversation();

	/**
	 * Translated "Loading MiniG...".
	 * 
	 * @return translated "Loading MiniG..."
	 */
	@DefaultMessage("Loading MiniG...")
	@Key("loadingMiniG")
	String loadingMiniG();

	/**
	 * Translated "Mail & {0}".
	 * 
	 * @return translated "Mail & {0}"
	 */
	@DefaultMessage("Mail & {0}")
	@Key("mailAndTrash")
	String mailAndTrash(String arg0);

	/**
	 * Translated "Copy to {0} is successful".
	 * 
	 * @return translated "Copy to {0} is successful"
	 */
	@DefaultMessage("Copy to {0} is successful")
	@Key("mailCopyDone")
	String mailCopyDone(String arg0);

	/**
	 * Translated "Mail-by".
	 * 
	 * @return translated "Mail-by"
	 */
	@DefaultMessage("Mail-by")
	@Key("mailby")
	String mailby();

	/**
	 * Translated "Mark as read".
	 * 
	 * @return translated "Mark as read"
	 */
	@DefaultMessage("Mark as read")
	@Key("markAsRead")
	String markAsRead();

	/**
	 * Translated "Mark as spam".
	 * 
	 * @return translated "Mark as spam"
	 */
	@DefaultMessage("Mark as spam")
	@Key("markAsSpam")
	String markAsSpam();

	/**
	 * Translated "Mark as unread".
	 * 
	 * @return translated "Mark as unread"
	 */
	@DefaultMessage("Mark as unread")
	@Key("markAsUnread")
	String markAsUnread();

	/**
	 * Translated "Maximum page size".
	 * 
	 * @return translated "Maximum page size"
	 */
	@DefaultMessage("Maximum page size")
	@Key("maxPageSize")
	String maxPageSize();

	/**
	 * Translated "The message has been moved to the {0}".
	 * 
	 * @return translated "The message has been moved to the {0}"
	 */
	@DefaultMessage("The message has been moved to {0}")
	@Key("messageDeleted")
	String moveMessage(String arg0);

	/**
	 * Translated "The message has been deleted".
	 * 
	 * @return translated "The message has been deleted"
	 */
	@DefaultMessage("The message has been deleted")
	@Key("messageDeletedForever")
	String messageDeletedForever();

	/**
	 * Translated "Your message has been discarded".
	 * 
	 * @return translated "Your message has been discarded"
	 */
	@DefaultMessage("Your message has been discarded")
	@Key("messageDiscarded")
	String messageDiscarded();

	/**
	 * Translated "Your message has been sent. Storing a copy ...".
	 * 
	 * @return translated "Your message has been sent. Storing a copy ..."
	 */
	@DefaultMessage("Your message has been sent. Storing a copy ...")
	@Key("messageSent")
	String messageSent();

	/**
	 * Translated "More actions".
	 * 
	 * @return translated "More actions"
	 */
	@DefaultMessage("More actions")
	@Key("moreActions")
	String moreActions();

	/**
	 * Translated "Move to".
	 * 
	 * @return translated "Move to"
	 */
	@DefaultMessage("Move to")
	@Key("moveTo")
	String moveTo();

	/**
	 * Translated "Newer".
	 * 
	 * @return translated "Newer"
	 */
	@DefaultMessage("Newer")
	@Key("newer")
	String newer();

	/**
	 * Translated "Newest".
	 * 
	 * @return translated "Newest"
	 */
	@DefaultMessage("Newest")
	@Key("newest")
	String newest();

	/**
	 * Translated "Next ›".
	 * 
	 * @return translated "Next ›"
	 */
	@DefaultMessage("Next ›")
	@Key("nextConversation")
	String nextConversation();

	/**
	 * Translated "No message in this folder".
	 * 
	 * @return translated "No message in this folder"
	 */
	@DefaultMessage("No message in this folder")
	@Key("noAvailableConversations")
	String noAvailableConversations();

	/**
	 * Translated "No available folders".
	 * 
	 * @return translated "No available folders"
	 */
	@DefaultMessage("No available folders")
	@Key("noAvailableFolders")
	String noAvailableFolders();

	/**
	 * Translated "No canned responses".
	 * 
	 * @return translated "No canned responses"
	 */
	@DefaultMessage("No canned responses")
	@Key("noCannedResponses")
	String noCannedResponses();

	/**
	 * Translated "None".
	 * 
	 * @return translated "None"
	 */
	@DefaultMessage("None")
	@Key("none")
	String none();

	/**
	 * Translated "Not Spam".
	 * 
	 * @return translated "Not Spam"
	 */
	@DefaultMessage("Not Spam")
	@Key("notSpam")
	String notSpam();

	/**
	 * Translated "Of".
	 * 
	 * @return translated "Of"
	 */
	@DefaultMessage("Of")
	@Key("of")
	String of();

	/**
	 * Translated "Older".
	 * 
	 * @return translated "Older"
	 */
	@DefaultMessage("Older")
	@Key("older")
	String older();

	/**
	 * Translated "Oldest".
	 * 
	 * @return translated "Oldest"
	 */
	@DefaultMessage("Oldest")
	@Key("oldest")
	String oldest();

	/**
	 * Translated "1 day".
	 * 
	 * @return translated "1 day"
	 */
	@DefaultMessage("1 day")
	@Key("one_day")
	String one_day();

	/**
	 * Translated "1 month".
	 * 
	 * @return translated "1 month"
	 */
	@DefaultMessage("1 month")
	@Key("one_month")
	String one_month();

	/**
	 * Translated "1 week".
	 * 
	 * @return translated "1 week"
	 */
	@DefaultMessage("1 week")
	@Key("one_week")
	String one_week();

	/**
	 * Translated "1 year".
	 * 
	 * @return translated "1 year"
	 */
	@DefaultMessage("1 year")
	@Key("one_year")
	String one_year();

	/**
	 * Translated "Permission denied.".
	 * 
	 * @return translated "Permission denied."
	 */
	@DefaultMessage("Permission denied.")
	@Key("permissionDenied")
	String permissionDenied();

	/**
	 * Translated "Hide".
	 * 
	 * @return translated "Hide"
	 */
	@DefaultMessage("Hide")
	@Key("plainText")
	String plainText();

	/**
	 * Translated "‹ Prev.".
	 * 
	 * @return translated "‹ Prev."
	 */
	@DefaultMessage("‹ Prev.")
	@Key("previousConversation")
	String previousConversation();

	/**
	 * Translated "Print all".
	 * 
	 * @return translated "Print all"
	 */
	@DefaultMessage("Print all")
	@Key("printAll")
	String printAll();

	/**
	 * Translated "Print".
	 * 
	 * @return translated "Print"
	 */
	@DefaultMessage("Print")
	@Key("printOne")
	String printOne();

	/**
	 * Translated "Quoting {0} :".
	 * 
	 * @return translated "Quoting {0} :"
	 */
	@DefaultMessage("Quoting {0} :")
	@Key("quoteSender")
	String quoteSender(String arg0);

	/**
	 * Translated "Read".
	 * 
	 * @return translated "Read"
	 */
	@DefaultMessage("Read")
	@Key("read")
	String read();

	/**
	 * Translated "Read mail".
	 * 
	 * @return translated "Read mail"
	 */
	@DefaultMessage("Read mail")
	@Key("readMail")
	String readMail();

	/**
	 * Translated "Recent conversations".
	 * 
	 * @return translated "Recent conversations"
	 */
	@DefaultMessage("Recent conversations")
	@Key("recentConversations")
	String recentConversations();

	/**
	 * Translated
	 * "Address is not recognize. Please make sure that address is properly formed."
	 * .
	 * 
	 * @return translated
	 *         "Address is not recognize. Please make sure that address is properly formed."
	 */
	@DefaultMessage("Address is not recognize. Please make sure that address is properly formed.")
	@Key("recipientError")
	String recipientError();

	/**
	 * Translated "Refresh".
	 * 
	 * @return translated "Refresh"
	 */
	@DefaultMessage("Refresh")
	@Key("refresh")
	String refresh();

	/**
	 * Translated "Reload MiniG".
	 * 
	 * @return translated "Reload MiniG"
	 */
	@DefaultMessage("Reload MiniG")
	@Key("reloadMinig")
	String reloadMinig();

	/**
	 * Translated "Remove".
	 * 
	 * @return translated "Remove"
	 */
	@DefaultMessage("Remove")
	@Key("remove")
	String remove();

	/**
	 * Translated "Remove star".
	 * 
	 * @return translated "Remove star"
	 */
	@DefaultMessage("Remove star")
	@Key("removeStar")
	String removeStar();

	/**
	 * Translated "Rename".
	 * 
	 * @return translated "Rename"
	 */
	@DefaultMessage("Rename")
	@Key("renameFolder")
	String renameFolder();

	/**
	 * Translated "Reply".
	 * 
	 * @return translated "Reply"
	 */
	@DefaultMessage("Reply")
	@Key("reply")
	String reply();

	/**
	 * Translated "Reply to all".
	 * 
	 * @return translated "Reply to all"
	 */
	@DefaultMessage("Reply to all")
	@Key("replyToAll")
	String replyToAll();

	/**
	 * Translated "Rich formatting".
	 * 
	 * @return translated "Rich formatting"
	 */
	@DefaultMessage("Rich formatting")
	@Key("richFormatting")
	String richFormatting();

	/**
	 * Translated "Save".
	 * 
	 * @return translated "Save"
	 */
	@DefaultMessage("Save")
	@Key("save")
	String save();

	/**
	 * Translated "Save as template".
	 * 
	 * @return translated "Save as template"
	 */
	@DefaultMessage("Save as template")
	@Key("saveAsTemplate")
	String saveAsTemplate();

	/**
	 * Translated "Save changes".
	 * 
	 * @return translated "Save changes"
	 */
	@DefaultMessage("Save changes")
	@Key("saveChanges")
	String saveChanges();

	/**
	 * Translated "Save now".
	 * 
	 * @return translated "Save now"
	 */
	@DefaultMessage("Save now")
	@Key("saveNow")
	String saveNow();

	/**
	 * Translated "Select".
	 * 
	 * @return translated "Select"
	 */
	@DefaultMessage("Select")
	@Key("select")
	String select();

	/**
	 * Translated "Select all {0} conversations in {1}".
	 * 
	 * @return translated "Select all {0} conversations in {1}"
	 */
	@DefaultMessage("Select all {0} conversations in {1}")
	@Key("selectAllConversations")
	String selectAllConversations(String arg0, String arg1);

	/**
	 * Translated "Send".
	 * 
	 * @return translated "Send"
	 */
	@DefaultMessage("Send")
	@Key("send")
	String send();

	/**
	 * Translated "Sent".
	 * 
	 * @return translated "Sent"
	 */
	@DefaultMessage("Sent")
	@Key("sent")
	String sent();

	/**
	 * Translated "Settings".
	 * 
	 * @return translated "Settings"
	 */
	@DefaultMessage("Settings")
	@Key("settings")
	String settings();

	/**
	 * Translated "Show".
	 * 
	 * @return translated "Show"
	 */
	@DefaultMessage("Show")
	@Key("show")
	String show();

	/**
	 * Translated "All".
	 * 
	 * @return translated "All"
	 */
	@DefaultMessage("All")
	@Key("showAllContactsRecentConversations")
	String showAllContactsRecentConversations();

	/**
	 * Translated "Any".
	 * 
	 * @return translated "Any"
	 */
	@DefaultMessage("Any")
	@Key("showAnyContactsRecentConversations")
	String showAnyContactsRecentConversations();

	/**
	 * Translated "show detail".
	 * 
	 * @return translated "show detail"
	 */
	@DefaultMessage("show detail")
	@Key("showDetail")
	String showDetail();

	/**
	 * Translated "Show quoted text".
	 * 
	 * @return translated "Show quoted text"
	 */
	@DefaultMessage("Show quoted text")
	@Key("showQuotedText")
	String showQuotedText();

	/**
	 * Translated "Show".
	 * 
	 * @return translated "Show"
	 */
	@DefaultMessage("Show")
	@Key("showRecentConversations")
	String showRecentConversations();

	/**
	 * Translated "Show search options".
	 * 
	 * @return translated "Show search options"
	 */
	@DefaultMessage("Show search options")
	@Key("showSearchOptions")
	String showSearchOptions();

	/**
	 * Translated "Sign out".
	 * 
	 * @return translated "Sign out"
	 */
	@DefaultMessage("Sign out")
	@Key("signOut")
	String signOut();

	/**
	 * Translated "B".
	 * 
	 * @return translated "B"
	 */
	@DefaultMessage("B")
	@Key("sizeByte")
	String sizeByte();

	/**
	 * Translated "KB".
	 * 
	 * @return translated "KB"
	 */
	@DefaultMessage("KB")
	@Key("sizeKilobyte")
	String sizeKilobyte();

	/**
	 * Translated "MB".
	 * 
	 * @return translated "MB"
	 */
	@DefaultMessage("MB")
	@Key("sizeMegabyte")
	String sizeMegabyte();

	/**
	 * Translated "The server refused to send your email (reason: {0})".
	 * 
	 * @return translated "The server refused to send your email (reason: {0})"
	 */
	@DefaultMessage("The server refused to send your email (reason: {0})")
	@Key("smtpError")
	String smtpError(String arg0);

	/**
	 * Translated "Spam".
	 * 
	 * @return translated "Spam"
	 */
	@DefaultMessage("Spam")
	@Key("spam")
	String spam();

	/**
	 * Translated "Starred".
	 * 
	 * @return translated "Starred"
	 */
	@DefaultMessage("Starred")
	@Key("starred")
	String starred();

	/**
	 * Translated "Subject".
	 * 
	 * @return translated "Subject"
	 */
	@DefaultMessage("Subject")
	@Key("subject")
	String subject();

	/**
	 * Translated "Subscribe".
	 * 
	 * @return translated "Subscribe"
	 */
	@DefaultMessage("Subscribe")
	@Key("subscribe")
	String subscribe();

	/**
	 * Translated
	 * "Your MiniG was inactive for too long. Click on the above link to reload."
	 * .
	 * 
	 * @return translated
	 *         "Your MiniG was inactive for too long. Click on the above link to reload."
	 */
	@DefaultMessage("Your MiniG was inactive for too long. Click on the above link to reload.")
	@Key("tartifletteDescription")
	String tartifletteDescription();

	/**
	 * Translated "Your MiniG session has expired".
	 * 
	 * @return translated "Your MiniG session has expired"
	 */
	@DefaultMessage("Your MiniG session has expired")
	@Key("tartifletteTitle")
	String tartifletteTitle();

	/**
	 * Translated "Template saved".
	 * 
	 * @return translated "Template saved"
	 */
	@DefaultMessage("Template saved")
	@Key("templateSaved")
	String templateSaved();

	/**
	 * Translated "To".
	 * 
	 * @return translated "To"
	 */
	@DefaultMessage("To")
	@Key("to")
	String to();

	/**
	 * Translated "today".
	 * 
	 * @return translated "today"
	 */
	@DefaultMessage("today")
	@Key("today")
	String today();

	/**
	 * Translated "Trash".
	 * 
	 * @return translated "Trash"
	 */
	@DefaultMessage("Trash")
	@Key("trash")
	String trash();

	/**
	 * Translated "Undo".
	 * 
	 * @return translated "Undo"
	 */
	@DefaultMessage("Undo")
	@Key("undo")
	String undo();

	/**
	 * Translated "Undo discard".
	 * 
	 * @return translated "Undo discard"
	 */
	@DefaultMessage("Undo discard")
	@Key("undoDiscard")
	String undoDiscard();

	/**
	 * Translated "Unread".
	 * 
	 * @return translated "Unread"
	 */
	@DefaultMessage("Unread")
	@Key("unread")
	String unread();

	/**
	 * Translated "Enables/Disables unread filtering".
	 * 
	 * @return translated "Enables/Disables unread filtering"
	 */
	@DefaultMessage("Enables/Disables unread filtering")
	@Key("unreadFilteringTip")
	String unreadFilteringTip();

	/**
	 * Translated "Unread mail".
	 * 
	 * @return translated "Unread mail"
	 */
	@DefaultMessage("Unread mail")
	@Key("unreadMail")
	String unreadMail();

	/**
	 * Translated "Unsubscribe".
	 * 
	 * @return translated "Unsubscribe"
	 */
	@DefaultMessage("Unsubscribe")
	@Key("unsubscribe")
	String unsubscribe();

	/**
	 * Translated "View".
	 * 
	 * @return translated "View"
	 */
	@DefaultMessage("View")
	@Key("viewAttachment")
	String viewAttachment();

	/**
	 * Translated "View sent message".
	 * 
	 * @return translated "View sent message"
	 */
	@DefaultMessage("View sent message")
	@Key("viewSent")
	String viewSent();

	/**
	 * Translated "{0} days".
	 * 
	 * @return translated "{0} days"
	 */
	@DefaultMessage("{0} days")
	@Key("x_days")
	String x_days(String arg0);

	/**
	 * Translated "{0} months".
	 * 
	 * @return translated "{0} months"
	 */
	@DefaultMessage("{0} months")
	@Key("x_months")
	String x_months(String arg0);

	/**
	 * Translated "{0} weeks".
	 * 
	 * @return translated "{0} weeks"
	 */
	@DefaultMessage("{0} weeks")
	@Key("x_weeks")
	String x_weeks(String arg0);

	/**
	 * Translated "{0} years".
	 * 
	 * @return translated "{0} years"
	 */
	@DefaultMessage("{0} years")
	@Key("x_years")
	String x_years(String arg0);
}
