package org.minig.server.service.impl.helper;

import javax.mail.Folder;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.minig.server.MailFolder;
import org.minig.server.service.FolderBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class FolderMapperTest {

	private FolderMapper uut = new FolderMapper();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test(expected = IllegalArgumentException.class)
	public void testToMailFolder_invalidArguments() {
		uut.toMailFolder(null);
	}

	@Test
	public void testToMailFolder_NpeTest() {
		FolderBuilder builder = new FolderBuilder();
		Folder mock = builder.setFullName(null).setName(null).setParent(null)
				.mock();

		MailFolder mf = uut.toMailFolder(mock);

		assertNull(mf.getId());
		assertNull(mf.getName());
		assertNull(mf.getParentFolderId());
		assertNull(mf.getPath());
		assertFalse(mf.getSubscribed());
	}

	@Test
	public void testToMailFolder_hasNoParent() {
		FolderBuilder builder = new FolderBuilder();
		Folder mock = builder.mock();

		MailFolder mf = uut.toMailFolder(mock);

		assertEquals(builder.getFullName(), mf.getId());
		assertEquals(builder.getName(), mf.getName());
		assertNull(mf.getParentFolderId());
		assertEquals(builder.isSubscribed(), mf.getSubscribed());
		assertEquals(builder.getFullName(), mf.getPath());
	}

	@Test
	public void testToMailFolder_hasParent() {
		FolderBuilder builder = new FolderBuilder();
		Folder mock = builder.setParent("parentFolder").mock();

		MailFolder mf = uut.toMailFolder(mock);

		assertEquals(builder.getFullName(), mf.getId());
		assertEquals(builder.getName(), mf.getName());
		assertEquals(builder.getParent(), mf.getParentFolderId());
		assertEquals(builder.isSubscribed(), mf.getSubscribed());
		assertEquals(builder.getFullName(), mf.getPath());
	}

}
