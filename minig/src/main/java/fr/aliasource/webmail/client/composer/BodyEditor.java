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

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.DockPanel;

import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.shared.IBody;
import fr.aliasource.webmail.client.test.BeanFactory;

/**
 * The body editor part of the mail composer
 * 
 * @author tom
 * 
 */
public class BodyEditor extends DockPanel {

    private MinigRichTextArea mta;

    private ResizeHandler resizeListener;
    private ComposerToolbarSwitcher cts;

    public BodyEditor(MailComposer mc, final View ui) {
        mta = new MinigRichTextArea(ui, mc);
        mta.addStyleName("whiteBackground");

        add(mta, DockPanel.CENTER);

        cts = new ComposerToolbarSwitcher(mta);
        add(cts.getWidget(), DockPanel.NORTH);

        setStyleName("bodyEditor");
        resizeListener = createResizeListener();
    }

    public void destroy() {
    }

    void switchToPlainText(boolean withConfirm) {
        cts.switchToPlain();
    }

    public IBody getMailBody() {
        return mta.getMailBody();
    }

    public void setMailBody(IBody b) {
        mta.setMailBody(b);
    }

    public void focus() {
        mta.setFocus(true);
    }

    public void resize(int height) {
        mta.setHeight(height);
    }

    private ResizeHandler createResizeListener() {
        return new ResizeHandler() {

            @Override
            public void onResize(ResizeEvent event) {
                mta.setHeight(event.getHeight());
            }
        };
    }

    public void update(IBody mailBody) {
        mta.update(mailBody);
        if (mta.isHtmlBody()) {
            cts.switchToRich();
        }
    }

    public boolean isEmpty() {
        return mta.isEmpty();
    }

    public ResizeHandler getResizeListener() {
        return resizeListener;
    }

    // TODO
    public boolean shouldSendInPlain() {
        return cts.isPlainOnly();
    }

    public void reset() {
        update(BeanFactory.instance.body().as());
        switchToPlainText(false);
        cts.reset();
    }

}
