package fr.aliasource.webmail.server.proxy.client.http;

import fr.aliasource.webmail.client.shared.ServerEventKind;

public class PushTests extends ProxyClientTestCase {

	public void testPush() {
		ServerEventKind event = ac.fetchEvent();
		assertNotNull(event);

		event = ac.fetchEvent();
		assertNotNull(event);

		event = ac.fetchEvent();
		assertNotNull(event);
	}

}
