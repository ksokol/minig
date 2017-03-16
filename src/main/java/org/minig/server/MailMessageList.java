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

package org.minig.server;

import java.util.ArrayList;
import java.util.List;

public class MailMessageList {

    private long fullLength;
    private int page;
    private List<MailMessage> mailList;

    public MailMessageList() {
        this.mailList = new ArrayList<>();
        this.page = 1;
        this.fullLength = 0;

    }

    public MailMessageList(List<MailMessage> mailList, int page, long fullLength) {
        this.mailList = mailList;
        this.page = page;
        this.fullLength = fullLength;
    }

    public long getFullLength() {
        return fullLength;
    }

    public void setFullLength(long fullLength) {
        this.fullLength = fullLength;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<MailMessage> getMailList() {
        return mailList;
    }

    public void setMailList(List<MailMessage> mailList) {
        this.mailList = mailList;
    }

}
