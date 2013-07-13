package fr.aliasource.webmail.client;

public final class XssUtils {

	public static final String safeHtml(String s) {
		if (s == null) {
			return "";
		} else {
			return s.replace("<", "&lt;").replace(">", "&gt;");
		}
	}

}
