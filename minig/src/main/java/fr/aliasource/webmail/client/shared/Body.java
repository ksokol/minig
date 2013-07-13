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

package fr.aliasource.webmail.client.shared;

import java.io.Serializable;

public class Body implements Serializable {

	private static final long serialVersionUID = -7930597590298852484L;

	private String html;
	private String cleanHtml;
	private String partialCleanHtml;
	private String plain;
	private boolean isTruncated;

	private boolean withSignature;

	public Body() {
		this("text/plain", "");
	}

	public Body(String mime, String value) {
		if ("text/plain".equals(mime)) {
			this.plain = value;
		} else if ("text/html".equals(mime)) {
			this.html = value;
		} else if ("text/cleanHtml".equals(mime)) {
			this.cleanHtml = value;
		} else if ("text/partialCleanHtml".equals(mime)) {
			this.partialCleanHtml = value;
		}
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public String getCleanHtml() {
		return cleanHtml;
	}

	public void setCleanHtml(String cleanHtml) {
		this.cleanHtml = cleanHtml;
	}

	public String getPartialCleanHtml() {
		return partialCleanHtml;
	}

	public void setPartialCleanHtml(String partialCleanHtml) {
		this.partialCleanHtml = partialCleanHtml;
	}

	public String getPlain() {
		return plain;
	}

	public void setPlain(String plain) {
		this.plain = plain;
	}

	public boolean isWithSignature() {
		return withSignature;
	}

	public void setWithSignature(boolean withSignature) {
		this.withSignature = withSignature;
	}

	public boolean isTruncated() {
		return isTruncated;
	}

	public void setTruncated(boolean isTruncated) {
		this.isTruncated = isTruncated;
	}

	public boolean isEmpty() {
		return plain == null || plain.length() == 0;
	}
}
