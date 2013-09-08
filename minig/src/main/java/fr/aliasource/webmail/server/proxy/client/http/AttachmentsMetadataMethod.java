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

package fr.aliasource.webmail.server.proxy.client.http;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import fr.aliasource.webmail.client.shared.AttachmentList;
import fr.aliasource.webmail.client.shared.AttachmentMetadata;
import fr.aliasource.webmail.client.shared.Folder;

public class AttachmentsMetadataMethod extends AbstractClientMethod {

	protected String token;

	AttachmentsMetadataMethod(HttpClient hc, String token, String backendUrl) {
		super(hc, backendUrl, "/attachmentsMetadata.do");
		this.token = token;
	}

	public AttachmentMetadata[] getMetadata(String[] attachementId) {
		StringBuilder ids = new StringBuilder(attachementId.length * 20);
		for (int i = 0; i < attachementId.length; i++) {
			if (i > 0) {
				ids.append(',');
			}
			ids.append(attachementId[i]);
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("ids", ids.toString());

		logger.info("loading ids: " + ids.toString());

		Document doc = execute(params);

		AttachmentMetadata[] ret = new AttachmentMetadata[attachementId.length];
		NodeList n = doc.getElementsByTagName("att");

		for (int i = 0; i < attachementId.length; i++) {
			Element e = (Element) n.item(i);
			ret[i] = new AttachmentMetadata(attachementId[i]);
			ret[i].setFileName(e.getAttribute("filename"));
			ret[i].setSize(Long.parseLong(e.getAttribute("size")));
			ret[i].setMime(e.getAttribute("mime"));
			ret[i].setWithPreview("true".equals(e.getAttribute("preview")));
			ret[i].setPreviewMime("preview-mime");
		}
		return ret;
	}

	public AttachmentList listAttachments(Folder folder, int page,
			int pageLength) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("token", token);
		params.put("folder", folder.getName());
		params.put("page", "" + page);
		params.put("pageLength", "" + pageLength);

		Document doc = execute(params);
		AttachmentList ret = null;
		if (doc != null) {
			if (logger.isDebugEnabled()) {
				DOMUtils.logDom(doc);
			}

			Element root = doc.getDocumentElement();
			NodeList crs = root.getElementsByTagName("cr");
			List<AttachmentMetadata> cPage = new ArrayList<AttachmentMetadata>(
					crs.getLength());
			Calendar cal = Calendar.getInstance();
			for (int i = 0; i < crs.getLength(); i++) {
				Element cr = (Element) crs.item(i);
				cPage.add(parseAttachment(cal, cr));
			}
			ret = new AttachmentList(Integer.parseInt(root
					.getAttribute("fullLength")), (AttachmentMetadata[]) cPage
					.toArray());
		}
		return ret;
	}

	private AttachmentMetadata parseAttachment(Calendar cal, Element cr) {
		/*
		 * String title = DOMUtils.getElementText(cr, "title");
		 * 
		 * Element ps = DOMUtils.getUniqueElement(cr, "participants");
		 * Set<Address> participants = new HashSet<Address>(); String[][]
		 * psArray = DOMUtils.getAttributes(ps, "p", new String[] { "addr",
		 * "displayName" }); for (int j = 0; j < psArray.length; j++) {
		 * participants.add(new Address(psArray[j][1], psArray[j][0])); }
		 * 
		 * Element mids = DOMUtils.getUniqueElement(cr, "mids"); String[][]
		 * mArray = DOMUtils.getAttributes(mids, "m", new String[] { "id" });
		 * List<String> messageIds = new ArrayList<String>(mArray.length); for
		 * (int j = 0; j < mArray.length; j++) { messageIds.add(mArray[j][0]); }
		 * 
		 * ConversationReference c = new ConversationReference();
		 * c.setParticipants(participants); c.setMessageIds(messageIds);
		 * c.setTitle(title); c.setRead("true".equals(cr.getAttribute("read")));
		 * c.setHasAttachements("true".equals(cr.getAttribute("attach")));
		 * cal.setTimeInMillis
		 * (Long.parseLong(cr.getAttribute("lastMessageDate")));
		 * c.setLastMessageDate(cal.getTime());
		 * c.setSourceFolderName(cr.getAttribute("folder"));
		 * c.setId(cr.getAttribute("id"));
		 * 
		 * parseMetadata(c, cr); return c;
		 */
		return null;
	}

}
