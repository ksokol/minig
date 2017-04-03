package org.minig.server.service.impl.helper.mime;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.assertThat;

/**
 * @author Kamill Sokol
 */
public class Mime4jAddressTest {

    @Test
    public void shouldSetUndisclosedAddressForPersonal() throws Exception {
        Mime4jAddress mime4jAddress = new Mime4jAddress("");

        assertThat(mime4jAddress.getAddress(), isEmptyString());
        assertThat(mime4jAddress.getPersonal(), is("undisclosed address"));
    }

    @Test
    public void shouldSetEmailAddress() throws Exception {
        Mime4jAddress mime4jAddress = new Mime4jAddress("recipient@localhost");

        assertThat(mime4jAddress.getAddress(), is("recipient@localhost"));
        assertThat(mime4jAddress.getPersonal(), is("recipient@localhost"));
    }

    @Test
    public void shouldSetEmailAddressAndPersonal() throws Exception {
        Mime4jAddress mime4jAddress = new Mime4jAddress("Personal <recipient@localhost>");

        assertThat(mime4jAddress.getAddress(), is("recipient@localhost"));
        assertThat(mime4jAddress.getPersonal(), is("Personal"));
    }
}
