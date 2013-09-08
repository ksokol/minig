package com.icegreen.greenmail.imap.commands;

import javax.mail.Message;
import javax.mail.search.SearchTerm;

import com.icegreen.greenmail.imap.ImapRequestLineReader;
import com.icegreen.greenmail.imap.ImapResponse;
import com.icegreen.greenmail.imap.ImapSession;
import com.icegreen.greenmail.imap.ProtocolException;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.MailFolder;

/**
 * Handles processeing for the SEARCH imap command.
 * 
 * @author Darrell DeBoer <darrell@apache.org>
 * @author Kamill Sokol <dev@sokol-web.de>
 * 
 *         Currently, our test classes need only a search by message id. In
 *         future, we should refactor MessageIdTermSearchCommand into a more
 *         sophisticated implementation allowing search by different types as
 *         stated in @see <a href=
 *         "http://docs.oracle.com/javaee/6/api/javax/mail/search/SearchTerm.html"
 *         >Package javax.mail.search</a>
 * 
 */
public class MessageIdTermSearchCommand extends SearchCommand {

	protected void doProcess(ImapRequestLineReader request,
			ImapResponse response, ImapSession session)
			throws ProtocolException, FolderException {
		doProcess(request, response, session, false);
	}

	private SearchCommandParser parser = new SearchCommandParser();

	public void doProcess(ImapRequestLineReader request, ImapResponse response,
			ImapSession session, boolean useUids) throws ProtocolException,
			FolderException {
		// Parse the search term from the request
		SearchTerm searchTerm = parser.searchTerm(request);
		parser.endLine(request);

		MailFolder folder = session.getSelected();
		long[] uids = folder.search(searchTerm);
		StringBuffer idList = new StringBuffer();
		for (int i = 0; i < uids.length; i++) {
			if (i > 0) {
				idList.append(SP);
			}
			long uid = uids[i];
			if (useUids) {
				idList.append(uid);
			} else {
				int msn = folder.getMsn(uid);
				idList.append(msn);
			}
		}

		response.commandResponse(this, idList.toString());

		boolean omitExpunged = (!useUids);
		session.unsolicitedResponses(response, omitExpunged);
		response.commandComplete(this);
	}

	private class SearchCommandParser extends CommandParser {

		public SearchTerm searchTerm(ImapRequestLineReader request)
				throws ProtocolException {

			boolean doAppend = false;
			final StringBuilder sb = new StringBuilder();
			char next = request.nextChar();

			while (next != '\n') {
				request.consume();
				next = request.nextChar();

				if (next == '<') {
					doAppend = true;
				}

				if (doAppend) {
					sb.append(next);
				}

				if (next == '>') {
					doAppend = false;
				}
			}

			return new SearchTerm() {
				private static final long serialVersionUID = 1L;

				public boolean match(Message message) {
					try {
						return message.getHeader("Message-ID")[0].equals(sb
								.toString());
					} catch (Exception e) {
						throw new RuntimeException(e.getMessage(), e);
					}
				}
			};
		}
	}

}
