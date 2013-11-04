package org.minig.server.service.impl;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.minig.server.MailFolder;
import org.minig.server.service.MockMailAuthentication;
import org.minig.server.service.RepositoryException;
import org.minig.server.service.ServiceTestConfig;
import org.minig.server.service.SmtpAndImapMockServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ServiceTestConfig.class })
@ActiveProfiles("test")
public class FolderRepositoryImplTest {

    @Autowired
    private SmtpAndImapMockServer mockServer;

    @Autowired
    private FolderRepositoryImpl uut;

    @Autowired
    private MockMailAuthentication mockMailAuthentication;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        mockServer.reset();
        assertEquals(0, uut.findBySubscribed(true).size());
    }

    @Test
    public void testFindAll_InboxOnly() {
        List<MailFolder> findAll = uut.findAll();

        assertEquals(1, findAll.size());
        assertEquals("INBOX", findAll.get(0).getId());
    }

    @Test
    public void testFindAll_mixedSubscribedAndUnsubscribed() {
        mockServer.createAndNotSubscribeMailBox("INBOX.1");
        mockServer.createAndNotSubscribeMailBox("INBOX.1.1");
        mockServer.createAndNotSubscribeMailBox("INBOX.2");
        mockServer.createAndSubscribeMailBox("INBOX.3");
        mockServer.createAndSubscribeMailBox("INBOX.3.3");
        mockServer.createAndSubscribeMailBox("INBOX.3.3.3");
        mockServer.createAndSubscribeMailBox("INBOX.5");

        List<MailFolder> findAll = uut.findAll();

        // inclusive INBOX
        assertEquals(8, findAll.size());

        findAll = uut.findBySubscribed(null);
        assertEquals(8, findAll.size());

        findAll = uut.findBySubscribed(true);
        assertEquals(4, findAll.size());

        findAll = uut.findBySubscribed(false);
        assertEquals(4, findAll.size());
    }

    // @Test
    // public void testFindChildren_invalidArguments() {
    // mockServer.createAndNotSubscribeMailBox("INBOX/1");
    // mockServer.createAndNotSubscribeMailBox("INBOX/1/1");
    // mockServer.createAndNotSubscribeMailBox("INBOX/1/2");
    // mockServer.createAndNotSubscribeMailBox("INBOX/1/3");
    //
    // List<MailFolder> findChildren = uut.findChildren("INBOX/1");
    //
    // assertEquals(3, findChildren.size());
    // }
    //
    @Test
    public void testCreate_invalidArguments() {
        int count = 0;

        try {
            uut.create(null, "folder");
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.create("parent", null);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.create(null, null);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.create("", "folder");
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.create("    ", "folder");
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.create("parent", "");
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.create("parent", "    ");
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.create("", "");
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.create("     ", "    ");
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(9, count);
    }

    @Test
    public void testCreate_ChildrenDoesNotExist() {
        uut.create("INBOX", "child1");
        mockServer.verifyMailbox("INBOX.child1");

        uut.create("INBOX.child1", "child2");
        mockServer.verifyMailbox("INBOX.child1.child2");
    }

    @Test
    public void testCreate_ChildDoesExist() {
        mockServer.createAndSubscribeMailBox("INBOX.child1");

        assertEquals(1, uut.findBySubscribed(true).size());

        uut.create("INBOX", "child1");

        assertEquals(1, uut.findBySubscribed(true).size());
    }

    @Test(expected = RepositoryException.class)
    public void testCreate_parentDoesNotExist() {
        uut.create("VOID", "child1");
    }

    @Test
    public void testCreate_parentInFolderArgumentDoesNotExist() {
        uut.create("INBOX", "child1.child2");

        List<MailFolder> findBySubscribed = uut.findBySubscribed(true);

        assertEquals(1, findBySubscribed.size());
        assertEquals("INBOX.child1.child2", findBySubscribed.get(0).getId());
    }

    @Test
    public void testRead_invalidArguments() {
        int count = 0;

        try {
            uut.read(null);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.read("");
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.read("   ");
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(3, count);
    }

    @Test
    public void testRead() {
        MailFolder read1 = uut.read("INBOX");
        assertEquals(read1.getName(), "INBOX");

        MailFolder shouldBeNull = uut.read("VOID");
        assertEquals(null, shouldBeNull);
    }

    @Test
    public void testGetInbox() {
        assertEquals(mockMailAuthentication.getInboxFolder(), uut.getInbox().getId());
    }

    @Test
    public void testGetTrash() {
        MailFolder trash = uut.getTrash();
        assertEquals(mockMailAuthentication.getTrashFolder(), trash.getId());
    }

    @Test
    public void testGetDraft() {
        MailFolder draft = uut.getDraft();
        assertEquals(mockMailAuthentication.getDraftsFolder(), draft.getId());
    }

    @Test
    public void testGetSent() {
        MailFolder sent = uut.getSent();
        assertEquals(mockMailAuthentication.getSentFolder(), sent.getId());
    }

    @Test
    public void testDelete_invalidArguments() {
        int count = 0;

        try {
            uut.delete(null);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.delete("");
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.delete("   ");
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(3, count);
    }

    @Test
    public void testDelete_emptyFolder() {
        mockServer.createAndSubscribeMailBox("INBOX.child1");

        List<MailFolder> findBySubscribed = uut.findAll();
        assertEquals(2, findBySubscribed.size());
        assertEquals("INBOX.child1", findBySubscribed.get(1).getId());

        uut.delete("INBOX.child1");
        assertEquals(1, uut.findAll().size());
        assertEquals("INBOX", findBySubscribed.get(0).getId());
    }

    @Test
    public void testDelete_emptyFolderWithChildFolder() {
        mockServer.createAndSubscribeMailBox("INBOX.child1");
        mockServer.createAndSubscribeMailBox("INBOX.child1.child2");

        List<MailFolder> findBySubscribed = uut.findAll();

        assertThat(findBySubscribed, Matchers.hasSize(3));

        assertThat(findBySubscribed, Matchers.hasItem(Matchers.<MailFolder> hasProperty("id", Matchers.is("INBOX.child1.child2"))));

        // assertEquals("INBOX.child1.child2", findBySubscribed.get(2).getId());

        uut.delete("INBOX.child1");

        assertEquals(1, uut.findAll().size());
        assertEquals("INBOX", findBySubscribed.get(0).getId());
    }

    @Test
    public void testDelete_folderDoesNotExist() {
        List<MailFolder> findBySubscribed = uut.findAll();
        assertEquals("INBOX", findBySubscribed.get(0).getId());

        uut.delete("INBOX.child1");
        assertEquals(1, uut.findAll().size());
        assertEquals("INBOX", findBySubscribed.get(0).getId());
    }

    @Test
    public void testUpdate_invalidArguments() {
        int count = 0;
        try {
            uut.update(null);
        } catch (IllegalArgumentException e) {
            count++;
        }

        try {
            uut.update(new MailFolder());
        } catch (IllegalArgumentException e) {
            count++;
        }

        assertEquals(2, count);
    }

    @Test
    public void testUpdate_noModification() {
        mockServer.createAndNotSubscribeMailBox("INBOX.test");

        List<MailFolder> findBySubscribed = uut.findBySubscribed(false);

        assertThat(findBySubscribed, Matchers.hasSize(2));
        assertThat(findBySubscribed, Matchers.hasItem(Matchers.<MailFolder> hasProperty("id", Matchers.is("INBOX.test"))));
        assertThat(findBySubscribed, Matchers.everyItem(Matchers.<MailFolder> hasProperty("subscribed", Matchers.is(false))));

        MailFolder mailFolder = null;

        for (MailFolder mf : findBySubscribed) {
            if (mf.getId().equals("INBOX.test")) {
                mailFolder = mf;
                break;
            }
        }

        assertEquals("INBOX.test", mailFolder.getId());
        assertFalse(mailFolder.getSubscribed());

        mailFolder.setSubscribed(null);

        uut.update(mailFolder);

        findBySubscribed = uut.findBySubscribed(false);

        assertThat(findBySubscribed, Matchers.hasSize(2));
        assertThat(findBySubscribed, Matchers.hasItem(Matchers.<MailFolder> hasProperty("id", Matchers.is("INBOX.test"))));
        assertThat(findBySubscribed, Matchers.everyItem(Matchers.<MailFolder> hasProperty("subscribed", Matchers.is(false))));
    }

    @Test
    public void testUpdate_subscribe() {
        mockServer.createAndNotSubscribeMailBox("INBOX.test");

        List<MailFolder> findBySubscribed = uut.findBySubscribed(false);

        assertThat(findBySubscribed, Matchers.hasSize(2));
        assertThat(findBySubscribed, Matchers.hasItem(Matchers.<MailFolder> hasProperty("id", Matchers.is("INBOX.test"))));
        assertThat(findBySubscribed, Matchers.everyItem(Matchers.<MailFolder> hasProperty("subscribed", Matchers.is(false))));

        MailFolder mailFolder = null;

        for (MailFolder mf : findBySubscribed) {
            if (mf.getId().equals("INBOX.test")) {
                mailFolder = mf;
                break;
            }
        }

        mailFolder.setSubscribed(true);

        uut.update(mailFolder);

        mailFolder = uut.findBySubscribed(true).get(0);

        assertEquals("INBOX.test", mailFolder.getId());
        assertTrue(mailFolder.getSubscribed());
    }
}
