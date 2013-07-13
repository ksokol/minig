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

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;

public class SubjectField extends HorizontalPanel {

	private TextBox subjectField;

	public SubjectField(View ui) {
		super();
		Label subject = new Label(I18N.strings.subject() + ": ");
		add(subject);
		subjectField = new TextBox();
		add(subjectField);
		setCellWidth(subjectField, "100%");
		setStyleName("enveloppeField");
		setCellVerticalAlignment(subject, VerticalPanel.ALIGN_MIDDLE);
	}

	public void clearText() {
		subjectField.setText("");
	}

	public void setText(String subject) {
		subjectField.setText(subject);
	}

	public String getText() {
		return subjectField.getText();
	}

}
