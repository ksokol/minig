package fr.aliasource.webmail.client.shared;

import java.io.Serializable;

public class SendParameters implements Serializable {

	private static final long serialVersionUID = -3789504249482405197L;

	private boolean highPriority;
	private boolean askForDispositionNotification;
	private boolean sign;
	private boolean encrypt;
	private boolean sendPlainText;

	public SendParameters() {
	}

	public boolean isHighPriority() {
		return highPriority;
	}

	public boolean isSign() {
		return sign;
	}

	public boolean isEncrypt() {
		return encrypt;
	}

	public boolean isSendPlainText() {
		return sendPlainText;
	}

	public boolean isAskForDispositionNotification() {
		return askForDispositionNotification;
	}
	
	public void setSendPlainText(boolean sendPlainText) {
		this.sendPlainText = sendPlainText;
	}

	public void setHighPriority(boolean highPriority) {
		this.highPriority = highPriority;
	}

	public void setSign(boolean sign) {
		this.sign = sign;
	}

	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}

	public void setAskForDispositionNotification(boolean askForDispositionNotification) {
		this.askForDispositionNotification = askForDispositionNotification;
	}
	
}
