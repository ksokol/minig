package org.minig.server.service;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class CompositeIdTest {

    @Test
    public void test1_invalid() {
        CompositeId id = new CompositeId();

        id.setFolder("folder");

        assertTrue(id.getId() == null);
    }

    @Test
    public void test2_invalid() {
        CompositeId id = new CompositeId();

        id.setMessageId("messageId");

        assertTrue(id.getId() == null);
    }

    @Test
    public void test3_invalid() {
        CompositeId id = new CompositeId();

        assertTrue(id.getId() == null);
    }

    @Test
    public void test1() {
        CompositeId id = new CompositeId();

        id.setFolder("folder");
        id.setMessageId("messageId");

        assertEquals("folder" + CompositeId.SEPARATOR + "messageId", id.getId());
    }

    @Test
    public void test2() {
        CompositeId id = new CompositeId();

        id.setMessageId("messageId");
        id.setFolder("folder");

        assertEquals("folder" + CompositeId.SEPARATOR + "messageId", id.getId());
    }

    @Test
    public void test3() {
        CompositeId id = new CompositeId();

        id.setId("folder" + CompositeId.SEPARATOR + "messageId");

        assertEquals("messageId", id.getMessageId());
        assertEquals("folder", id.getFolder());
    }

    @Test
    public void test_setIdWins() {
        CompositeId id = new CompositeId();

        id.setId("folder" + CompositeId.SEPARATOR + "messageId");
        id.setFolder("ignoredFolder");
        id.setMessageId("ignoredMessageId");

        assertEquals("folder" + CompositeId.SEPARATOR + "messageId", id.getId());
    }

    @Test
    public void shouldNotEqualWhenMessageIdIsNotEqual() throws Exception {
        CompositeId id1 = new CompositeId("INBOX", "messageId1");
        CompositeId id2 = new CompositeId("INBOX", "messageId2");

        assertThat(id1.equals(id2), is(false));
    }

    @Test
    public void shouldNotEqualWhenFolderIsNotEqual() throws Exception {
        CompositeId id1 = new CompositeId("INBOX1", "messageId");
        CompositeId id2 = new CompositeId("INBOX2", "messageId");

        assertThat(id1.equals(id2), is(false));
    }

    @Test
    public void shouldEqual() throws Exception {
        CompositeId id1 = new CompositeId("INBOX", "messageId1");
        CompositeId id2 = new CompositeId("INBOX", "messageId1");

        assertThat(id1.equals(id2), is(true));
    }

    @Test
    public void shouldNotHaveEqualHashCodeWhenMessageIdIsNotEqual() throws Exception {
        CompositeId id1 = new CompositeId("INBOX", "messageId1");
        CompositeId id2 = new CompositeId("INBOX", "messageId2");

        assertThat(id1.hashCode(), is(not(id2.hashCode())));
    }

    @Test
    public void shouldNotHaveEqualHashCodeWhenFolderIsNotEqual() throws Exception {
        CompositeId id1 = new CompositeId("INBOX1", "messageId");
        CompositeId id2 = new CompositeId("INBOX2", "messageId");

        assertThat(id1.hashCode(), is(not(id2.hashCode())));
    }

    @Test
    public void shouldHaveEqualHashCode() throws Exception {
        CompositeId id1 = new CompositeId("INBOX", "messageId1");
        CompositeId id2 = new CompositeId("INBOX", "messageId1");

        assertThat(id1.hashCode(), is(id2.hashCode()));
    }
}
