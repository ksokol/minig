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

package fr.aliasource.webmail.server.proxy.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import fr.aliasource.webmail.client.shared.AttachmentMetadata;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ContactGroup;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.Folder;
import fr.aliasource.webmail.client.shared.EmailAddress;
import fr.aliasource.webmail.client.shared.MessageId;
import fr.aliasource.webmail.client.shared.book.UiContact;
import fr.aliasource.webmail.client.shared.book.UiEmail;

/**
 * Creates & stores fake data for easy UI testing
 * 
 * @author tom
 * 
 */
public class DummyDataProvider {

	private Random rand;
	private List<ConversationReference> search;

	private Map<ConversationId, ConversationReference> idx;

	private Map<Folder, List<ConversationReference>> mailData;

	public static final int ONE_HOUR = 1000 * 3600;

	private int convIdAllocator;

	private int attachIdAllocator;

	private Set<String> attachIds;

	public DummyDataProvider() {
		rand = new Random(System.currentTimeMillis());
		idx = new HashMap<ConversationId, ConversationReference>();
		attachIds = new HashSet<String>();

		LinkedList<ConversationReference> empty = new LinkedList<ConversationReference>();

		LinkedList<ConversationReference> inbox = new LinkedList<ConversationReference>();
		fillFullList(new Folder("INBOX"), inbox, 30);

		LinkedList<ConversationReference> second = new LinkedList<ConversationReference>();
		fillFullList(new Folder("second"), second, 25);

		LinkedList<ConversationReference> drafts = new LinkedList<ConversationReference>();
		LinkedList<ConversationReference> sent = new LinkedList<ConversationReference>();
		LinkedList<ConversationReference> trash = new LinkedList<ConversationReference>();

		mailData = new HashMap<Folder, List<ConversationReference>>();
		mailData.put(new Folder("INBOX"), inbox);
		mailData.put(new Folder("INBOX.Drafts"), drafts);
		mailData.put(new Folder("INBOX.Sent"), sent);
		mailData.put(new Folder("INBOX.Trash"), trash);
		mailData.put(new Folder("second"), second);
		mailData.put(new Folder("empty"), empty);

		initSearch();
	}

	/**
	 * Generates a dummy list of conversations for the given folder.
	 * 
	 * @param folder
	 * @param l
	 */
	private void fillFullList(Folder folder, List<ConversationReference> l,
			int fullListLength) {

		for (int i = 0; i < fullListLength; i++) {
			ConversationReference cr = getDummyConv(folder);
			l.add(cr);
		}
	}

	public List<ConversationReference> get(Folder f) {
		return mailData.get(f);
	}

	public List<AttachmentMetadata> getAttachments(Folder folder) {
		List<AttachmentMetadata> ret = new ArrayList<AttachmentMetadata>();
		for (int i = 0; i < 50; i++) {
			AttachmentMetadata metadata = new AttachmentMetadata();
			metadata.setFileName(i + ".name");
			metadata.setSize(randInt(1024 * 1014));
			metadata.setMime("application/octet-stream");
			metadata.setId("" + i);
			metadata.setConversationId(0 + "");
			metadata.setConversationTitle("Fake conversation title ");
			metadata.setConversationDate(randDate().getTime());

			ret.add(metadata);
		}
		return ret;
	}

	public ConversationReference getById(ConversationId convId) {
		return idx.get(convId);
	}

	public Date randDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis()
				- (ONE_HOUR * rand.nextInt(48)));
		return cal.getTime();
	}

	public ConversationReference getDummyConv(Folder f) {
		ConversationReference cr = new ConversationReference();
		cr.setLastMessageDate(randDate());
		Set<EmailAddress> addrs = new HashSet<EmailAddress>();
		int participantsCount = rand.nextInt(2) + 1;
		for (int i = 0; i < participantsCount; i++) {
			int randAddr = rand.nextInt(900) + 100;
			addrs.add(new EmailAddress(((randAddr % 2) == 0 ? "Foo Baz" : "Bar Baz")
					+ randAddr, "foo" + randAddr + "@barbaz.org"));
		}

		cr.setParticipants(addrs);
		List<MessageId> mids = new LinkedList<MessageId>();

		int midsCount = rand.nextInt(2) + participantsCount;
		for (int i = 0; i < midsCount; i++) {
			mids.add(new MessageId(rand.nextInt(900) + 100));
		}

		cr.setMessageIds(mids);
		cr.setSourceFolderName(f.getName());
		cr.setId(allocConversationId(cr));
		cr.setTitle("Fake conversation title " + cr.getId() + ", in folder "
				+ f.getDisplayName());

		cr.setRead(false);
		cr.setHasAttachements((rand.nextInt(100) % 3) == 0);

		return cr;
	}

	private void initSearch() {
		List<ConversationReference> crl = new LinkedList<ConversationReference>();
		for (ConversationReference cr : mailData.get(new Folder("INBOX"))) {
			if ((rand.nextInt(100) % 3) == 0) {
				crl.add(cr);
			}
		}
		for (ConversationReference cr : mailData.get(new Folder("second"))) {
			if ((rand.nextInt(100) % 3) == 0) {
				crl.add(cr);
			}
		}
		Collections.shuffle(crl, rand);
		search = crl;
	}

	public List<ConversationReference> getSearch() {
		return search;
	}

	public ContactGroup[] getContactGroups() {
		ContactGroup[] groups = new ContactGroup[10];
		for (int i = 0; i < groups.length; i++) {
			groups[i] = new ContactGroup("id" + i, "Contact group "
					+ rand.nextInt(999));
		}
		return groups;
	}

	public UiContact[] getContacts(ContactGroup cg, String query) {
		UiContact[] contacts = new UiContact[100];
		for (int i = 0; i < contacts.length; i++) {
			UiContact c = new UiContact();
			c.setUid(i);
			c.setLastname("John Doe" + rand.nextInt(999));
			c.addEmail("INTERNET;X-OBM-Ref1", new UiEmail("rand.contact."
					+ rand.nextInt(999) + "@dummy.contact.biz"));
			contacts[i] = c;
		}
		return contacts;
	}

	public static int getONE_HOUR() {
		return ONE_HOUR;
	}

	private ConversationReference getFromMessage(Folder f, ClientMessage m) {
		ConversationReference cr = new ConversationReference();
		Set<EmailAddress> a = new HashSet<EmailAddress>();
		a.add(m.getSender());
		a.addAll(m.getTo());
		cr.setParticipants(a);
		List<MessageId> mids = Arrays.asList(new MessageId(rand.nextInt(899)));
		cr.setTitle(m.getSubject());
		cr.setMessageIds(mids);
		cr.setSourceFolderName(f.getName());
		cr.setId(allocConversationId(cr));
		cr.setLastMessageDate(m.getDate());
		return cr;
	}

	private ConversationId allocConversationId(ConversationReference cr) {
		convIdAllocator++;
		ConversationId ret = new ConversationId(cr.getSourceFolderName() + "/" + convIdAllocator);
		idx.put(ret, cr);
		return ret;
	}

	public ConversationId store(Folder f, ClientMessage m) {
		List<ConversationReference> l = mailData.get(f);
		ConversationReference cr = getFromMessage(f, m);
		cr.setSourceFolderName(f.getName());
		l.add(cr);
		return cr.getId();
	}

	public int randInt(int i) {
		return rand.nextInt(i);
	}

	public String allocateAttachementId() {
		String ret = "attachid_" + (attachIdAllocator++);
		attachIds.add(ret);
		return ret;
	}

	public void dropAttachement(String atId) {
		attachIds.remove(atId);
	}

}
