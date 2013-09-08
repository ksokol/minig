package org.minig.server.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.minig.server.service.impl.AttachmentRepositoryImplTest;
import org.minig.server.service.impl.FolderServiceImplTest;
import org.minig.server.service.impl.MailServiceImplTest;

@RunWith(Suite.class)
@SuiteClasses({ MailServiceImplTest.class, FolderServiceImplTest.class, AttachmentRepositoryImplTest.class })
public class ServiceTestSuite {

}
