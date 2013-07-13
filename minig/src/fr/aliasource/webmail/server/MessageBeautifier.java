package fr.aliasource.webmail.server;

public class MessageBeautifier {

	private static String PATTERN_URL_WITH_HTTP = "[^\">](((http://)|(https://))"
			+ "+[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)"
			+ "+(/[#&;\\n\\-=?:|,()@\\+\\%/\\.\\w]+)?+(:[0-9]*+)?)";

	private static String PATTERN_URL_WITH_HTTP_START = "^(((http://)|(https://))"
			+ "+[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)"
			+ "+(/[#&;\\n\\-=?:|,()@\\+\\%/\\.\\w]+)?+(:[0-9]*+)?)";

	private static String PATTERN_URL_WITHOUT_HTTP = "[^\"/>](((w{3}.))"
			+ "+[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)"
			+ "+(/[#&;\\n\\-=?:|,()@\\+\\%/\\.\\w]+)?+(:[0-9]*+)?)";

	private static String PATTERN_URL_WITHOUT_HTTP_START = "^(((w{3}.))"
			+ "+[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)"
			+ "+(/[#&;\\n\\-=?:|,()@\\+\\%/\\.\\w]+)?+(:[0-9]*+)?)";

	public MessageBeautifier() {
	}

	public String beautify(String origMsg) {
		String ret = origMsg;

		// TODO handle received xml tags
		// ret = ret.replace("<[]", "&lt;").replace(">", "&gt;");

		ret = ret.replaceAll(PATTERN_URL_WITH_HTTP,
				" <a target=\"_blank\" href=\"$1\">$1</a>");
		ret = ret.replaceAll(PATTERN_URL_WITHOUT_HTTP,
				" <a target=\"_blank\" href=\"http://$1\">$1</a>");

		ret = ret.replaceAll(PATTERN_URL_WITH_HTTP_START,
				"<a target=\"_blank\" href=\"$1\">$1</a>");
		ret = ret.replaceAll(PATTERN_URL_WITHOUT_HTTP_START,
				"<a target=\"_blank\" href=\"http://$1\">$1</a>");

		return ret;
	}
}
