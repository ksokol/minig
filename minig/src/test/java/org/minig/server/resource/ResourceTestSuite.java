package org.minig.server.resource;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.minig.server.resource.attachment.AttachmentResourceTest;
import org.minig.server.resource.folder.FolderResourceTest;
import org.minig.server.resource.mail.MailResourceTest;

@RunWith(Suite.class)
@SuiteClasses({ MailResourceTest.class, FolderResourceTest.class,
		AttachmentResourceTest.class })
public class ResourceTestSuite {

}
