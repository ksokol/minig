package fr.aliasource.webmail.server;

import junit.framework.TestCase;

public class HTMLToPlainTests extends TestCase {

	public void testCtor() {
		new HTMLToPlainConverter();
	}

	private String cvt(String html) {
		return new HTMLToPlainConverter().convert(html);
	}

	public void testConvertDivs() {
		String ret = cvt("<div class=\"bla\">blabla</div>\n<div class=\"bla\">blibli</div>");
		assertNotNull(ret);
	}

	public void testConvertBr() {
		String ret = cvt("Cataldo Thomas a écrit :<br>&gt; Nouveautés :&nbsp;<br>&gt; &nbsp;- hello<br>&gt; &nbsp;- world<br><br>replay<br>");
		assertNotNull(ret);
	}

	public void testConvertEntities() {
		String ret = cvt("<div class=\"bla\">blabla&apos;s</div>\n<div class=\"bla\">black &amp; decker</div>");
		assertNotNull(ret);
	}

	public void testConvertQuotes() {
		String ret = cvt("<blockquote>blabla&apos;s</blockquote><div class=\"bla\">black &amp; decker</div>");
		assertNotNull(ret);
	}

	public void testConvertEuros() {
		String ret = cvt("<blockquote>€uro symbol</blockquote><div class=\"bla\">black &amp; decker</div>");
		assertNotNull(ret);
	}
}
