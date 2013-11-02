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

package fr.aliasource.webmail.client.composer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.shared.AttachmentId;
import fr.aliasource.webmail.client.shared.IBody;
import fr.aliasource.webmail.client.shared.IClientMessage;
import fr.aliasource.webmail.client.shared.IEmailAddress;
import fr.aliasource.webmail.client.shared.IFolder;
import fr.aliasource.webmail.client.shared.ReplyInfo;
import fr.aliasource.webmail.client.test.BeanFactory;

public class ReplyManager {

    private IEmailAddress me;

    public ReplyManager() {
        Element element = DOM.getElementById("username");
        String innerText = element.getInnerText();

        this.me = BeanFactory.instance.emailAddress().as();
        me.setEmail(innerText);
    }

    public IClientMessage prepareReply(IClientMessage message, boolean all) {
        IClientMessage ret = BeanFactory.instance.clientMessage().as();
        if (!all) {
            ret.setTo(Arrays.asList(message.getSender()));
        } else {
            Set<IEmailAddress> ads = new HashSet<IEmailAddress>();
            // reply to all recipients except me
            addRecips(ads, message.getTo());
            addRecips(ads, message.getCc());
            addRecips(ads, message.getBcc());
            ads.add(message.getSender());
            ret.setTo(new ArrayList<IEmailAddress>(ads));
        }
        ret.setSubject(replySubject(message.getSubject()));
        ret.setAttachments(new ArrayList<AttachmentId>());
        ret.setBody(quoteForReply(message));
        return ret;
    }

    private void addRecips(Set<IEmailAddress> ads, List<IEmailAddress> rcpt) {
        for (int i = 0; i < rcpt.size(); i++) {
            IEmailAddress ad = rcpt.get(i);
            if (!equals(ad, me)) {
                ads.add(ad);
            }
        }
    }

    private boolean equals(IEmailAddress a1, IEmailAddress a2) {
        return a1.getEmail().equals(a2.getEmail());
    }

    private String replySubject(String subject) {
        if (subject == null) {
            return "RE: ";
        }
        if (subject.toLowerCase().startsWith("re:")) {
            return subject;
        } else {
            return "RE: " + subject;
        }
    }

    private IBody quoteForReply(IClientMessage message) {
        String endLine = "\n";
        String plainB = message.getBody().getPlain();
        StringBuffer quote = new StringBuffer(2 * plainB.length());
        // insert two empty lines to write the reply
        quote.append(endLine).append(endLine);
        quote.append(I18N.strings.quoteSender(message.getSender().getDisplayName()) + endLine);
        String[] lines = plainB.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (!lines[i].trim().isEmpty()) {
                quote.append("> ").append(lines[i].trim()).append(endLine);
            }
        }
        quote.append(endLine);

        String quoted = quote.toString();
        IBody ret = BeanFactory.instance.body().as();
        ret.setHtml(new PlainToHTMLConverter().convert(quoted));

        return ret;
    }

    public void prepareForward(final IClientMessage message, final QuickReply quickReply) {
        message.setSender(me);
        message.setSubject(forwardSubject(message.getSubject()));
        message.setTo(null);
        message.setCc(null);
        message.setBcc(null);
        message.setAttachments(null);

        // set html to null in order to trigger plain message part in MinigRichTextArea
        message.getBody().setHtml(null);

        quickReply.loadDraft(message);
        quickReply.focusComposer();
    }

    private String forwardSubject(String subject) {
        if (subject == null) {
            return "[Fwd: ]";
        }
        if (subject.toLowerCase().startsWith("[fwd:")) {
            return subject;
        } else {
            return "[Fwd: " + subject + "]";
        }
    }

    public ReplyInfo getInfo(IClientMessage message) {
        IFolder folder = BeanFactory.instance.folder().as();
        folder.setName(message.getFolderName());

        ReplyInfo ri = new ReplyInfo(folder, message.getId(), message.getId());
        return ri;
    }

}
