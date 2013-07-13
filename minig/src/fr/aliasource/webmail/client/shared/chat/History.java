package fr.aliasource.webmail.client.shared.chat;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class History extends LinkedList<HistoryItem> implements Serializable {

	private static final long serialVersionUID = 6922417136041408162L;

	private String chatId;
	private Set<String> participants;

	public History() {
		participants = new HashSet<String>();
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public Date getLastChat() {
		if (isEmpty()) {
			return null;
		} else {
			return getLast().getTimestamp();
		}
	}

	public Set<String> getParticipants() {
		return participants;
	}

	@Override
	public boolean add(HistoryItem e) {
		participants.add(e.getFrom());
		return super.add(e);
	}
}
