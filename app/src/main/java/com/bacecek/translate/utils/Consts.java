package com.bacecek.translate.utils;

/**
 * Created by Denis Buzmakov on 04/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class Consts {
	public static final int MAX_LISTEN_SYMBOLS = 10000;
	public static final int DELAY_INPUT = 700;
	public static final int RECOGNITION_REQUEST_CODE = 777;
	public static final int CHOOSE_LANG_REQUEST_CODE = 666;
	public static final String EXTRA_FULLSCREEN = "extra_fullscreen";
	public static final int MIN_TEXTSIZE_FULLSCREEN = 25;
	public static final String PREFS_LANGS_KEY = "prefs_langs";
	public static final String PREFS_SETTINGS_KEY = "prefs_settings";
	public static final String PREFS_ORIGINAL_LANG_KEY = "prefs_original_lang";
	public static final String PREFS_TARGET_LANG_KEY = "prefs_langs";
	public static final String PREFS_DEFAULT_ORIGINAL_LANG = "ru";
	public static final String PREFS_DEFAULT_TARGET_LANG = "en";
	public static final String PREFS_LOCALE = "prefs_locale";
	public static final String EXTRA_CHOOSE_LANG_CURRENT = "extra_choose_lang_current";
	public static final String EXTRA_CHOOSE_LANG_TYPE = "extra_choose_lang_type";
	public static final String EXTRA_CHOOSE_LANG_RETURN = "extra_chosen_lang";
	public static final int CHOOSE_LANG_TYPE_ORIGINAL = 0;
	public static final int CHOOSE_LANG_TYPE_TARGET = 1;

	public static class DI {
		public static final String DI_TRANSLATOR_RETROFIT = "translator_retrofit";
		public static final String DI_TRANSLATOR_OKHTTP = "translator_okhttp";
		public static final String DI_TRANSLATOR_INTERCEPTOR = "translator_interceptor";
		public static final String DI_DICTIONARY_RETROFIT = "dictionary_retrofit";
		public static final String DI_DICTIONARY_OKHTTP = "dictionary_okhttp";
		public static final String DI_DICTIONARY_INTERCEPTOR = "dictionary_interceptor";
	}
}
