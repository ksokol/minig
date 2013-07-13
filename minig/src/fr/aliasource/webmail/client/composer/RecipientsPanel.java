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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;
import fr.aliasource.webmail.client.shared.EmailAddress;

/**
 * The address selection widget used in mail composer
 * 
 * @author tom
 * 
 */
public class RecipientsPanel extends HorizontalPanel {

	private SuggestBox mailField;
	private FlowPanel recipListPanel;
	private int token;
	private int recipCount;
	private HashMap<String, String> recipients;
	private long lastTs;

	private static String PATTERN_EMAIL = "[a-zA-Z_0-9\\-.]+@[a-zA-Z_0-9\\-.]+\\.[a-z]+";

	public RecipientsPanel(View ui, String label) {
		super();
		token = -1;
		Label recipientType = new Label(label);
		add(recipientType);
		SuggestOracle oracle = new AddressBookSuggestOracle();
		mailField = new SuggestBox(oracle);
		createMailFieldEventHandler();
		createMailFieldKeyboardListener();
		createMailFieldClickListener();
		createMailFieldFocusListener();

		FlowPanel wrap = new FlowPanel();
		wrap.setStyleName("wrap");
		recipListPanel = new FlowPanel();
		wrap.add(recipListPanel);
		wrap.add(mailField);

		add(wrap);
		setStyleName("enveloppeField");
		setCellWidth(wrap, "100%");
		setCellVerticalAlignment(recipientType, VerticalPanel.ALIGN_MIDDLE);

		recipients = new HashMap<String, String>();

		// Add click listener
		sinkEvents(Event.ONCLICK);
	}

	public void onBrowserEvent(Event event) {
		switch (DOM.eventGetType(event)) {
		case Event.ONCLICK:
			mailField.setFocus(true);
			break;
		}
	}

	private void addRecipient(Suggestion s) {
		if (!recipients.containsKey(s.getReplacementString())) {
			final String email = s.getReplacementString();
			String name = s.getDisplayString();
			createRecipient(email, name);
			if (token > -1) {
				recipListPanel.getWidget(token).removeStyleName(
						"highlightRecipient");
			}
		}
		mailField.setFocus(true);
		mailField.setText("");
	}

	private void addRecipient(EmailAddress a) {
		final String email = a.getEmail();
		String name = a.getDisplay();
		createRecipient(email, name);
	}

	private void addRecipient(String email) {
		createRecipient(email, email);
	}

	private void createRecipient(final String email, String name) {
		Label l = new Label(name);
		final HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		hp.setTitle(email);
		hp.add(l);
		hp.setStyleName("recipient");
		Image deleteRecip = new Image("minig/images/x.gif");
		deleteRecip.setStyleName("deleteRecip");
		deleteRecip.setTitle(I18N.strings.remove());
		deleteRecip.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent ev) {
				hp.removeFromParent();
				recipients.remove(email);
				if (token > -1) {
					recipListPanel.getWidget(token).removeStyleName(
							"highlightRecipient");
					token = -1;
				}
				mailField.setFocus(true);
			}
		});
		hp.add(deleteRecip);
		recipListPanel.add(hp);
		recipients.put(email, name);
	}

	private void createMailFieldFocusListener() {
		mailField.getTextBox().addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				String email = mailField.getText();
				if (!email.isEmpty()) {
					addEmail(email);
				}
			}
		});
	}

	private void createMailFieldEventHandler() {
		mailField.addSelectionHandler(new SelectionHandler<Suggestion>() {
			@Override
			public void onSelection(SelectionEvent<Suggestion> event) {
				addRecipient(event.getSelectedItem());
			}
		});
	}

	private void createMailFieldKeyboardListener() {
		mailField.addKeyDownHandler(new KeyDownHandler() {
			public void onKeyDown(KeyDownEvent kde) {
				long ts = new Date().getTime();
				long diff = ts - lastTs;
				lastTs = ts;
				if (diff < 150) {
					// crap to prevent double delete on backspace...
					return;
				}

				if (mailField.getText().isEmpty()) {
					recipCount = recipListPanel.getWidgetCount();
					switch (kde.getNativeKeyCode()) {
					case KeyCodes.KEY_BACKSPACE:
						if (recipCount > 0) {
							String email = "";
							if (token == -1) {
								email = recipListPanel
										.getWidget(recipCount - 1).getTitle();
								recipListPanel.getWidget(recipCount - 1)
										.removeFromParent();
							} else {
								email = recipListPanel.getWidget(token)
										.getTitle();
								recipListPanel.getWidget(token)
										.removeFromParent();
							}
							recipients.remove(email);
							mailField.setFocus(true);
							token = -1;
						}
						break;
					case KeyCodes.KEY_LEFT:
						if (recipCount > 0) {
							if (token == -1) {
								token = recipCount - 1;
							} else if (token > 0) {
								recipListPanel.getWidget(token)
										.removeStyleName("highlightRecipient");
								token--;
							} else {
								token = 0;
							}
							recipListPanel.getWidget(token).addStyleName(
									"highlightRecipient");
						}
						break;
					case KeyCodes.KEY_RIGHT:
						if (recipCount > 0) {
							if (token < recipCount - 1 && token > -1) {
								recipListPanel.getWidget(token)
										.removeStyleName("highlightRecipient");
								token++;
							} else {
								token = recipCount - 1;
							}
							recipListPanel.getWidget(token).addStyleName(
									"highlightRecipient");
						}
						break;
					case KeyCodes.KEY_DELETE:
						if (token >= 0) {
							String email = recipListPanel.getWidget(token)
									.getTitle();
							recipListPanel.remove(token);
							recipients.remove(email);
							mailField.setFocus(true);
							recipCount = recipListPanel.getWidgetCount();
							if (recipCount > 0) {
								if (token >= recipCount - 1) {
									token = recipCount - 1;
								} else if (token <= 0) {
									token = 0;
								}
								recipListPanel.getWidget(token).addStyleName(
										"highlightRecipient");
							} else {
								token = -1;
							}
						}
						break;
					case KeyCodes.KEY_ESCAPE:
						if (token >= 0) {
							recipListPanel.getWidget(token).removeStyleName(
									"highlightRecipient");
							mailField.setFocus(true);
						}
						token = -1;
						break;
					default:
						if (token > -1) {
							recipListPanel.getWidget(token).removeStyleName(
									"highlightRecipient");
							token = -1;
						}
						break;
					}
				} else {
					String email = mailField.getText();
					switch (kde.getNativeKeyCode()) {
					case KeyCodes.KEY_ENTER:
						addEmail(email);
						break;
					case KeyCodes.KEY_ESCAPE:
						mailField.setText("");
						break;
					}
				}
			}
		});
	}

	private void createMailFieldClickListener() {
		mailField.getTextBox().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent sender) {
				if (token > -1) {
					recipListPanel.getWidget(token).removeStyleName(
							"highlightRecipient");
					token = -1;
				}
			}
		});
	}

	public void clearText() {
		recipListPanel.clear();
		recipients.clear();
	}

	public void setRecipients(List<EmailAddress> ads) {
		clearText();
		for (EmailAddress addr: ads) {
			if (!addr.getEmail().isEmpty()) {
				addRecipient(addr);
			}
		}
	}

	public List<EmailAddress> getRecipients() {
		ArrayList<EmailAddress> ad = new ArrayList<EmailAddress>(recipients.size());
		for (String r : recipients.keySet()) {
			String display = recipients.get(r);
			int idx = display.lastIndexOf(" <");
			if (idx > 0) {
				display = display.substring(0, idx);
			}
			EmailAddress a = new EmailAddress(display, r);
			ad.add(a);
		}
		return ad;
	}

	public void focus() {
		mailField.setFocus(true);
	}

	private void addEmail(String email) {
		if (email.matches(PATTERN_EMAIL)) {
			addRecipient(email);
			mailField.setFocus(true);
			mailField.setText("");
		} else if (email.contains(",") || email.contains("<")) {
			String[] spl = email.split(",");
			for (String s : spl) {
				String m = s.trim();
				int idx = m.indexOf("<");
				if (idx > 0) {
					m = m.substring(idx + 1);
					m = m.replace(">", "");
					m = m.replace("\"", "");
				}
				if (m.matches(PATTERN_EMAIL)) {
					addRecipient(m);
				}
			}
			mailField.setFocus(true);
			mailField.setText("");
		}
	}

}
