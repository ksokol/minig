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

package fr.aliasource.webmail.client;

import java.util.LinkedList;
import java.util.List;

import fr.aliasource.webmail.client.shared.IFolder;
import fr.aliasource.webmail.client.test.BeanFactory;

/**
 * Folder selection controller
 * 
 * @author tom
 * 
 */
public class FolderSelector {

    private List<IFolderSelectionListener> listeners;
    private IFolder current;
    private List<IFolder> subscribed;

    public FolderSelector() {
        listeners = new LinkedList<IFolderSelectionListener>();

        // TODO
        IFolder folder = BeanFactory.instance.folder().as();
        folder.setName("INBOX");
        folder.setId("INBOX");

        current = folder; // new Folder("INBOX", I18N.strings.inbox());

    }

    public void addListener(IFolderSelectionListener fsl) {
        listeners.add(fsl);
        if (subscribed != null) {
            fsl.foldersChanged(subscribed);
        }
    }

    public void removeListener(IFolderSelectionListener fsl) {
        listeners.remove(fsl);
    }

    private void notifyListeners(IFolder f) {
        for (IFolderSelectionListener l : listeners) {
            l.folderSelected(f);
        }
    }

    public void setFolders(List<IFolder> folders) {
        this.subscribed = folders;
        for (IFolderSelectionListener l : listeners) {
            l.foldersChanged(folders);
        }
    }

    public void select(IFolder f) {
        current = f;
        notifyListeners(f);
    }

    public IFolder getCurrent() {
        return current;
    }
}
