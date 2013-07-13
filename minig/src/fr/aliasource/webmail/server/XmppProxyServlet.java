package fr.aliasource.webmail.server;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import fr.aliasource.webmail.client.rpc.GetSettings;

public class XmppProxyServlet extends AbstractProxyServlet {

	
	private static final long serialVersionUID = -4358239128130172597L;

	/**
	 * Init
	 */
	@Override
	public void init(final ServletConfig config) throws ServletException {
		FrontEndConfig fec = new FrontEndConfig();
		String url = fec.get().get(GetSettings.XMPP_BIND_URL);
		try {
			URL urlObj = new URL(url);
			remotePath = urlObj.getPath();
			remotePort = urlObj.getPort();
			remoteServer = urlObj.getHost();
		} catch (MalformedURLException e1) {
			throw new ServletException(e1);
		}

		logger.info("remote=" + remoteServer + ":" + remotePort + ""
				+ remotePath);
	}



}
