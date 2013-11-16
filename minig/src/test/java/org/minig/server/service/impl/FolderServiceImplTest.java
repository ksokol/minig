package org.minig.server.service.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailFolder;
import org.minig.server.MailFolderList;
import org.minig.server.TestConstants;
import org.minig.server.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.mail.internet.MimeMessage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceTestConfig.class })
@ActiveProfiles("test")
public class FolderServiceImplTest {

    @Autowired
    private FolderService uut;

    @Autowired
    private SmtpAndImapMockServer mockServer;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        mockServer.reset();
        mockServer.createAndSubscribeMailBox("INBOX.Trash");
        mockServer.createAndSubscribeMailBox("INBOX.Sent");
        mockServer.createAndSubscribeMailBox("INBOX.Drafts");
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testCreateFolderInInbox_invalidArguments() {
        int count = 0;

        try {
            uut.createFolderInInbox(null);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.createFolderInInbox("");
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.createFolderInInbox("   ");
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(3, count);
    }

    @Test
    public void testCreateFolderInInbox() {
        uut.createFolderInInbox("test");
        mockServer.verifyMailbox("INBOX.test");
    }

    @Test
    public void testCreateInParent_invalidArguments() {
        int count = 0;

        try {
            uut.createFolderInParent(null, null);
        } catch (Exception e) {
            count++;
        }

        try {
            uut.createFolderInParent("", "");
        } catch (Exception e) {
            count++;
        }

        try {
            uut.createFolderInParent("  ", "   ");
        } catch (Exception e) {
            count++;
        }

        try {
            uut.createFolderInParent("", null);
        } catch (Exception e) {
            count++;
        }

        try {
            uut.createFolderInParent("   ", null);
        } catch (Exception e) {
            count++;
        }

        try {
            uut.createFolderInParent("parent", null);
        } catch (Exception e) {
            count++;
        }

        try {
            uut.createFolderInParent("parent", "");
        } catch (Exception e) {
            count++;
        }

        try {
            uut.createFolderInParent("parent", "  ");
        } catch (Exception e) {
            count++;
        }

        try {
            uut.createFolderInParent("parent", "  ");
        } catch (Exception e) {
            count++;
        }

        assertEquals(9, count);
    }

    @Test
    public void testCreateInParent() {
        uut.createFolderInParent("INBOX", "test1");
        mockServer.verifyMailbox("INBOX.test1");

        mockServer.createAndSubscribeMailBox("INBOX.test2");

        uut.createFolderInParent("INBOX.test2", "test3");
        mockServer.verifyMailbox("INBOX.test2.test3");
    }

    @Test
    public void testFindAll() {
        MailFolderList findAll = uut.findAll();

        // INBOX, Trash, Sent, Drafts
        assertEquals(4, findAll.getFolderList().size());

        for (MailFolder mf : findAll.getFolderList()) {
            assertFalse(mf.getEditable());
        }
    }

    @Test
    public void testUpdateFolder_invalidArguments() {
        int count = 0;

        try {
            uut.updateFolder(null);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.updateFolder(new MailFolder());
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(2, count);
    }

    @Test
    public void testUpdateFolder_unsubscribing() throws InterruptedException {
        mockServer.createAndSubscribeMailBox("INBOX.test");

        assertEquals(4, uut.findBySubscribed(true).getFolderList().size());

        MailFolder mf = uut.findById("INBOX.test");
        mf.setSubscribed(false);
        uut.updateFolder(mf);

        assertEquals(3, uut.findBySubscribed(true).getFolderList().size());
    }

    @Test
    public void testUpdateFolder_movingFolderNoMessagesNoNestedFolders() throws InterruptedException {
        mockServer.createAndSubscribeMailBox("INBOX.source");
        mockServer.createAndSubscribeMailBox("INBOX.source.folder");

        mockServer.createAndSubscribeMailBox("INBOX.target");

        MailFolder mf = uut.findById("INBOX.source.folder");
        mf.setParentFolderId("INBOX.target");
        uut.updateFolder(mf);

        assertEquals(0, uut.findByParent("INBOX.source").getFolderList().size());
        assertEquals(1, uut.findByParent("INBOX.target").getFolderList().size());
        assertEquals("INBOX.target", uut.findById("INBOX.target.folder").getParentFolderId());
    }

    @Test
    public void testUpdateFolder_movingFolderNoMessagesWithNestedFolders() throws InterruptedException {
        mockServer.createAndSubscribeMailBox("INBOX.source");
        mockServer.createAndSubscribeMailBox("INBOX.source.folder");
        mockServer.createAndSubscribeMailBox("INBOX.source.folder.nested1");
        mockServer.createAndSubscribeMailBox("INBOX.source.folder.nested2");
        mockServer.createAndSubscribeMailBox("INBOX.target");

        MailFolder mf = uut.findById("INBOX.source.folder");
        mf.setParentFolderId("INBOX.target");
        uut.updateFolder(mf);

        assertTrue(uut.findById("INBOX.source.folder") == null);
        assertEquals(0, uut.findByParent("INBOX.source").getFolderList().size());
        assertEquals(1, uut.findByParent("INBOX.target").getFolderList().size());
        assertEquals("INBOX.target", uut.findById("INBOX.target.folder").getParentFolderId());
        assertEquals(2, uut.findByParent("INBOX.target.folder").getFolderList().size());
    }

    @Test
    public void testUpdateFolder_movingFolderWithMessagesWithNestedFolders() throws InterruptedException {
        MimeMessage msg = new MimeMessageBuilder().build(TestConstants.PLAIN);

        mockServer.createAndSubscribeMailBox("INBOX.source");
        mockServer.prepareMailBox("INBOX.source.folder", msg);
        mockServer.prepareMailBox("INBOX.source.folder.nested1", msg);
        mockServer.prepareMailBox("INBOX.source.folder.nested2", msg);
        mockServer.createAndSubscribeMailBox("INBOX.target");

        MailFolder mf = uut.findById("INBOX.source.folder");
        mf.setParentFolderId("INBOX.target");
        uut.updateFolder(mf);

        assertEquals(0, uut.findByParent("INBOX.source").getFolderList().size());
        assertEquals(1, uut.findByParent("INBOX.target").getFolderList().size());
        assertEquals("INBOX.target", uut.findById("INBOX.target.folder").getParentFolderId());
        assertEquals(2, uut.findByParent("INBOX.target.folder").getFolderList().size());

        mockServer.verifyMessageCount("INBOX.target.folder", 1);
        mockServer.verifyMessageCount("INBOX.target.folder.nested1", 1);
        mockServer.verifyMessageCount("INBOX.target.folder.nested2", 1);
    }

    @Test
    public void testDeleteFolder_invalidArguments() {
        int count = 0;

        try {
            uut.deleteFolder(null);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.deleteFolder("");
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.deleteFolder("    ");
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(3, count);
    }

    @Test
    public void testDeleteFolder_trashedNoMessagesNoNestedFolders() {
        mockServer.createAndSubscribeMailBox("INBOX.Trash.deleteme");
        uut.deleteFolder("INBOX.Trash.deleteme");
        assertEquals(0, uut.findByParent("INBOX.Trash").getFolderList().size());
    }

    @Test
    public void testDeleteFolder_trashedNoMessagesWithNestedFolders() {
        mockServer.createAndSubscribeMailBox("INBOX.Trash.deleteme");
        mockServer.createAndSubscribeMailBox("INBOX.Trash.deleteme.nested1");
        mockServer.createAndSubscribeMailBox("INBOX.Trash.deleteme.nested2");

        uut.deleteFolder("INBOX.Trash.deleteme");
        assertEquals(0, uut.findByParent("INBOX.Trash").getFolderList().size());
    }

    @Test
    public void testDeleteFolder_trashedWithMessagesWithNestedFolders() {
        MimeMessage msg = new MimeMessageBuilder().build(TestConstants.PLAIN);

        mockServer.createAndSubscribeMailBox("INBOX.Trash.deleteme");
        mockServer.prepareMailBox("INBOX.Trash.deleteme", msg);
        mockServer.prepareMailBox("INBOX.Trash.deleteme.nested1", msg);
        mockServer.prepareMailBox("INBOX.Trash.deleteme.nested2", msg);

        uut.deleteFolder("INBOX.Trash.deleteme.nested1");

        assertEquals(1, uut.findByParent("INBOX.Trash").getFolderList().size());
        assertEquals(1, uut.findByParent("INBOX.Trash.deleteme").getFolderList().size());

        mockServer.verifyMessageCount("INBOX.Trash.deleteme", 1);
        mockServer.verifyMessageCount("INBOX.Trash.deleteme.nested2", 1);

        uut.deleteFolder("INBOX.Trash.deleteme");

        assertEquals(0, uut.findByParent("INBOX.Trash").getFolderList().size());
    }

    @Test
    public void testDeleteFolder_noMessagesNoNestedFolders() {
        mockServer.createAndSubscribeMailBox("INBOX.deleteme2");

        uut.deleteFolder("INBOX.deleteme2");

        assertTrue(uut.findById("INBOX.deleteme2") == null);
        assertEquals("INBOX.Trash.deleteme2", uut.findById("INBOX.Trash.deleteme2").getId());
    }

    @Test
    public void testDeleteFolder_noMessagesWithNestedFolders() {
        mockServer.prepareMailBox("INBOX.deleteme2", new MimeMessageBuilder().mock());

        uut.deleteFolder("INBOX.deleteme2");

        assertTrue(uut.findById("INBOX.deleteme2") == null);
        assertEquals("INBOX.Trash.deleteme2", uut.findById("INBOX.Trash.deleteme2").getId());
        mockServer.verifyMessageCount("INBOX.Trash.deleteme2", 1);
    }

    @Test
    public void testDeleteFolder_withMessagesWithNestedFolders() {
        MimeMessage msg = new MimeMessageBuilder().mock();

        mockServer.createAndSubscribeMailBox("INBOX.Trash");

        mockServer.prepareMailBox("INBOX.test.deleteme2", msg);
        mockServer.prepareMailBox("INBOX.test.deleteme2.nested1", msg);
        mockServer.prepareMailBox("INBOX.test.deleteme2.nested2", msg);

        uut.deleteFolder("INBOX.test.deleteme2.nested1");

        mockServer.verifyMailbox("INBOX.Trash.nested1");
        mockServer.verifyMessageCount("INBOX.Trash.nested1", 1);
        mockServer.verifyMessageCount("INBOX.test.deleteme2", 1);
        mockServer.verifyMessageCount("INBOX.test.deleteme2.nested2", 1);

        uut.deleteFolder("INBOX.test.deleteme2");

        mockServer.verifyMailbox("INBOX.Trash.deleteme2");
        mockServer.verifyMessageCount("INBOX.Trash.deleteme2", 1);
        mockServer.verifyMessageCount("INBOX.Trash.deleteme2.nested2", 1);
        mockServer.verifyMessageCount("INBOX.Trash.nested1", 1);
    }

    // @Test
    // public void testFindByEditable_invalidArgument() {
    // assertEquals(0, uut.findByEditable(null).getFolderList().size());
    // }

    // @Test
    // public void testFindByEditable_false() {
    // MailFolderList findByEditable = uut.findByEditable(false);
    //
    // for (MailFolder mf : findByEditable.getFolderList()) {
    // System.out.println(mf.getId());
    // }
    //
    // System.out.println(findByEditable.getFolderList().size());
    //
    // }

    //
    // @Test
    // public void testFindBySubscribed() {
    // fail("Not yet implemented");
    // }
    //

    //
    // @Test
    // public void testFindById() {
    // fail("Not yet implemented");
    // }
    //

}
