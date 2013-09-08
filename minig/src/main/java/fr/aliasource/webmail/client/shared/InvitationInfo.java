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

package fr.aliasource.webmail.client.shared;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author adrienp
 * 
 */
public class InvitationInfo implements Serializable {

	private static final long serialVersionUID = 7384092328293715121L;

	private String calendarUrl;
	private Event event;
	private List<SimpleEvent> eventOfDay;

	public InvitationInfo() {

	}

	public String getCalendarUrl() {
		return calendarUrl;
	}

	public void setCalendarUrl(String calendarUrl) {
		this.calendarUrl = calendarUrl;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public List<SimpleEvent> getEventOfDay() {
		return eventOfDay;
	}

	public void setEventOfDay(List<SimpleEvent> eventOfDay) {
		this.eventOfDay = eventOfDay;
	}

}
