package com.bacecek.translate.utils;

/**
 * Created by Denis Buzmakov on 04/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class Consts {
	public static final int MAX_VOCALIZE_SYMBOLS = 10000;
	public static final int DELAY_INPUT = 700;
	public static final int RECOGNITION_REQUEST_CODE = 777;
	public static final int CHOOSE_LANG_REQUEST_CODE = 666;
	public static final int MIN_TEXTSIZE_FULLSCREEN = 25;
	public static final int CHOOSE_LANG_TYPE_ORIGINAL = 0;
	public static final int CHOOSE_LANG_TYPE_TARGET = 1;

	public static class DI {
		public static final String DI_TRANSLATOR_RETROFIT = "translator_retrofit";
		public static final String DI_TRANSLATOR_OKHTTP = "translator_okhttp";
		public static final String DI_TRANSLATOR_INTERCEPTOR = "translator_interceptor";
		public static final String DI_DICTIONARY_RETROFIT = "dictionary_retrofit";
		public static final String DI_DICTIONARY_OKHTTP = "dictionary_okhttp";
		public static final String DI_DICTIONARY_INTERCEPTOR = "dictionary_interceptor";
		public static final String DI_PREFS_LANGS = "prefs_langs";
		public static final String DI_PREFS_SETTINGS = "prefs_settings";
	}

	public static class PrefsLangs {
		public static final String KEY = "prefs_langs";
		public static final String ORIGINAL_LANG_KEY = "prefs_original_lang";
		public static final String TARGET_LANG_KEY = "prefs_langs";
		public static final String DEFAULT_ORIGINAL_LANG = "ru";
		public static final String DEFAULT_TARGET_LANG = "en";
		public static final String LOCALE = "prefs_locale";
	}

	public static class PrefsSettings {
		public static final String KEY = "prefs_settings";
		public static final String SHOW_DICTIONARY = "settings_show_dictionary";
		public static final String RETURN = "settings_return";
		public static final String SIMULTANEOUS_TRANSLATION = "settings_simultaneous_translation";
	}

	public static class Extra {
		public static final String EXTRA_CHOOSE_LANG_CURRENT = "extra_choose_lang_current";
		public static final String EXTRA_CHOOSE_LANG_TYPE = "extra_choose_lang_type";
		public static final String EXTRA_CHOOSE_LANG_RETURN = "extra_chosen_lang";
		public static final String EXTRA_FULLSCREEN = "extra_fullscreen";
	}

	public static class Settings {
		public static final int SETTINGS_COUNT = 3;
		public static final int SETTING_SIMULTANEOUS = 0;
		public static final int SETTING_DICTIONARY = 1;
		public static final int SETTING_RETURN = 2;
	}
}
