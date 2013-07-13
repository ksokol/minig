package fr.aliasource.webmail.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Fetches login & domain from HTTP headers. Sends a ticket with the login and
 * saslauthd is supposed to accept that.
 * 
 * @author tom
 * 
 */
public class NativeLemonProvider implements ISSOProvider {

	public static final String DOMAIN_PREF = "frontend.lemon.domainHeader";
	public static final String UID_PREF = "frontend.lemon.uidHeader";
	public static final String TICKET_PREF = "frontend.lemon.ticket";

	private static final Log logger = LogFactory
			.getLog(NativeLemonProvider.class);

	private Set<String> lemonIps;

	public NativeLemonProvider() throws IOException {
		lemonIps = Collections.synchronizedSet(new HashSet<String>());
		Properties p = new Properties();
		FileInputStream in = new FileInputStream("/etc/obm/obm_conf.ini");
		p.load(in);
		in.close();
		String[] ips = p.get("lemonLdapIps").toString().split(",");
		synchronized (lemonIps) {
			for (String ip : ips) {
				lemonIps.add(ip);
			}
		}
	}

	@Override
	public Credentials obtainCredentials(Map<String, String> settings,
			HttpServletRequest req) {
		synchronized (lemonIps) {
			if (!lemonIps.contains(req.getRemoteHost())) {
				logger
						.warn("login not coming from a valid lemon ldap ip address.");
				return null;
			}
		}
		String lat = req.getHeader(settings.get(UID_PREF));
		if (!lat.contains("@")) {
			lat = lat + "@" + req.getHeader(settings.get(DOMAIN_PREF));
		}
		return new Credentials(lat, settings.get(TICKET_PREF));
	}

	@Override
	public boolean redirectToSSOServer(String myUrl,
			Map<String, String> settings, HttpServletRequest req,
			HttpServletResponse resp) {
		return false;
	}

	@Override
	public boolean wentToSSOServer(HttpServletRequest req) {
		return true;
	}

}
