package org.minig.server.service;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.minig.server.service.impl.AttachmentRepositoryImplTest;
import org.minig.server.service.impl.FolderRepositoryImplTest;
import org.minig.server.service.impl.MailRepositoryImplTest;
import org.minig.server.service.impl.helper.FolderMapperTest;
import org.minig.server.service.impl.helper.MessageMapperTest;

@RunWith(Suite.class)
@SuiteClasses({ MessageMapperTest.class, FolderMapperTest.class, MailRepositoryImplTest.class, FolderRepositoryImplTest.class,
		AttachmentRepositoryImplTest.class })
public class RepositoryTestSuite {

}
