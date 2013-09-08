package fr.aliasource.webmail.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ObmProxyServlet extends HttpServlet {
	private Log logger = LogFactory.getLog(getClass());

	private String url;

	private static final long serialVersionUID = -4358239128130172597L;

	/**
	 * Init
	 */
	@Override
	public void init(final ServletConfig config) throws ServletException {
		ObmConfig fec = new ObmConfig();

		StringBuilder extUrl = new StringBuilder();
		extUrl.append(fec.getExternalUrl());
		extUrl.append("sections/sections_index.php");
		url = extUrl.toString();
		logger.info("Fetching sections from " + url);

	}

	/**
	 * Fast stream transfer method
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	private void transfer(InputStream in, OutputStream out, boolean closeIn)
			throws IOException {
		final byte[] buffer = new byte[2048];

		try {
			while (true) {
				int amountRead = in.read(buffer);
				if (amountRead == -1) {
					break;
				}
				out.write(buffer, 0, amountRead);
			}
		} finally {
			if (closeIn) {
				in.close();
			}
			out.flush();
			out.close();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.error("only post is allowed on this url, action="
				+ req.getParameter("action"));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String action = req.getParameter("action");
		String login = req.getParameter("login");
		String composedUrl = url + "?action=" + action + "&login=" + login;
		logger.info("fetching from obm at " + composedUrl);
		URL u = new URL(composedUrl);
		resp.setContentType("text/html");
		resp.setCharacterEncoding("UTF-8");

		InputStream in = u.openStream();
		ServletOutputStream out = resp.getOutputStream();
		transfer(in, out, true);
		out.close();
	}

}
