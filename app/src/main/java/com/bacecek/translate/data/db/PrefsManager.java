package com.bacecek.translate.data.db;

import android.content.Context;
import android.content.SharedPreferences;
import com.bacecek.translate.utils.Consts;
import java.util.Locale;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 07/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class PrefsManager {
	private SharedPreferences mPrefsLangs;

	@Inject
	public PrefsManager(Context context) {
		mPrefsLangs = context.getSharedPreferences(Consts.PREFS_LANGS_KEY, Context.MODE_PRIVATE);
	}

	public String getLastUsedOriginalLang() {
		return mPrefsLangs.getString(Consts.PREFS_ORIGINAL_LANG_KEY, Consts.PREFS_DEFAULT_ORIGINAL_LANG);
	}

	public String getLastUsedTargetLang() {
		return mPrefsLangs.getString(Consts.PREFS_TARGET_LANG_KEY, Consts.PREFS_DEFAULT_TARGET_LANG);
	}

	public void setLastUsedOriginalLang(String code) {
		mPrefsLangs.edit()
				.putString(Consts.PREFS_ORIGINAL_LANG_KEY, code)
				.apply();
	}

	public void setLastUsedTargetLang(String code) {
		mPrefsLangs.edit()
				.putString(Consts.PREFS_TARGET_LANG_KEY, code)
				.apply();
	}

	public String getSavedSystemLocale() {
		return mPrefsLangs.getString(Consts.PREFS_LOCALE, Locale.getDefault().getLanguage());
	}

	public void saveSystemLocale() {
		mPrefsLangs.edit()
				.putString(Consts.PREFS_LOCALE, Locale.getDefault().getLanguage())
				.apply();
	}
}
