package fr.aliasource.webmail.client.shared.book;

import java.io.Serializable;

public class UiAddress implements Serializable {

	private static final long serialVersionUID = -1466221856037162728L;

	private String street;
	private String zipCode;
	private String expressPostal;
	private String town;
	private String country;
	private String state;

	public UiAddress() {
		// default constructor to please GWT
	}

	public UiAddress(String street, String zipCode, String expressPostal,
			String town, String country, String state) {
		super();
		this.street = street;
		this.zipCode = zipCode;
		this.expressPostal = expressPostal;
		this.town = town;
		this.country = country;
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public String getExpressPostal() {
		return expressPostal;
	}

	public String getStreet() {
		return street;
	}

	public String getTown() {
		return town;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void setExpressPostal(String expressPostal) {
		this.expressPostal = expressPostal;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
