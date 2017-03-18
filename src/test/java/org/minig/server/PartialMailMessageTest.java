package org.minig.server;

import org.junit.Before;
import org.junit.Test;
import org.minig.server.service.MimeMessageBuilder;

import java.util.Date;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Kamill Sokol
 */
public class PartialMailMessageTest {

    private PartialMailMessage partialMailMessage;
    private MimeMessageBuilder builder;

    @Before
    public void setUp() throws Exception {
        builder = new MimeMessageBuilder()
                .setFolder("folder")
                .setMessageId("messageId");

        partialMailMessage = new PartialMailMessage(builder.mock());
    }

    @Test
    public void getId() throws Exception {
        assertThat(partialMailMessage.getId(), is("folder|messageId"));
    }

    @Test
    public void getMessageId() throws Exception {
        assertThat(partialMailMessage.getMessageId(), is("messageId"));
    }

    @Test
    public void getFolder() throws Exception {
        assertThat(partialMailMessage.getFolder(), is("folder"));
    }

    @Test
    public void getSubject() throws Exception {
        partialMailMessage = new PartialMailMessage(builder.setSubject("subject").mock());
        assertThat(partialMailMessage.getSubject(), is("subject"));
    }

    @Test
    public void getDate() throws Exception {
        Date expectedDate = new Date();
        partialMailMessage = new PartialMailMessage(builder.setDate(expectedDate).mock());
        assertThat(partialMailMessage.getDate(), is(expectedDate));
    }

    @Test
    public void getAnswered() throws Exception {
        partialMailMessage = new PartialMailMessage(builder.setAnswered(false).mock());
        assertThat(partialMailMessage.getAnswered(), is(false));

        partialMailMessage = new PartialMailMessage(builder.setAnswered(true).mock());
        assertThat(partialMailMessage.getAnswered(), is(true));
    }

    @Test
    public void getDeleted() throws Exception {
        partialMailMessage = new PartialMailMessage(builder.setDeleted(false).mock());
        assertThat(partialMailMessage.getDeleted(), is(false));

        partialMailMessage = new PartialMailMessage(builder.setDeleted(true).mock());
        assertThat(partialMailMessage.getDeleted(), is(true));
    }

    @Test
    public void getRead() throws Exception {
        partialMailMessage = new PartialMailMessage(builder.setRead(false).mock());
        assertThat(partialMailMessage.getRead(), is(false));

        partialMailMessage = new PartialMailMessage(builder.setRead(true).mock());
        assertThat(partialMailMessage.getRead(), is(true));
    }

    @Test
    public void getStarred() throws Exception {
        partialMailMessage = new PartialMailMessage(builder.setStarred(false).mock());
        assertThat(partialMailMessage.getStarred(), is(false));

        partialMailMessage = new PartialMailMessage(builder.setStarred(true).mock());
        assertThat(partialMailMessage.getStarred(), is(true));
    }

    @Test
    public void getForwarded() throws Exception {
        partialMailMessage = new PartialMailMessage(builder.setForwarded(false).mock());
        assertThat(partialMailMessage.getForwarded(), is(false));

        partialMailMessage = new PartialMailMessage(builder.setForwarded(true).mock());
        assertThat(partialMailMessage.getForwarded(), is(true));
    }

    @Test
    public void getSender() throws Exception {
        partialMailMessage = new PartialMailMessage(builder.setSender("sender").mock());
        assertThat(partialMailMessage.getSender(),
                allOf(
                        hasProperty("email", is("sender")),
                        hasProperty("displayName", is("sender"))
                    )
                );
    }

    @Test
    public void getSenderWithDisplayName() throws Exception {
        partialMailMessage = new PartialMailMessage(builder.setSender("sender <email@localhost>").mock());
        assertThat(partialMailMessage.getSender(),
                allOf(
                        hasProperty("email", is("email@localhost")),
                        hasProperty("displayName", is("sender"))
                )
        );
    }

    @Test
    public void getUndisclosedSender() throws Exception {
        partialMailMessage = new PartialMailMessage(builder.setSender(null).mock());
        assertThat(partialMailMessage.getSender(),
                allOf(
                        hasProperty("email", is("undisclosed sender")),
                        hasProperty("displayName", is("undisclosed sender"))
                )
        );
    }
}
