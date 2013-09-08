package fr.aliasource.webmail.client.composer;

public class PlainToHTMLConverter {

	public String convert(String plain) {
		String ret = plain;

		ret = ret.replace("<", "&lt;");
		ret = ret.replace(">", "&gt;");

		ret = ret.replace("\r", "");
		ret = ret.replace("\n", "<br>");

		return ret;
	}

}
