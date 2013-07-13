package fr.aliasource.webmail.client.chat;

import junit.framework.TestCase;
import fr.aliasource.webmail.server.MessageBeautifier;

public class MessageBeutifierTests extends TestCase {

	private MessageBeautifier mb;

	protected void setUp() {
		mb = new MessageBeautifier();
	}

	public void testReplace() {
		String[] replacable = {
				"http://www.youtube.com",
				"www.pouic.org",
				"Check www.minig.org please",
				"2 replacements www.obm.org and https://goatse.fr/pouic?action=toto",
				"https://www.obm.org", " https://www.obm.org" };
		for (String s : replacable) {
			String replaced = mb.beautify(s);
			System.err.println("replaced: " + replaced);
			assertNotSame(s, replaced);
		}

	}

	public void testNoReplacement() {
		String[] untouched = { "Toto titi",
				"checkout flashgames on <a href=\"www.ecrans.fr\">www.ecrans.fr</a>" };
		for (String s : untouched) {
			String un = mb.beautify(s);
			System.err.println("untouched: " + un);
			assertEquals(s, un);
		}
	}

}
