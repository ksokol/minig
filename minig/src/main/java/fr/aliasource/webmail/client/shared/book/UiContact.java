package fr.aliasource.webmail.client.shared.book;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;

/**
 * This is copied from <em>obm-sync</em> contacts object model.
 * 
 * @author tom
 * 
 */
public class UiContact implements Serializable {

	private static final long serialVersionUID = 1370574699081226015L;

	private Integer uid;

	private String firstname;
	private String lastname;
	private String middlename;
	private String suffix;

	private String title;
	private String service;
	private String aka;
	private String comment;

	private String company;

	private Date birthday;
	private Integer birthdayId;

	private Date anniversary;
	private Integer anniversaryId;

	private String assistant;
	private String manager;
	private String spouse;

	private Integer entityId;

	private boolean collected;

	private Map<String, UiPhone> phones;
	private Map<String, UiWebsite> websites;
	private Map<String, UiEmail> emails;
	private Map<String, UiInstantMessagingId> imIdentifiers;
	private Map<String, UiAddress> addresses;

	public UiContact() {
		phones = new HashMap<String, UiPhone>();
		websites = new HashMap<String, UiWebsite>();
		emails = new HashMap<String, UiEmail>();
		addresses = new HashMap<String, UiAddress>();
		imIdentifiers = new HashMap<String, UiInstantMessagingId>();
		collected = false;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getAka() {
		return aka;
	}

	public void setAka(String aka) {
		this.aka = aka;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Map<String, UiPhone> getPhones() {
		return phones;
	}

	public Map<String, UiWebsite> getWebsites() {
		return websites;
	}

	public Map<String, UiEmail> getEmails() {
		return emails;
	}

	public Map<String, UiInstantMessagingId> getImIdentifiers() {
		return imIdentifiers;
	}

	public Map<String, UiAddress> getAddresses() {
		return addresses;
	}

	public void addPhone(String lbl, UiPhone p) {
		phones.put(lbl, p);
	}

	public void addAddress(String lbl, UiAddress p) {
		addresses.put(lbl, p);
	}

	public void addWebsite(String lbl, UiWebsite p) {
		websites.put(lbl, p);
	}

	public void addIMIdentifier(String lbl, UiInstantMessagingId imid) {
		imIdentifiers.put(lbl, imid);
	}

	public void addEmail(String lbl, UiEmail email) {
		emails.put(lbl, email);
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public Integer getBirthdayId() {
		return birthdayId;
	}

	public void setBirthdayId(Integer birthdayId) {
		this.birthdayId = birthdayId;
	}

	public String getMiddlename() {
		return middlename;
	}

	public void setMiddlename(String middlename) {
		this.middlename = middlename;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Date getAnniversary() {
		return anniversary;
	}

	public void setAnniversary(Date anniversary) {
		this.anniversary = anniversary;
	}

	public Integer getAnniversaryId() {
		return anniversaryId;
	}

	public void setAnniversaryId(Integer anniversaryId) {
		this.anniversaryId = anniversaryId;
	}

	public String getAssistant() {
		return assistant;
	}

	public void setAssistant(String assistant) {
		this.assistant = assistant;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

	public String getSpouse() {
		return spouse;
	}

	public void setSpouse(String spouse) {
		this.spouse = spouse;
	}

	public boolean isCollected() {
		return collected;
	}

	public void setCollected(boolean collected) {
		this.collected = collected;
	}

	private boolean empty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public String getDisplayName() {
		if (!GWT.isClient()) {
			throw new RuntimeException(
					"getDisplayName can only be called from gwt context, not from server side");
		}

		// TODO use i18n on this one
		StringBuilder ret = new StringBuilder();

		// last filled & maybe first
		if (!empty(getLastname())) {
			ret.append(getLastname());
			if (!empty(getFirstname())) {
				ret.append(' ');
				ret.append(getFirstname());
			}
		} else if (!empty(getFirstname())) { // last empty, first filled
			ret.append(getFirstname());
		} else { // both are empty
			ret.append("[John Doe] (no display name...)");
		}

		return ret.toString();
	}

}
