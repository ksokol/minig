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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Label & progress bar displayed while minig is loading
 * 
 * @author tom
 * 
 */
public class SplashScreen extends VerticalPanel {

	private HTML label;
	private ProgressBar pb;

	public SplashScreen() {
		label = new HTML("<b>" + I18N.strings.loadingMiniG() + "</b>");
		pb = new ProgressBar();

		add(label);
		add(pb);
		pb.setPercent(0);
		setSpacing(10);
	}

	/**
	 * Sets the filling of the progress bar
	 * 
	 * @param percent
	 *            0 means no progress, 100 means complete
	 */
	public void setPercent(int percent) {
		pb.setPercent(percent);
	}

	public void setText(String t) {
		label.setHTML("<b>" + t + "</b>");
	}
}
