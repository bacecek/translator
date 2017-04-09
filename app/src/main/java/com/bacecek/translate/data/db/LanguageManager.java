package com.bacecek.translate.data.db;

import com.bacecek.translate.data.entities.Language;
import java.util.Arrays;

/**
 * Created by Denis Buzmakov on 08/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class LanguageManager {
	private static LanguageManager mInstance;
	private String mCurrentOriginalLangCode;
	private String mCurrentTargetLangCode;
	private final String[] mAvailableRecognition = {"ru", "en", "tr", "uk"};
	private OnChangeLanguageListener mListener;

	public static synchronized LanguageManager getInstance() {
		if(mInstance == null) {
			mInstance = new LanguageManager();
		}
		return mInstance;
	}

	private LanguageManager() {
		mCurrentOriginalLangCode = PrefsManager.getInstance().getLastUsedOriginalLang();
		mCurrentTargetLangCode = PrefsManager.getInstance().getLastUsedTargetLang();
	}

	public String getCurrentOriginalLangCode() {
		return mCurrentOriginalLangCode;
	}

	public void setCurrentOriginalLangCode(String originalLangCode) {
		if(originalLangCode.equals(mCurrentTargetLangCode)) {
			swapLanguages();
		} else {
			mCurrentOriginalLangCode = originalLangCode;
			saveOriginalLanguage();
			mListener.onChangeOriginalLang(getCurrentOriginalLanguage());
			RealmController.getInstance().updateTimestampLanguage(mCurrentOriginalLangCode);
		}
	}

	public void setCurrentTargetLangCode(String targetLangCode) {
		if(targetLangCode.equals(mCurrentOriginalLangCode)) {
			swapLanguages();
		} else {
			mCurrentTargetLangCode = targetLangCode;
			saveTargetLanguage();
			mListener.onChangeTargetLang(getCurrentTargetLanguage());
			RealmController.getInstance().updateTimestampLanguage(mCurrentTargetLangCode);
		}
	}

	public void setCurrentOriginalLangName(String originalLangName) {
		setCurrentOriginalLangCode(RealmController.getInstance().getLanguageByName(originalLangName).getCode());
	}

	public void setCurrentTargetLangName(String targetLangName) {
		setCurrentTargetLangCode(RealmController.getInstance().getLanguageByName(targetLangName).getCode());
	}

	public String getCurrentTargetLangCode() {
		return mCurrentTargetLangCode;
	}

	public Language getCurrentOriginalLanguage() {
		return RealmController.getInstance().getLanguageByCode(mCurrentOriginalLangCode);
	}

	public Language getCurrentTargetLanguage() {
		return RealmController.getInstance().getLanguageByCode(mCurrentTargetLangCode);
	}

	public String getCurrentOriginalLangName() {
		return getCurrentOriginalLanguage().getName();
	}

	public String getCurrentTargetLangName() {
		return getCurrentTargetLanguage().getName();
	}

	public void swapLanguages() {
		String temp = mCurrentOriginalLangCode;
		mCurrentOriginalLangCode = mCurrentTargetLangCode;
		mCurrentTargetLangCode = temp;
		saveOriginalLanguage();
		saveTargetLanguage();
		mListener.onChangeOriginalLang(getCurrentOriginalLanguage());
		mListener.onChangeTargetLang(getCurrentTargetLanguage());
		RealmController.getInstance().updateTimestampLanguage(mCurrentOriginalLangCode);
		RealmController.getInstance().updateTimestampLanguage(mCurrentTargetLangCode);
	}

	private void saveOriginalLanguage() {
		PrefsManager.getInstance().setLastUsedOriginalLang(mCurrentOriginalLangCode);
	}

	private void saveTargetLanguage() {
		PrefsManager.getInstance().setLastUsedTargetLang(mCurrentTargetLangCode);
	}

	public boolean isRecognitionAndVocalizeAvailable(String langCode) {
		return Arrays.asList(mAvailableRecognition).contains(langCode);
	}

	public OnChangeLanguageListener getListener() {
		return mListener;
	}

	public void setListener(OnChangeLanguageListener listener) {
		mListener = listener;
	}

	public interface OnChangeLanguageListener {
		void onChangeOriginalLang(Language lang);
		void onChangeTargetLang(Language lang);
	}
}
