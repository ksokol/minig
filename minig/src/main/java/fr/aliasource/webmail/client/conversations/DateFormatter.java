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

package fr.aliasource.webmail.client.conversations;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

import fr.aliasource.webmail.client.lang.Strings;

/**
 * Provide pretty formatting of conversation dates
 * 
 * @author tom
 * 
 */
public class DateFormatter {

	private DateTimeFormat oldMail;
	private DateTimeFormat oldMailDetails;
	private DateTimeFormat todayMail;
	private long now;
	private Date nowDate;
	private Strings strings;

	private static final long ONE_DAY_MS = 1000 * 3600 * 24;
	private static final long ONE_HOUR_MS = 1000 * 3600;
	private static final long ONE_MINUTE_MS = 1000 * 60;

	/**
	 * Creates a date formatter, given the current time.
	 * 
	 * @param nowDate
	 */
	public DateFormatter(Date nowDate) {

		strings = GWT.create(Strings.class);
		oldMail = DateTimeFormat.getFormat(strings.dateOldMail());
		oldMailDetails = DateTimeFormat.getFormat(strings.dateOldMailDetails());
		todayMail = DateTimeFormat.getFormat(strings.dateTodayMail());
		this.nowDate = nowDate;
		this.now = nowDate.getTime();
	}

	@SuppressWarnings("deprecation")
	private boolean isToday(Date d) {
		long ago = now - d.getTime();
		if (ago > ONE_DAY_MS) {
			return false;
		}
		// use deprecated method to cope with GWT JRE emulation
		return d.getDay() == nowDate.getDay();
	}

	/**
	 * Creates a pretty formatting suitable for display in the conversation
	 * reader
	 * 
	 * @param d
	 * @return
	 */
	public String formatPretty(Date d) {
		long received = d.getTime();
		long ago = now - received;
		if (!isToday(d)) {
			return oldMail.format(d);
		} else if (ago < ONE_HOUR_MS) {
			int minutes = (int) (ago / ONE_MINUTE_MS);
			if (minutes > 1) {
				return formatSmall(d) + " ("
						+ strings.dateXMinutesAgo(Integer.toString(minutes))
						+ ")";
			} else {
				return formatSmall(d) + " (" + strings.dateOneMinuteAgo() + ")";
			}
		} else {
			int hour = (int) (ago / ONE_HOUR_MS);
			if (hour > 1) {
				return formatSmall(d) + " ("
						+ strings.dateXHoursAgo(Integer.toString(hour)) + ")";
			} else {
				return formatSmall(d) + " (" + strings.dateOneHourAgo() + ")";
			}
		}
	}

	/**
	 * Formats a date with a format suitable for listing conversations
	 * 
	 * @param d
	 * @return
	 */
	public String formatSmall(Date d) {
		if (!isToday(d)) {
			return oldMail.format(d);
		} else {
			return todayMail.format(d);
		}
	}

	/**
	 * Formats a date with a detailed format
	 * 
	 * @param d
	 * @return
	 */
	public String formatDetails(Date d) {
		return oldMailDetails.format(d);
	}

}
