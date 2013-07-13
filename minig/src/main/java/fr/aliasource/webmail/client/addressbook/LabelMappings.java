package fr.aliasource.webmail.client.addressbook;

import java.util.HashMap;
import java.util.Map;

import fr.aliasource.webmail.client.I18N;

public class LabelMappings {

	private static Map<String, String> lblTransMap = new HashMap<String, String>();

	static {
		lblTransMap.put("HOME;VOICE;X-OBM-Ref1", I18N.strings.homeVoice());
		lblTransMap.put("WORK;VOICE;X-OBM-Ref1", I18N.strings.workVoice());
		lblTransMap.put("OTHER;VOICE;X-OBM-Ref1", I18N.strings.otherVoice());
		lblTransMap.put("HOME;FAX;X-OBM-Ref1", I18N.strings.homeFax());
		lblTransMap.put("WORK;FAX;X-OBM-Ref1", I18N.strings.workFax());
		lblTransMap.put("CELL;VOICE;X-OBM-Ref1", I18N.strings.cellVoice());

		lblTransMap.put("WORK;X-OBM-Ref1", I18N.strings.workAddress());
		lblTransMap.put("HOME;X-OBM-Ref1", I18N.strings.homeAddress());
		lblTransMap.put("OTHER;X-OBM-Ref1", I18N.strings.otherAddress());
	}

	public static String i18n(String lbl) {
		return lblTransMap.get(lbl);
	}

}
