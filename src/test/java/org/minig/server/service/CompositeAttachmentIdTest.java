package org.minig.server.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompositeAttachmentIdTest {

    @Test
    public void test1_invalid() {
        CompositeAttachmentId id = new CompositeAttachmentId();

        id.setFolder("folder");

        assertTrue(id.getId() == null);
    }

    @Test
    public void test2_invalid() {
        CompositeAttachmentId id = new CompositeAttachmentId();

        id.setMessageId("messageId");

        assertTrue(id.getId() == null);
    }

    @Test
    public void test3_invalid() {
        CompositeAttachmentId id = new CompositeAttachmentId();

        id.setFileName("fileName");

        assertTrue(id.getId() == null);
    }

    @Test
    public void test4_invalid() {
        CompositeId id = new CompositeId();

        assertTrue(id.getId() == null);
    }

    @Test
    public void test1() {
        CompositeAttachmentId id = new CompositeAttachmentId();

        id.setFolder("folder");
        id.setMessageId("messageId");
        id.setFileName("fileName");

        assertEquals("folder" + CompositeId.SEPARATOR + "messageId" + CompositeId.SEPARATOR + "fileName", id.getId());
    }

    @Test
    public void test2() {
        CompositeAttachmentId id = new CompositeAttachmentId();

        id.setMessageId("messageId");
        id.setFolder("folder");
        id.setFileName("fileName");

        assertEquals("folder" + CompositeId.SEPARATOR + "messageId" + CompositeId.SEPARATOR + "fileName", id.getId());
    }

    @Test
    public void test3() {
        CompositeAttachmentId id = new CompositeAttachmentId();

        id.setFileName("fileName");
        id.setMessageId("messageId");
        id.setFolder("folder");

        assertEquals("folder" + CompositeId.SEPARATOR + "messageId" + CompositeId.SEPARATOR + "fileName", id.getId());
    }

    @Test
    public void test4() {
        CompositeAttachmentId id = new CompositeAttachmentId();

        id.setMessageId("messageId");
        id.setFileName("fileName");
        id.setFolder("folder");

        assertEquals("folder" + CompositeId.SEPARATOR + "messageId" + CompositeId.SEPARATOR + "fileName", id.getId());
    }

    @Test
    public void test_setIdWins() {
        CompositeAttachmentId id = new CompositeAttachmentId();

        id.setId("folder" + CompositeId.SEPARATOR + "messageId" + CompositeId.SEPARATOR + "fileName");
        id.setFolder("ignoredFolder");
        id.setMessageId("ignoredMessageId");
        id.setFileName("ignoredFileName");

        assertEquals("folder" + CompositeId.SEPARATOR + "messageId" + CompositeId.SEPARATOR + "fileName", id.getId());
    }

}
