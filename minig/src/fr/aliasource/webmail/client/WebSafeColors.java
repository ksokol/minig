package fr.aliasource.webmail.client;

import java.util.HashMap;
import java.util.Map;

import fr.aliasource.webmail.client.shared.Folder;

/**
 * http://www.w3schools.com/Html/html_colors.asp
 * 
 * 215 colors, white & black excluded.
 * 
 * @author tom
 * 
 */
public class WebSafeColors {

	private static Map<Integer, String> colors;

	static {
		int idx = 0;
		colors = new HashMap<Integer, String>();

		colors.put(idx++, "#000033"); // 0
		colors.put(idx++, "#000066");
		colors.put(idx++, "#000099");
		colors.put(idx++, "#0000cc");
		colors.put(idx++, "#0000ff");
		colors.put(idx++, "#003300");
		colors.put(idx++, "#003333");
		colors.put(idx++, "#003366");
		colors.put(idx++, "#003399");
		colors.put(idx++, "#0033cc");
		colors.put(idx++, "#0033ff");
		colors.put(idx++, "#006600");
		colors.put(idx++, "#006633");
		colors.put(idx++, "#006666");
		colors.put(idx++, "#006699");
		colors.put(idx++, "#0066cc");
		colors.put(idx++, "#0066ff");
		colors.put(idx++, "#009900");
		colors.put(idx++, "#009933");
		colors.put(idx++, "#009966");
		colors.put(idx++, "#009999");
		colors.put(idx++, "#0099cc");
		colors.put(idx++, "#0099ff");
		colors.put(idx++, "#00cc00");
		colors.put(idx++, "#00cc33");
		colors.put(idx++, "#00cc66");
		colors.put(idx++, "#00cc99");
		colors.put(idx++, "#00cccc");
		colors.put(idx++, "#00ccff");
		colors.put(idx++, "#00ff00");
		colors.put(idx++, "#00ff33");
		colors.put(idx++, "#00ff66");
		colors.put(idx++, "#00ff99");
		colors.put(idx++, "#00ffcc");
		colors.put(idx++, "#00ffff");
		colors.put(idx++, "#330000");
		colors.put(idx++, "#330033");
		colors.put(idx++, "#330066");
		colors.put(idx++, "#330099");
		colors.put(idx++, "#3300cc");
		colors.put(idx++, "#3300ff");
		colors.put(idx++, "#333300");
		colors.put(idx++, "#333333");
		colors.put(idx++, "#333366");
		colors.put(idx++, "#333399");
		colors.put(idx++, "#3333cc");
		colors.put(idx++, "#3333ff");
		colors.put(idx++, "#336600");
		colors.put(idx++, "#336633");
		colors.put(idx++, "#336666");
		colors.put(idx++, "#336699");
		colors.put(idx++, "#3366cc");
		colors.put(idx++, "#3366ff");
		colors.put(idx++, "#339900");
		colors.put(idx++, "#339933");
		colors.put(idx++, "#339966");
		colors.put(idx++, "#339999");
		colors.put(idx++, "#3399cc");
		colors.put(idx++, "#3399ff");
		colors.put(idx++, "#33cc00");
		colors.put(idx++, "#33cc33");
		colors.put(idx++, "#33cc66");
		colors.put(idx++, "#33cc99");
		colors.put(idx++, "#33cccc");
		colors.put(idx++, "#33ccff");
		colors.put(idx++, "#33ff00");
		colors.put(idx++, "#33ff33");
		colors.put(idx++, "#33ff66");
		colors.put(idx++, "#33ff99");
		colors.put(idx++, "#33ffcc");
		colors.put(idx++, "#33ffff");
		colors.put(idx++, "#660000");
		colors.put(idx++, "#660033");
		colors.put(idx++, "#660066");
		colors.put(idx++, "#660099");
		colors.put(idx++, "#6600cc");
		colors.put(idx++, "#6600ff");
		colors.put(idx++, "#663300");
		colors.put(idx++, "#663333");
		colors.put(idx++, "#663366");
		colors.put(idx++, "#663399");
		colors.put(idx++, "#6633cc");
		colors.put(idx++, "#6633ff");
		colors.put(idx++, "#666600");
		colors.put(idx++, "#666633");
		colors.put(idx++, "#666666");
		colors.put(idx++, "#666699");
		colors.put(idx++, "#6666cc");
		colors.put(idx++, "#6666ff");
		colors.put(idx++, "#669900");
		colors.put(idx++, "#669933");
		colors.put(idx++, "#669966");
		colors.put(idx++, "#669999");
		colors.put(idx++, "#6699cc");
		colors.put(idx++, "#6699ff");
		colors.put(idx++, "#66cc00");
		colors.put(idx++, "#66cc33");
		colors.put(idx++, "#66cc66");
		colors.put(idx++, "#66cc99");
		colors.put(idx++, "#66cccc");
		colors.put(idx++, "#66ccff");
		colors.put(idx++, "#66ff00");
		colors.put(idx++, "#66ff33");
		colors.put(idx++, "#66ff66");
		colors.put(idx++, "#66ff99");
		colors.put(idx++, "#66ffcc");
		colors.put(idx++, "#66ffff");
		colors.put(idx++, "#990000");
		colors.put(idx++, "#990033");
		colors.put(idx++, "#990066");
		colors.put(idx++, "#990099");
		colors.put(idx++, "#9900cc");
		colors.put(idx++, "#9900ff");
		colors.put(idx++, "#993300");
		colors.put(idx++, "#993333");
		colors.put(idx++, "#993366");
		colors.put(idx++, "#993399");
		colors.put(idx++, "#9933cc");
		colors.put(idx++, "#9933ff");
		colors.put(idx++, "#996600");
		colors.put(idx++, "#996633");
		colors.put(idx++, "#996666");
		colors.put(idx++, "#996699");
		colors.put(idx++, "#9966cc");
		colors.put(idx++, "#9966ff");
		colors.put(idx++, "#999900");
		colors.put(idx++, "#999933");
		colors.put(idx++, "#999966");
		colors.put(idx++, "#999999");
		colors.put(idx++, "#9999cc");
		colors.put(idx++, "#9999ff");
		colors.put(idx++, "#99cc00");
		colors.put(idx++, "#99cc33");
		colors.put(idx++, "#99cc66");
		colors.put(idx++, "#99cc99");
		colors.put(idx++, "#99cccc");
		colors.put(idx++, "#99ccff");
		colors.put(idx++, "#99ff00");
		colors.put(idx++, "#99ff33");
		colors.put(idx++, "#99ff66");
		colors.put(idx++, "#99ff99");
		colors.put(idx++, "#99ffcc");
		colors.put(idx++, "#99ffff");
		colors.put(idx++, "#cc0000");
		colors.put(idx++, "#cc0033");
		colors.put(idx++, "#cc0066");
		colors.put(idx++, "#cc0099");
		colors.put(idx++, "#cc00cc");
		colors.put(idx++, "#cc00ff");
		colors.put(idx++, "#cc3300");
		colors.put(idx++, "#cc3333");
		colors.put(idx++, "#cc3366");
		colors.put(idx++, "#cc3399");
		colors.put(idx++, "#cc33cc");
		colors.put(idx++, "#cc33ff");
		colors.put(idx++, "#cc6600");
		colors.put(idx++, "#cc6633");
		colors.put(idx++, "#cc6666");
		colors.put(idx++, "#cc6699");
		colors.put(idx++, "#cc66cc");
		colors.put(idx++, "#cc66ff");
		colors.put(idx++, "#cc9900");
		colors.put(idx++, "#cc9933");
		colors.put(idx++, "#cc9966");
		colors.put(idx++, "#cc9999");
		colors.put(idx++, "#cc99cc");
		colors.put(idx++, "#cc99ff");
		colors.put(idx++, "#cccc00");
		colors.put(idx++, "#cccc33");
		colors.put(idx++, "#cccc66");
		colors.put(idx++, "#cccc99");
		colors.put(idx++, "#cccccc");
		colors.put(idx++, "#ccccff");
		colors.put(idx++, "#ccff00");
		colors.put(idx++, "#ccff33");
		colors.put(idx++, "#ccff66");
		colors.put(idx++, "#ccff99");
		colors.put(idx++, "#ccffcc");
		colors.put(idx++, "#ccffff");
		colors.put(idx++, "#ff0000");
		colors.put(idx++, "#ff0033");
		colors.put(idx++, "#ff0066");
		colors.put(idx++, "#ff0099");
		colors.put(idx++, "#ff00cc");
		colors.put(idx++, "#ff00ff");
		colors.put(idx++, "#ff3300");
		colors.put(idx++, "#ff3333");
		colors.put(idx++, "#ff3366");
		colors.put(idx++, "#ff3399");
		colors.put(idx++, "#ff33cc");
		colors.put(idx++, "#ff33ff");
		colors.put(idx++, "#ff6600");
		colors.put(idx++, "#ff6633");
		colors.put(idx++, "#ff6666");
		colors.put(idx++, "#ff6699");
		colors.put(idx++, "#ff66cc");
		colors.put(idx++, "#ff66ff");
		colors.put(idx++, "#ff9900");
		colors.put(idx++, "#ff9933");
		colors.put(idx++, "#ff9966");
		colors.put(idx++, "#ff9999");
		colors.put(idx++, "#ff99cc");
		colors.put(idx++, "#ff99ff");
		colors.put(idx++, "#ffcc00");
		colors.put(idx++, "#ffcc33");
		colors.put(idx++, "#ffcc66");
		colors.put(idx++, "#ffcc99");
		colors.put(idx++, "#ffcccc");
		colors.put(idx++, "#ffccff");
		colors.put(idx++, "#ffff00");
		colors.put(idx++, "#ffff33");
		colors.put(idx++, "#ffff66");
		colors.put(idx++, "#ffff99");
		colors.put(idx++, "#ffffcc"); // 214
		colors.put(idx++, "#ffffcc"); // 215
	}

	public static String htmlColor(Folder f) {
		String s = f.getName().toLowerCase();
		int col = Math.abs(s.hashCode()) % (colors.size());
		return colors.get(col);
	}

	public static String fgColor(Folder f) {
		String col = htmlColor(f);
		char c = col.charAt(3);
		if (c == '9' || c == ('c') || c == 'f') {
			return "black";
		} else {
			return "white";
		}
	}
}
