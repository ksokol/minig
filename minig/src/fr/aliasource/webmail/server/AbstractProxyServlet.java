package fr.aliasource.webmail.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractProxyServlet extends HttpServlet {

	private static final long serialVersionUID = -5136540462062536690L;
	
	/**
	 * "Official" HTTP line end
	 */
	public final static String CRLF = "\r\n";
	public final static String LF = "\n";

	private static final void appendCleaned(final StringBuilder sb,
			final String str) {
		for (int i = 0; i < str.length(); i++) {
			final char ch = str.charAt(i);
			if (ch == ' ') {
				sb.append("%20");
			} else {
				sb.append(ch);
			}
		}
	}

	/**
	 * remote path
	 */
	protected String remotePath;

	/**
	 * Port at remote server
	 */
	protected int remotePort;

	/**
	 * remote server
	 */
	protected String remoteServer;
	protected final Log logger = LogFactory.getLog(getClass());

	public AbstractProxyServlet() {
	}

	/**
	 * Copy a file from in to out. Sub-classes can override this in order to do
	 * filtering of some sort.
	 */
	public void copyStream(final InputStream in, final OutputStream out)
			throws IOException {
		final BufferedInputStream bin = new BufferedInputStream(in);
		int b;
		while ((b = bin.read()) != -1) {
			out.write(b);
		}
	}

	@Override
	public String getServletInfo() {
		return "Online redirecting content.";
	}

	/**
	 * Init
	 */
	@Override
	public abstract void init(final ServletConfig config) throws ServletException;
	
	/**
	 * Read a RFC2616 line from an InputStream:
	 */
	public int readLine(final InputStream in, final byte[] b)
			throws IOException {
		int off2 = 0;
		while (off2 < b.length) {
			final int r = in.read();
			if (r == -1) {
				if (off2 == 0) {
					return -1;
				}
				break;
			}
			if (r == 13) {
				continue;
			}
			if (r == 10) {
				break;
			}
			b[off2] = (byte) r;
			++off2;
		}
		return off2;
	}

	@SuppressWarnings({ "rawtypes" })
	@Override
	protected void service(final HttpServletRequest req,
			final HttpServletResponse res) throws ServletException, IOException {

		// Connect to "remote" server:
		Socket sock;
		OutputStream out;
		InputStream in;
		//
		try {
			sock = new Socket(remoteServer, remotePort); // !!!!!!!!
			out = new BufferedOutputStream(sock.getOutputStream());
			in = new BufferedInputStream(sock.getInputStream());
		} catch (final IOException e) {
			res.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
					"Socket opening: " + remoteServer + ":" + remotePort
							+ " >> " + e.toString());
			return;
		}
		try {
			// Build up a HTTP request from pure strings:
			final StringBuilder sb = new StringBuilder(200);
			sb.append(req.getMethod());
			sb.append(' ');
			final String pi = req.getPathInfo();
			sb.append(remotePath);
			if (pi != null) {
				appendCleaned(sb, pi);
			} else {
				sb.append("/");
			}
			if (req.getQueryString() != null) {
				sb.append('?');
				appendCleaned(sb, req.getQueryString());
			}
			sb.append(' ');
			sb.append("HTTP/1.0");
			sb.append(CRLF);
			out.write(sb.toString().getBytes());
			final java.util.Enumeration en = req.getHeaderNames();
			while (en.hasMoreElements()) {
				final String k = (String) en.nextElement();
				// Filter incoming headers:
				if ("Host".equalsIgnoreCase(k)) {
					sb.setLength(0);
					sb.append(k);
					sb.append(": ");
					sb.append(remoteServer);
					sb.append(":");
					sb.append(remotePort);
					sb.append(CRLF);
					out.write(sb.toString().getBytes());
				}
				//
				// Throw away persistant connections between
				// servers
				// Throw away request potentially causing a 304
				// response.
				else if (!"Connection".equalsIgnoreCase(k)
						&& !"If-Modified-Since".equalsIgnoreCase(k)
						&& !"If-None-Match".equalsIgnoreCase(k)) {
					sb.setLength(0);
					sb.append(k);
					sb.append(": ");
					sb.append(req.getHeader(k));
					sb.append(CRLF);
					out.write(sb.toString().getBytes());
				}
			}
			// Finish request header by an empty line
			out.write(CRLF.getBytes());
			// Copy post data
			final InputStream inr = req.getInputStream();
			copyStream(inr, out);
			out.flush();

			// Now we have finished the outgoing request.
			// We'll now see, what is coming back from remote:

			// Get the answer, treat its header and copy the stream
			// data:
			if (treatHeader(in, req, res)) {
				out = res.getOutputStream();
				copyStream(in, out);
			}
		} catch (final IOException e) {
			return;
		}
		try {
			// out.close();
			in.close();
			sock.close();
		} catch (final IOException ignore) {
		}
	}

	/**
	 * Split a blank separated string into
	 */
	public String[] wordStr(final String inp) {
		final StringTokenizer tok = new StringTokenizer(inp, " ");
		int i;
		final int n = tok.countTokens();
		final String[] res = new String[n];
		for (i = 0; i < n; i++) {
			res[i] = tok.nextToken();
		}
		return res;
	}

	/**
	 * XXX Should identify RFC2616 LWS
	 */
	protected boolean isLWS(final char c) {
		return c == ' ';
	}

	/**
	 * Forward and filter header from backend Request.
	 */
	private boolean treatHeader(final InputStream in,
			final HttpServletRequest req, final HttpServletResponse res)
			throws ServletException {
		boolean retval = true;
		final byte[] lineBytes = new byte[4096];
		int len;
		String line;

		try {
			// Read the first line of the request.
			len = readLine(in, lineBytes);
			if (len == -1 || len == 0) {
				throw new ServletException("No Request found in Data.");
			}

			// We mainly skip the header by the foreign server
			// assuming, that we can handle protocoll mismatch or
			// so!
			res.setHeader("viaJTTP", "JTTP");

			// Some more headers require special care ....
			boolean firstline = true;
			// Shortcut evaluation skips the read on first time!
			while (firstline || (len = readLine(in, lineBytes)) > 0) {
				line = new String(lineBytes, 0, len);
				final int colonPos = line.indexOf(":");
				if (firstline && colonPos == -1) {
					// Special first line considerations ...
					final String headl[] = wordStr(line);
					try {
						res.setStatus(Integer.parseInt(headl[1]));
					} catch (final NumberFormatException ignore) {
					} catch (final Exception panik) {
						return true;
					}
				} else if (colonPos != -1) {
					final String head = line.substring(0, colonPos);
					// XXX Skip LWS (what is LWS)
					int i = colonPos + 1;
					while (isLWS(line.charAt(i))) {
						i++;
					}
					final String value = line.substring(i);
					if (head.equalsIgnoreCase("Location")) {
						// res.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
						// res.setHeader(head, value );
					} else if (head.equalsIgnoreCase("Content-type")) {
						res.setContentType(value);
					} else if (head.equalsIgnoreCase("Content-length")) {
						try {
							final int cLen = Integer.parseInt(value);
							retval = cLen > 0;
							res.setContentLength(cLen);
						} catch (final NumberFormatException ignore) {
						}
					}
					// Generically treat unknown headers
					else {
						res.setHeader(head, value);
					}
				}
				// XXX We do not treat multiline continuation
				// Headers here
				// which have not occured anywhere yet.
				firstline = false;
			}
		} catch (final IOException e) {
			throw new ServletException("Header skip problem: " + e.getMessage());
		}
		return retval;
	}

}
