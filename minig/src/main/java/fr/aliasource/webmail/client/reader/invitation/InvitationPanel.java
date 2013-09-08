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

import java.util.Date;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.lang.Strings;
import fr.aliasource.webmail.client.shared.ClientMessage;
import fr.aliasource.webmail.client.shared.ConversationId;
import fr.aliasource.webmail.client.shared.InvitationInfo;
import fr.aliasource.webmail.client.shared.SimpleEvent;

public class InvitationPanel extends HorizontalPanel {

	private final static String INVITATION_ACCEPT = "yes";
	private final static String INVITATION_REFUSE = "no";
	private final static String INVITATION_MAYBE = "maybe";

	private ClientMessage cm;
	private InvitationInfo invitationInfo;

	private FlexTable infoEventPanel;
	private VerticalPanel agendaPanel;
	private Strings strings;
	private ControllerInvitation ctrlInvitation;

	private Anchor accept;
	private Anchor maybe;
	private Anchor refuse;

	public InvitationPanel(View ui, ClientMessage cm) {
		strings = I18N.strings;
		this.cm = cm;

		ctrlInvitation = new ControllerInvitation(ui, this);
		this.setWidth("100%");
		this.setVisible(false);
		this.addStyleName("invitationPanel");

	}

	public void init() {
		ConversationId convId = cm.getConvId();
		if (convId.hasFolder()) {
			ctrlInvitation.requestGetInvitationData(cm.getUid(), convId.getSourceFolder()); 
		}
	}

	public void update(InvitationInfo invitationInfo) {
		if (invitationInfo != null) {
			this.invitationInfo = invitationInfo;
			VerticalPanel vpInfoEvent = fillInfoEventPanel();
			agendaPanel = fillAgendaPanel();
			this.add(vpInfoEvent);
			this.add(agendaPanel);
			updateGoingLink(invitationInfo.getEvent().getGoing());
			this.setVisible(true);
		}
	}

	public void updateGoingLink(String going) {
		HTML goingLabel = new HTML(strings.invitationGoing() + "&nbsp;");
		HorizontalPanel hsp = new HorizontalPanel();
		hsp.addStyleName("going");
		hsp.add(goingLabel);

		if (INVITATION_ACCEPT.equals(going)) {
			HTML label = new HTML(accept.getText());
			hsp.add(label);
		} else {
			hsp.add(accept);
		}
		hsp.add(new HTML("&nbsp;-&nbsp;"));
		if (INVITATION_MAYBE.equals(going)) {
			HTML label = new HTML(maybe.getText());
			hsp.add(label);
		} else {
			hsp.add(maybe);
		}
		hsp.add(new HTML("&nbsp;-&nbsp;"));
		if (INVITATION_REFUSE.equals(going)) {
			HTML label = new HTML(refuse.getText());
			hsp.add(label);
		} else {
			hsp.add(refuse);
		}

		infoEventPanel.setWidget(5, 1, hsp);
	}

	private VerticalPanel fillInfoEventPanel() {
		VerticalPanel vp = new VerticalPanel();
		vp.setStyleName("detailEvent noWrap");
		Label lb = new Label(strings.newEvent());
		vp.add(lb);

		this.infoEventPanel = new FlexTable();
		infoEventPanel.addStyleName("leftPanel");
		infoEventPanel.setText(0, 0, strings.invitationTitle() + ":");
		infoEventPanel.setText(0, 1, invitationInfo.getEvent().getTitle());

		infoEventPanel.setText(1, 0, strings.invitationOwner() + ":");
		infoEventPanel.setText(1, 1, invitationInfo.getEvent().getOwner());

		infoEventPanel.setText(2, 0, strings.invitationWhen() + ":");

		if (invitationInfo.getEvent().getDtStart() != null) {
			DateTimeFormat dtfStart = DateTimeFormat.getShortDateTimeFormat();
			String sStart = dtfStart.format(invitationInfo.getEvent()
					.getDtStart());

			DateTimeFormat dtfEnd = DateTimeFormat.getShortTimeFormat();
			String sEnd = dtfEnd.format(invitationInfo.getEvent().getDtEnd());

			infoEventPanel.setText(2, 1, sStart + " - " + sEnd);
		}

		infoEventPanel.setText(3, 0, strings.invitationWhere() + ":");
		infoEventPanel.setText(3, 1, invitationInfo.getEvent().getLocation());

		String who = getWho();
		if (!"".endsWith(who)) {
			infoEventPanel.setText(4, 0, strings.invitationWho() + ":");
			infoEventPanel.setText(4, 1, who);
		}

		Element moreDetail = DOM.createElement("a");
		moreDetail.setAttribute("href", invitationInfo.getCalendarUrl());
		moreDetail.setInnerText(strings.moreActions());
		HTML linkMoreDetail = new HTML();
		linkMoreDetail.getElement().appendChild(moreDetail);

		infoEventPanel.setWidget(5, 1, linkMoreDetail);

		accept = getGoingLink(strings.invitationYes(), "invitationYes",
				INVITATION_ACCEPT);
		maybe = getGoingLink(strings.invitationMaybe(), "invitationMaybe",
				INVITATION_MAYBE);
		refuse = getGoingLink(strings.invitationNo(), "invitationNo",
				INVITATION_REFUSE);
		updateGoingLink("");

		for (int i = 0; i < infoEventPanel.getRowCount(); ++i) {
			infoEventPanel.getCellFormatter().setStyleName(i, 0, "keys");
		}
		infoEventPanel.getRowFormatter().addStyleName(0, "titleEvent");
		infoEventPanel.getRowFormatter().addStyleName(1, "ownerEvent");
		infoEventPanel.getRowFormatter().addStyleName(2, "startEvent");

		vp.add(infoEventPanel);
		return vp;
	}

	private Anchor getGoingLink(String title, String targetHistoryToken,
			final String going) {
		Anchor link = new Anchor(title);

		ClickHandler handler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				ctrlInvitation.requestGoingEvent(invitationInfo.getEvent()
						.getExtId(), going);
			}

		};
		link.addClickHandler(handler);

		return link;
	}

	private String getWho() {
		StringBuffer who = new StringBuffer();
		int nbAttendee = 0;
		List<String> listAttende = invitationInfo.getEvent().getAttendees();
		for (int i = 0; i < listAttende.size() && nbAttendee < 2; ++i) {
			if (!listAttende.get(i)
					.equals(invitationInfo.getEvent().getOwner())) {
				who.append(listAttende.get(i));
				if (i != (listAttende.size() - 1)) {
					who.append(", ");
				}
				nbAttendee++;
			}
		}
		if (invitationInfo.getEvent().getAttendees().size() > 2) {
			who.append((" ..."));
		}
		return who.toString();
	}

	private VerticalPanel fillAgendaPanel() {
		agendaPanel = new VerticalPanel();
		agendaPanel.setStyleName("agendaPanel");
		agendaPanel.setBorderWidth(1);
		if (invitationInfo.getEvent().getDtStart() != null) {
			Date when = invitationInfo.getEvent().getDtStart();
			DateTimeFormat dtf = DateTimeFormat.getMediumDateFormat();
			HTML day = new HTML(strings.invitationDay(dtf.format(when)));
			day.setStyleName("invitationDay");
			agendaPanel.add(day);
		}

		for (SimpleEvent sEvent : invitationInfo.getEventOfDay()) {
			String sStart = "";
			if (sEvent.isAllDay()) {
				sStart = strings.eventAllDay();
			} else if (sEvent.getStart() != null) {
				DateTimeFormat dtfStart = DateTimeFormat.getShortTimeFormat();
				sStart = dtfStart.format(sEvent.getStart());
			}
			InvitationAgendaLine line = new InvitationAgendaLine(sStart, sEvent
					.getTitle());
			agendaPanel.add(line);

		}

		Anchor a = new Anchor(strings.invitationGoToCalendar());
		a.setHref(invitationInfo.getCalendarUrl());
		a.setTarget("_blank");
		agendaPanel.add(a);

		return agendaPanel;
	}

}
