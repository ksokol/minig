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

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.shared.IAttachmentMetadata;
import fr.aliasource.webmail.client.test.AjaxFactory;

/**
 * The widget used to display a clickable attachment & its size.
 * 
 * @author tom
 * 
 */
public class AttachmentDisplay extends FlexTable {

	public AttachmentDisplay(final IAttachmentMetadata meta) {
		addMimeImage(meta);

		HTML label = new HTML("<b>" + meta.getFileName() + "</b>");
		setWidget(0, 1, label);

		Label size = new Label(prettySize(meta.getSize()));
		setWidget(1, 1, size);

		Anchor download = new Anchor(I18N.strings.downloadAttachment(), false,
				AjaxFactory.attachmentDownloadLink(meta.getId()));
		download.getElement().setAttribute("target", "_blank");
		setWidget(1, 2, download);

		getFlexCellFormatter().setColSpan(0, 1, 3);
		getFlexCellFormatter().setRowSpan(0, 0, 2);
	}

	private void addMimeImage(IAttachmentMetadata meta) {
		Image mime = new Image("minig/images/mime.gif");
		setWidget(0, 0, mime);
	}

	private String prettySize(long size) {
		if (size < 1024) {
			return size + " " + I18N.strings.sizeByte();
		}
		size = size / 1024;
		if (size < 1024) {
			return size + " " + I18N.strings.sizeKilobyte();
		}
		size = size / 1024;
		return size + " " + I18N.strings.sizeMegabyte();
	}

}
