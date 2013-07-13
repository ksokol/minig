package fr.aliasource.webmail.server;

import java.io.StringReader;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.cyberneko.html.parsers.SAXParser;

public class HTMLToPlainConverter {

	private Log logger = LogFactory.getLog(getClass());

	public String convert(String html) {
		String ret = html;

		SAXParser sp = new SAXParser();

		HTMLToPlainHandler ch = new HTMLToPlainHandler();
		sp.setContentHandler(ch);
		XMLInputSource xis = new XMLInputSource(null, null, null,
				new StringReader(ret), null);
		try {
			sp.parse(xis);
			ret = ch.getResult();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("plainConversion result:\n" + ret);
		}

		// wrap lines
		StringBuilder sb = new StringBuilder();
		String[] lines = ret.split("\n");
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("> ")) {
				sb.append(lines[i]).append("\n");
			} else {
				sb.append(WordUtils.wrap(lines[i], 80)).append("\n");
			}
		}
		ret = sb.toString();

		return ret;
	}

}
