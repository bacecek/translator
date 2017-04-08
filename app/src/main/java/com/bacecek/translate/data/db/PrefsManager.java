package com.bacecek.translate.data.db;

import android.content.Context;
import android.content.SharedPreferences;
import com.bacecek.translate.utils.Consts;

/**
 * Created by Denis Buzmakov on 07/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class PrefsManager {
	private static PrefsManager mInstance;
	private static volatile Context mContext;
	private SharedPreferences mPrefsLangs;

	public static synchronized PrefsManager getInstance() {
		if (mContext == null) {
			throw new IllegalStateException("Call `PrefsManager.init(Context)` before calling this method.");
		}

		if(mInstance == null) {
			mInstance = new PrefsManager();
		}
		return mInstance;
	}

	public static void init(Context context) {
		mContext = context.getApplicationContext();
	}

	private PrefsManager() {
		mPrefsLangs = mContext.getSharedPreferences(Consts.PREFS_LANGS_KEY, Context.MODE_PRIVATE);
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
}
