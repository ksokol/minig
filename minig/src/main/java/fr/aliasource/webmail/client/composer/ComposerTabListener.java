package fr.aliasource.webmail.client.composer;

import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;

import fr.aliasource.webmail.client.I18N;
import fr.aliasource.webmail.client.View;

public class ComposerTabListener implements BeforeSelectionHandler<Integer>,
		SelectionHandler<Integer> {

	private MailComposer mc;
	private View ui;
	private boolean running;

	public ComposerTabListener(MailComposer mc, View ui) {
		this.mc = mc;
		this.ui = ui;
		this.running = false;
	}

	public void onBeforeSelection(BeforeSelectionEvent<Integer> bse) {
		if (running) {
			return;
		}
		running = true;
		onBeforeSelectionImpl(bse);
		running = false;
	}

	private void onBeforeSelectionImpl(BeforeSelectionEvent<Integer> bse) {
		// we switch between two tabs unrelated to composer
		if (bse.getItem() != View.COMPOSER
				&& ui.getCurrentTab() != View.COMPOSER) {
			return;
		}

		if (!mc.isTimerStarted()) {
			mc.clearComposer();
			return;
		}

		if (!mc.isEmpty()) {
			boolean discard = Window.confirm(I18N.strings
					.confirmDiscardMessage());
			if (discard) {
				mc.discard();
			} else {
				bse.cancel();
			}
		}
	}

	public void onSelection(SelectionEvent<Integer> se) {
		if (View.COMPOSER != se.getSelectedItem()) {
			return;
		}

		// Not called on quick reply mode (QRep has no tab listener)
		mc.resize();
		mc.focusTo();
	}

}
