package fr.aliasource.webmail.client.settings;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.datepicker.client.DateBox;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.shared.VacationInfo;

public class VacationStatusBox extends FlexTable {

	private RadioButton doNotSend;
	private RadioButton send;
	private CheckBox onlyInRange;
	private DateBox startTime;
	private DateBox endTime;
	private List<ISettingChangeListener> listeners;

	public VacationStatusBox(List<ISettingChangeListener> listeners) {
		this.listeners = listeners;
		doNotSend = new RadioButton("enableVacation", I18N.strings
				.vacationDisable());
		doNotSend.setValue(true);
		doNotSend.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				onlyInRange.setEnabled(send.getValue());
				onlyInRange.setValue(send.getValue());
				startTime.setEnabled(onlyInRange.getValue());
				endTime.setEnabled(onlyInRange.getValue());
				notifyChange();
			}
		});
		send = new RadioButton("enableVacation", I18N.strings.vacationEnable());
		send.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				onlyInRange.setEnabled(send.getValue());
				startTime.setEnabled(onlyInRange.getValue());
				endTime.setEnabled(onlyInRange.getValue());
				notifyChange();
			}
		});

		setWidget(0, 0, doNotSend);
		setWidget(1, 0, send);
		getFlexCellFormatter().setColSpan(0, 0, 4);
		getFlexCellFormatter().setColSpan(1, 0, 4);

		onlyInRange = new CheckBox(I18N.strings.vacationRange());
		onlyInRange.setEnabled(false);
		onlyInRange.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				startTime.setEnabled(onlyInRange.getValue());
				endTime.setEnabled(onlyInRange.getValue());
				notifyChange();
			}
		});
		setWidget(2, 1, onlyInRange);
		getFlexCellFormatter().setWidth(2, 0, "30px");
		getFlexCellFormatter().setColSpan(2, 1, 3);

		setWidget(3, 2, new Label(I18N.strings.vacationStart()));
		setWidget(4, 2, new Label(I18N.strings.vacationEnd()));
		getFlexCellFormatter().setWidth(3, 1, "30px");
		getFlexCellFormatter().setWidth(4, 1, "30px");
		startTime = new DateBox();
		startTime.setEnabled(false);
		setWidget(3, 3, startTime);

		endTime = new DateBox();
		endTime.setEnabled(false);
		setWidget(4, 3, endTime);
	}

	public boolean isVacationEnabled() {
		return send.getValue();
	}

	private void notifyChange() {
		for (ISettingChangeListener scl : listeners) {
			scl.notifySettingChanged();
		}
	}

	public Date getVacationStart() {
		if (!onlyInRange.getValue()) {
			return null;
		}
		return startTime.getValue();
	}

	public Date getVacationEnd() {
		if (!onlyInRange.getValue()) {
			return null;
		}
		return endTime.getValue();
	}

	public void setState(VacationInfo vi) {
		GWT.log("vacation: " + vi.getStart() + " end: " + vi.getEnd()
				+ " enab: " + vi.isEnabled(), null);
		startTime.setValue(vi.getStart());
		endTime.setValue(vi.getEnd());
		onlyInRange.setValue(vi.getStart() != null && vi.getEnd() != null);
		onlyInRange.setEnabled(vi.getStart() != null && vi.getEnd() != null);
		send.setValue(vi.isEnabled());
	}

	public VacationInfo getState() {
		VacationInfo ret = new VacationInfo();
		ret.setEnabled(send.getValue());
		ret.setStart(getVacationStart());
		ret.setEnd(getVacationEnd());
		return ret;
	}

}
