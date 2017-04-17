package com.bacecek.translate.data.db;

import android.content.SharedPreferences;
import com.bacecek.translate.util.Consts.DI;
import com.bacecek.translate.util.Consts.PrefsLangs;
import com.bacecek.translate.util.Consts.PrefsSettings;
import java.util.Locale;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by Denis Buzmakov on 07/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class PrefsManager {
	private SharedPreferences mPrefsLangs;
	private SharedPreferences mPrefsSettings;

	@Inject
	public PrefsManager(
			@Named(DI.DI_PREFS_LANGS)SharedPreferences prefsLangs,
			@Named(DI.DI_PREFS_SETTINGS) SharedPreferences prefsSettings) {
		mPrefsLangs = prefsLangs;
		mPrefsSettings = prefsSettings;
	}

	public String getLastUsedOriginalLang() {
		return mPrefsLangs.getString(PrefsLangs.ORIGINAL_LANG_KEY, PrefsLangs.DEFAULT_ORIGINAL_LANG);
	}

	public String getLastUsedTargetLang() {
		return mPrefsLangs.getString(PrefsLangs.TARGET_LANG_KEY, PrefsLangs.DEFAULT_TARGET_LANG);
	}

	public void setLastUsedOriginalLang(String code) {
		mPrefsLangs.edit()
				.putString(PrefsLangs.ORIGINAL_LANG_KEY, code)
				.apply();
	}

	public void setLastUsedTargetLang(String code) {
		mPrefsLangs.edit()
				.putString(PrefsLangs.TARGET_LANG_KEY, code)
				.apply();
	}

	public String getSavedSystemLocale() {
		return mPrefsLangs.getString(PrefsLangs.LOCALE, Locale.getDefault().getLanguage());
	}

	public void saveSystemLocale() {
		mPrefsLangs.edit()
				.putString(PrefsLangs.LOCALE, Locale.getDefault().getLanguage())
				.apply();
	}

	public boolean showDictionary() {
		return mPrefsSettings.getBoolean(PrefsSettings.SHOW_DICTIONARY, true);
	}

	public boolean returnForTranslate() {
		return mPrefsSettings.getBoolean(PrefsSettings.RETURN, false);
	}

	public boolean simultaneousTranslation() {
		return mPrefsSettings.getBoolean(PrefsSettings.SIMULTANEOUS_TRANSLATION, true);
	}

	public void setShowDictionary(boolean value) {
		mPrefsSettings.edit()
				.putBoolean(PrefsSettings.SHOW_DICTIONARY, value)
				.apply();
	}

	public void setReturnForTranslate(boolean value) {
		mPrefsSettings.edit()
				.putBoolean(PrefsSettings.RETURN, value)
				.apply();
	}

	public void setSimultaneousTranslation(boolean value) {
		mPrefsSettings.edit()
				.putBoolean(PrefsSettings.SIMULTANEOUS_TRANSLATION, value)
				.apply();
	}
}
