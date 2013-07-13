package fr.aliasource.webmail.server.proxy.client.http;

import fr.aliasource.webmail.client.shared.VacationInfo;

public class ProxyClientFilterTests extends ProxyClientTestCase {

	public void testFetchVacation() {
		VacationInfo vi = ac.fetchVacation();
		assertNotNull(vi);
	}

}
