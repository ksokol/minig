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

package fr.aliasource.webmail.client.reader;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import fr.aliasource.webmail.client.shared.IClientMessage;
import fr.aliasource.webmail.client.shared.IEmailAddress;

public class RecipientsStyleHandler {

    private Map<IEmailAddress, String> styles;

    public RecipientsStyleHandler(IClientMessage cc) {
        styles = new HashMap<IEmailAddress, String>();

        Set<IEmailAddress> sa = new LinkedHashSet<IEmailAddress>();
        IClientMessage cm = cc;
        // for (ClientMessage cm : cc.getMessages()) {
        sa.add(cm.getSender());
        for (IEmailAddress a : cm.getTo()) {
            sa.add(a);
        }
        for (IEmailAddress a : cm.getCc()) {
            sa.add(a);
        }
        for (IEmailAddress a : cm.getBcc()) {
            sa.add(a);
        }
        // }
        String style = "recipientLabel";
        int i = 1;
        for (IEmailAddress a : sa) {
            styles.put(a, style + i);
            i++;
        }
    }

    public String getStyle(IEmailAddress sender) {
        return styles.get(sender);
    }

}
