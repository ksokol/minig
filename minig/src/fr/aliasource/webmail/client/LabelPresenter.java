package fr.aliasource.webmail.client;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;

import fr.aliasource.webmail.client.ctrl.WebmailController;
import fr.aliasource.webmail.client.shared.Folder;

public class LabelPresenter {

	private Folder f;
	private HTML color;
	private Anchor link;
	private String defaultText;
	private boolean unreadOnly;
	private int idx;
	private RowFormatter rowFormatter;
	private boolean activeFilter;
	private SimpleDropController sdc;
	private HandlerRegistration reg;

	public LabelPresenter(Folder f, int idx, RowFormatter rowFormatter,
			boolean withDnD) {
		this.unreadOnly = true;
		this.f = f;
		this.idx = idx;
		this.rowFormatter = rowFormatter;
		defaultText = createText();
		createColor();
		createLink();
		if (withDnD) {
			addDnD();
		}
	}

	private void addDnD() {
		sdc = new SimpleDropController(link) {
			public void onDrop(DragContext context) {
				WebmailController.get().getView().getDragController().runMove(
						WebmailController.get().getSelector().getCurrent(), f);
				super.onDrop(context);
			}

			@Override
			public void onEnter(DragContext context) {
				super.onEnter(context);
				link.addStyleName("dropHighlight");
			}

			@Override
			public void onLeave(DragContext context) {
				super.onLeave(context);
				link.removeStyleName("dropHighlight");
			}
		};
		WebmailController.get().getView().getDragController()
				.registerDropController(sdc);
	}

	private void createLink() {
		Anchor ret = new Anchor(defaultText);
		ret.addStyleName("noWrap");
		ret.setTitle(f.getName());
		this.link = ret;
	}

	private String createText() {
		String name = f.getName();

		if (!WebmailController.get().isSystemFolder(name)) {
			String[] parts = name.split("/");
			StringBuilder n = new StringBuilder(name.length());
			for (int i = 0; i < parts.length; i++) {
				if (i > 0) {
					n.append('/');
				}
				if (i != parts.length - 1) {
					n.append(parts[i].charAt(0));
				} else {
					n.append(parts[i]);
				}
			}
			name = n.toString();
		} else {
			name = WebmailController.get().displayName(name);
		}
		return name;
	}

	private void createColor() {
		String color = WebSafeColors.htmlColor(f);
		this.color = new HTML(
				"<span style=\"background-color: "
						+ color
						+ "; -moz-border-radius: 2px; -webkit-border-radius: 2px; width: 10px;\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>");
	}

	public Widget getColor() {
		return color;
	}

	public Anchor getLink() {
		return link;
	}

	private void setVisible(boolean visible) {
		rowFormatter.setVisible(idx, visible);
	}

	public void setUnreadCount(int unreadCount) {
		if (unreadCount > 0) {
			link.addStyleName("bold");
			link.setText(defaultText + " (" + unreadCount + ")");
		} else {
			link.removeStyleName("bold");
			link.setText(defaultText);
		}

		setUnreadOnly(unreadOnly);
	}

	public void setUnreadOnly(boolean unreadOnly) {
		this.unreadOnly = unreadOnly;
		if (activeFilter) {
			return;
		}
		if (!unreadOnly) {
			setVisible(true);
		} else {
			setVisible(!defaultText.equals(link.getText()));
		}
	}

	public boolean isUnreadOnly() {
		return unreadOnly;
	}

	public void applyFilter(String text) {
		if (text == null || text.length() == 0) {
			this.activeFilter = false;
			setUnreadOnly(unreadOnly);
		} else {
			this.activeFilter = true;
			String name = f.getName();
			if (WebmailController.get().isSystemFolder(name)) {
				name = WebmailController.get().displayName(f);
			}

			String[] parts = name.toLowerCase().split("/");
			String t = text.toLowerCase();
			for (String s : parts) {
				boolean match = s.startsWith(t);
				setVisible(match);
				if (match) {
					break;
				}
			}
		}
	}

	public void unregisterDnD() {
		if (sdc != null) {
			WebmailController.get().getView().getDragController()
					.unregisterDropController(sdc);
		}
	}

	public void registerClickHandler(ClickHandler createHandler) {
		reg = getLink().addClickHandler(createHandler);
	}

	public void destroy() {
		unregisterDnD();
		if (reg != null) {
			reg.removeHandler();
			reg = null;
		}
		sdc = null;
		GWT.log("lbl presenter destroyed", null);
	}

}
