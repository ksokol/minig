package fr.aliasource.webmail.server;

import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class HTMLToPlainHandler implements ContentHandler {

	private StringBuilder cur = new StringBuilder();
	private StringBuilder result;

	private Log logger = LogFactory.getLog(getClass());

	private HashSet<String> crElems = new HashSet<String>();
	private HashSet<String> deleteElems = new HashSet<String>();

	private boolean prevIsLineBreak;

	public HTMLToPlainHandler() {
		prevIsLineBreak = false;
		cur = new StringBuilder();
		result = new StringBuilder();
		// localName are all uppercased by neko
		crElems.add("DIV");
		crElems.add("BR");
		crElems.add("P");
		crElems.add("TR");
		crElems.add("BLOCKQUOTE");

		deleteElems.add("STYLE");
		deleteElems.add("META");
		deleteElems.add("TITLE");
		deleteElems.add("HEAD");
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String s = new String(ch, start, length);
		cur.append(s);
		prevIsLineBreak = false;
		if (logger.isDebugEnabled()) {
			logger.debug("s: '" + s + "'");
		}
	}

	@Override
	public void endDocument() throws SAXException {
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		String curString = cur.toString();
		if (!deleteElems.contains(localName.toUpperCase())
				&& curString.trim().length() > 0) {
			result.append(curString);
		}
		cur = new StringBuilder();
		if (crElems.contains(localName)) {
			result.append("\n");
			prevIsLineBreak = true;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("endElem " + localName + " prevIsLineBreak: "
					+ prevIsLineBreak);
		}
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	@Override
	public void setDocumentLocator(Locator locator) {
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
	}

	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if (logger.isDebugEnabled()) {
			logger.debug("localname: " + localName + " prevIsLB: "
					+ prevIsLineBreak);
		}
		if (localName.equals("BLOCKQUOTE")) {
			cur.append("> ");
		}
		if (!prevIsLineBreak && crElems.contains(localName)
				&& !"BR".equals(localName)) {
			cur.append('\n');
		}
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

	public String getResult() {
		return result.toString();
	}

}
