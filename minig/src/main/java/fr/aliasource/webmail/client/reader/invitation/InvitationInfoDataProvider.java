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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

import fr.aliasource.webmail.client.shared.Event;
import fr.aliasource.webmail.client.shared.InvitationInfo;
import fr.aliasource.webmail.client.shared.MessageId;
import fr.aliasource.webmail.client.shared.SimpleEvent;

public class InvitationInfoDataProvider {
	private String token;
	private ControllerInvitation ctrl;
	private String url;

	public InvitationInfoDataProvider(String token, ControllerInvitation ctrl) {
		this.token = token;
		this.ctrl = ctrl;
		GWT.log("token stored: " + this.token, null);
		url = GWT.getModuleBaseURL() + "getInvitationInfo";
	}

	public void requestInvitation(MessageId messageId, String folder) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL
				.encode(url));
		builder.setHeader("Content-Type", "application/x-www-form-urlencoded");

		String requestData = "token=" + URL.encodeComponent(token);
		requestData += "&messageId=" + URL.encodeComponent(String.valueOf(messageId.getMessageId()));
		requestData += "&folder=" + URL.encodeComponent(folder);
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
						Document doc = XMLParser.parse(resp);

						InvitationInfo invitationInfo = parseXml(doc);
						ctrl.invitationReceived(invitationInfo);
					} else {
						GWT.log("error: " + response.getStatusCode() + " "
								+ response.getStatusText(), null);
					}
				}

			});
		} catch (RequestException e) {
			GWT.log("Couldn't connect to server", e);
		}
	}

	private InvitationInfo parseXml(Document doc) {

		InvitationInfo invitationInfo = new InvitationInfo();

		Element root = doc.getDocumentElement();
		invitationInfo.setCalendarUrl(getNodeValueElementByTagName(root,
				"calendarUrl"));

		invitationInfo.setEvent(getMainEvent(root));
		invitationInfo.setEventOfDay(getEventOfDay(root));
		Collections.sort(invitationInfo.getEventOfDay());

		return invitationInfo;
	}

	private Event getMainEvent(Element root) {
		NodeList nlEvent = root.getElementsByTagName("event");
		Event event = null;
		if (nlEvent.getLength() > 0) {
			Element eventEle = (Element) nlEvent.item(0);

			event = new Event();
			event.setExtId(getNodeValueElementByTagName(eventEle, "extId"));
			event.setTitle(getNodeValueElementByTagName(eventEle, "title"));
			event
					.setLocation(getNodeValueElementByTagName(eventEle,
							"location"));
			event.setOwner(getNodeValueElementByTagName(eventEle, "owner"));
			event.setGoing(getNodeValueElementByTagName(eventEle, "going"));
			Date dStart = new Date(Long.parseLong(getNodeValueElementByTagName(
					eventEle, "start")));
			event.setDtStart(dStart);
			Date dEnd = new Date(Long.parseLong(getNodeValueElementByTagName(
					eventEle, "end")));
			event.setDtEnd(dEnd);
			event.setAttendees(getListNodeValueElementsByTagName(eventEle,
					"attendee"));
		}

		return event;
	}

	private List<SimpleEvent> getEventOfDay(Element root) {
		List<SimpleEvent> ret = new LinkedList<SimpleEvent>();
		NodeList subRoot = root.getElementsByTagName("eventsOfDay");
		if (subRoot.getLength() > 0) {
			Element eventsOfDayElem = (Element) subRoot.item(0);
			NodeList nlEvent = eventsOfDayElem
					.getElementsByTagName("eventOfDay");

			for (int i = 0; i < nlEvent.getLength(); i++) {
				Element eventEle = (Element) nlEvent.item(i);

				SimpleEvent event = new SimpleEvent();

				event.setTitle(eventEle.getAttribute("title"));
				Date start = new Date(Long.parseLong(eventEle
						.getAttribute("start")));
				GWT.log("** ev " + event.getTitle() + ": " + start, null);
				event.setStart(start);

				Boolean allDay = Boolean.parseBoolean(eventEle
						.getAttribute("allDay"));
				event.setAllDay(allDay.booleanValue());

				ret.add(event);
			}

		}
		return ret;
	}

	private List<String> getListNodeValueElementsByTagName(Element root,
			String tagName) {
		List<String> result = new ArrayList<String>();

		NodeList childrens = root.getElementsByTagName(tagName);
		for (int i = 0; i < childrens.getLength(); i++) {
			Node node = childrens.item(i);
			if (node.getFirstChild() != null) {
				result.add(node.getFirstChild().getNodeValue());
			}
		}

		return result;
	}

	private String getNodeValueElementByTagName(Element root, String tagName) {
		NodeList children = root.getElementsByTagName(tagName);
		if (children.getLength() > 0) {
			Node node = children.item(0);
			Node child = node.getFirstChild();
			if (child != null) {
				return child.getNodeValue();
			}
		}
		return "";
	}
}
