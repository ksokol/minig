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

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Command;

import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.IFolderList;
import fr.aliasource.webmail.client.test.Ajax;
import fr.aliasource.webmail.client.test.AjaxFactory;
import fr.aliasource.webmail.client.test.AjaxCallback;

public class ListSubFoldersCommand implements Command {

    public ListSubFoldersCommand() {
    }

    public void execute() {
        Ajax<IFolderList> builder = AjaxFactory.subscribedFolder(true);

        try {
            builder.send(new AjaxCallback<IFolderList>() {

                @Override
                public void onSuccess(IFolderList object) {
                    WebmailController.get().getSelector().setFolders(object.getFolderList());
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    // TODO
                    GWT.log("/folderManager failure (" + exception.getMessage() + ")", null);
                }
            });
        } catch (RequestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
