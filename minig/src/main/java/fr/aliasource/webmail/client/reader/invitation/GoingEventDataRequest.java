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

package fr.aliasource.webmail.client.reader.invitation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;

public class GoingEventDataRequest {
	private String token;
	private ControllerInvitation ctrl;
	private String url;

	public GoingEventDataRequest(String token, ControllerInvitation ctrl) {
		this.token = token;
		this.ctrl = ctrl;
		GWT.log("token stored in CalDataProvider: " + this.token, null);
		url = GWT.getModuleBaseURL() + "goingEvent";
	}

	public void requestGoing(String extId, final String going) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL
				.encode(url));
		builder.setHeader("Content-Type", "application/x-www-form-urlencoded");

		String requestData = "token=" + URL.encodeComponent(token);
		requestData += "&extId=" + URL.encodeComponent(extId);
		requestData += "&going=" + URL.encodeComponent(going);
		try {
			builder.sendRequest(requestData, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					GWT.log("srv error", exception);
				}

				public void onResponseReceived(Request request,
						Response response) {
					if (200 == response.getStatusCode()) {
						// Process the response in response.getText()
						String resp = response.getText();
						GWT.log("text:\n" + resp, null);
						ctrl.goingReceived(going);
					} else {
						GWT.log("error: " + response.getStatusCode() + " "
								+ response.getStatusText(), null);
					}
				}
			});
		} catch (RequestException e) {
			// Couldn't connect to server
		}
	}
}
