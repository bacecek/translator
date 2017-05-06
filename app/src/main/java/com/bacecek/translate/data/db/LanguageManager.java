package com.bacecek.translate.data.db;

import com.bacecek.translate.data.entity.Language;
import java.util.Arrays;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 08/04/2017.
 * <buzmakov.da@gmail.com>
 */

/**
 * Класс для управления языками в приложении.
 */
public class LanguageManager {
	private final String[] mAvailableRecognition = {"ru", "en", "tr", "uk"};
	private OnChangeLanguageListener mListener;
	private PrefsManager mPrefsManager;
	private RealmController mRealmController;

	@Inject
	public LanguageManager(PrefsManager prefsManager, RealmController realmController) {
		mPrefsManager = prefsManager;
		mRealmController = realmController;
	}

	public String getCurrentOriginalLangCode() {
		return mPrefsManager.getLastUsedOriginalLang();
	}

	public void setCurrentOriginalLangCode(String originalLangCode) {
		if(originalLangCode.equals(getCurrentTargetLangCode())) {
			swapLanguages();
		} else {
			saveOriginalLanguage(originalLangCode);
			if(mListener != null) {
				mListener.onChangeOriginalLang(getCurrentOriginalLanguage());
			}
			mRealmController.updateTimestampLanguage(originalLangCode);
		}
	}

	public void setCurrentTargetLangCode(String targetLangCode) {
		if(targetLangCode.equals(getCurrentOriginalLangCode())) {
			swapLanguages();
		} else {
			saveTargetLanguage(targetLangCode);
			if(mListener != null) {
				mListener.onChangeTargetLang(getCurrentTargetLanguage());
			}
			mRealmController.updateTimestampLanguage(targetLangCode);
		}
	}

	public String getCurrentTargetLangCode() {
		return mPrefsManager.getLastUsedTargetLang();
	}

	public Language getCurrentOriginalLanguage() {
		return mRealmController.getLanguageByCode(getCurrentOriginalLangCode());
	}

	public Language getCurrentTargetLanguage() {
		return mRealmController.getLanguageByCode(getCurrentTargetLangCode());
	}

	public String getCurrentOriginalLangName() {
		return getCurrentOriginalLanguage().getName();
	}

	public String getCurrentTargetLangName() {
		return getCurrentTargetLanguage().getName();
	}

	public void swapLanguages() {
		String originalLangCode = getCurrentOriginalLangCode();
		String targetLangCode = getCurrentTargetLangCode();
		saveOriginalLanguage(targetLangCode);
		saveTargetLanguage(originalLangCode);
		if(mListener != null){
			mListener.onChangeOriginalLang(getCurrentOriginalLanguage());
			mListener.onChangeTargetLang(getCurrentTargetLanguage());
		}
		mRealmController.updateTimestampLanguage(originalLangCode);
		mRealmController.updateTimestampLanguage(targetLangCode);
	}

	private void saveOriginalLanguage(String langCode) {
		mPrefsManager.setLastUsedOriginalLang(langCode);
	}

	private void saveTargetLanguage(String langCode) {
		mPrefsManager.setLastUsedTargetLang(langCode);
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

	/**
	 * Интерфейс для уведомления изменения языка
	 */
	public interface OnChangeLanguageListener {
		void onChangeOriginalLang(Language lang);
		void onChangeTargetLang(Language lang);
	}
}
