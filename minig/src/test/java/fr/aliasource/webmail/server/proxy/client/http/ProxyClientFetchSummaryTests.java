/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 2.0
 *
 * The contents of this file are subject to the GNU General Public
 * License Version 2 or later (the "GPL").
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is
 *   MiniG.org project members
 *
 * ***** END LICENSE BLOCK ***** */

package fr.aliasource.webmail.server.proxy.client.http;

import java.util.List;

import fr.aliasource.webmail.client.shared.CloudyFolder;

public class ProxyClientFetchSummaryTests extends ProxyClientTestCase {

	public void testListSubscribed() {
		// warm the cache
		List<CloudyFolder> l = ac.getFolderService().fetchSummary();
		assertNotNull(l);
		assertTrue(l.size() > 0);

		long time = System.currentTimeMillis();
		int count = 1000;
		for (int i = 0; i < count; i++) {
			ac.getFolderService().fetchSummary();
		}
		time = System.currentTimeMillis() - time;
		int msForOneCall = (int) (time / count);
		int summaryPerSecond = 1000 / msForOneCall;
		System.err.println("perf report: " + msForOneCall
				+ "ms for one summary, running at " + summaryPerSecond
				+ " fetch/s.");

		assertNotNull(l);
	}

}
