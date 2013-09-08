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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.SimplePanel;

import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.shared.IBody;

abstract class MinigTextArea extends SimplePanel {

    final static protected String HTML_SIGNATURE_SEPARATOR = "<br>--&nbsp;<br>";

    protected View ui;
    protected MailComposer composer;
    protected String signature;

    public MinigTextArea(View ui, MailComposer mc) {
        this.ui = ui;
        this.composer = mc;
    }

    public abstract void addKeyboardListener(KeyPressHandler addKeyboardListener);

    public abstract void setFocus(boolean b);

    public abstract RichTextToolbar getRichTextToolbar();

    public abstract IBody getMailBody();

    public abstract void setMailBody(IBody body);

    public abstract void setHeight(int height);

    final void update(IBody mailBody) {
        if (es(mailBody.getPlain())) {
            mailBody.setPlain("");
        }
        if (es(mailBody.getHtml())) {
            mailBody.setHtml("");
        }
        logBody(mailBody);

        setMailBody(mailBody);
    }

    /**
     * Another empty string method
     * 
     * @param s
     * @return
     */
    private final boolean es(String s) {
        if (s == null) {
            return true;
        }
        if (s.length() == 0) {
            return true;
        }
        if (s.trim().length() == 0) {
            return true;
        }
        return false;
    }

    private final void logBody(IBody mailBody) {
        if (!GWT.isScript()) {
            StringBuilder sb = new StringBuilder();
            sb.append("update(mailBody):\n");
            sb.append("plain:\n");
            sb.append(mailBody.getPlain());
            sb.append("html:\n");
            sb.append(mailBody.getHtml());
            GWT.log(sb.toString(), null);
        }
    }

    public abstract boolean isEmpty();

}
