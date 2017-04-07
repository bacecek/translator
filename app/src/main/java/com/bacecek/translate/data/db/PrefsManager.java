package com.bacecek.translate.data.db;

import android.content.Context;
import android.content.SharedPreferences;
import com.bacecek.translate.utils.Consts;

/**
 * Created by Denis Buzmakov on 07/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class PrefsManager {
	public static String getLastUsedOriginalLang(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Consts.PREFS_LANGS_KEY, Context.MODE_PRIVATE);
		return prefs.getString(Consts.PREFS_ORIGINAL_LANG_KEY, Consts.PREFS_DEFAULT_ORIGINAL_LANG);
	}

	public static String getLastUsedTargetLang(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(Consts.PREFS_LANGS_KEY, Context.MODE_PRIVATE);
		return prefs.getString(Consts.PREFS_TARGET_LANG_KEY, Consts.PREFS_DEFAULT_TARGET_LANG);
	}

	public static void setLastUsedOriginalLang(Context context, String code) {
		SharedPreferences prefs = context.getSharedPreferences(Consts.PREFS_LANGS_KEY, Context.MODE_PRIVATE);
		prefs.edit()
				.putString(Consts.PREFS_ORIGINAL_LANG_KEY, code)
				.apply();
	}

	public static void setLastUsedTargetLang(Context context, String code) {
		SharedPreferences prefs = context.getSharedPreferences(Consts.PREFS_LANGS_KEY, Context.MODE_PRIVATE);
		prefs.edit()
				.putString(Consts.PREFS_TARGET_LANG_KEY, code)
				.apply();
	}
}
