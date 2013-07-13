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
import java.util.Comparator;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;

import fr.aliasource.webmail.client.ctrl.AjaxCall;
import fr.aliasource.webmail.client.shared.MailSuggestion;

/**
 * Provides emails autocompletion source
 * 
 * @author tom
 * 
 */
public class AddressBookSuggestOracle extends SuggestOracle {

	public AddressBookSuggestOracle() {
	}

	public void requestSuggestions(final Request request,
			final Callback callback) {

		AsyncCallback<MailSuggestion[]> ac = new AsyncCallback<MailSuggestion[]>() {

			public void onFailure(Throwable caught) {
				ArrayList<MailSuggestion> l = new ArrayList<MailSuggestion>();
				Response r = new Response(l);
				callback.onSuggestionsReady(request, r);
			}

			public void onSuccess(MailSuggestion[] s) {
				Arrays.sort(s, new Comparator<MailSuggestion>() {

					@Override
					public int compare(MailSuggestion o1, MailSuggestion o2) {
						return o1.getDisplayString().compareToIgnoreCase(
								o2.getDisplayString());
					}
				});
				ArrayList<MailSuggestion> l = new ArrayList<MailSuggestion>(
						s.length + 1);
				l.addAll(Arrays.asList(s));
				Response r = new Response(l);
				callback.onSuggestionsReady(request, r);
			}

		};
		AjaxCall.lemails.listEmails(request.getQuery(), ac);
	}

}
