package org.minig.server.service.impl.helper.mime;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * @author Kamill Sokol
 *
 * @see <a href="https://tools.ietf.org/html/rfc822">https://tools.ietf.org/html/rfc822</a>
 */
public final class Mime4jAddress {

    private static final String UNDISCLOSED_ADDRESS = "undisclosed address";

    private final String address;
    private final String personal;

    protected Mime4jAddress(String address) {
        String addressTmp = "";
        String personalTmp = UNDISCLOSED_ADDRESS;

        try {
            InternetAddress internetAddress = new InternetAddress(address);
            addressTmp = internetAddress.getAddress();
            personalTmp = internetAddress.getPersonal() == null ? addressTmp : internetAddress.getPersonal();
        } catch (AddressException exception) {
            addressTmp = "";
            personalTmp = UNDISCLOSED_ADDRESS;
        } finally {
            this.address = addressTmp;
            this.personal = personalTmp;
        }
    }

    public String getAddress() {
        return address;
    }

    public String getPersonal() {
        return personal;
    }
}
