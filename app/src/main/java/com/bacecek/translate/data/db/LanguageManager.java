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
 * Хранение текущих языков в SharedPreferences - быстро и удобно.
 * Хранение всех языков в Realm.
 */
public class LanguageManager {
	private String mCurrentOriginalLangCode;
	private String mCurrentTargetLangCode;
	//достаточно тупо. сделано так, потому что изменение/добавление языков вряд ли планируется, плюсом это быстрее и удобнее, чем через SpeechKit
	private final String[] mAvailableRecognition = {"ru", "en", "tr", "uk"};
	private OnChangeLanguageListener mListener;
	private PrefsManager mPrefsManager;
	private RealmController mRealmController;

	@Inject
	public LanguageManager(PrefsManager prefsManager, RealmController realmController) {
		mPrefsManager = prefsManager;
		mRealmController = realmController;
		mCurrentOriginalLangCode = mPrefsManager.getLastUsedOriginalLang();
		mCurrentTargetLangCode = mPrefsManager.getLastUsedTargetLang();
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
			if(mListener != null) {
				mListener.onChangeOriginalLang(getCurrentOriginalLanguage());
			}
			mRealmController.updateTimestampLanguage(mCurrentOriginalLangCode);
		}
	}

	public void setCurrentTargetLangCode(String targetLangCode) {
		if(targetLangCode.equals(mCurrentOriginalLangCode)) {
			swapLanguages();
		} else {
			mCurrentTargetLangCode = targetLangCode;
			saveTargetLanguage();
			if(mListener != null) {
				mListener.onChangeTargetLang(getCurrentTargetLanguage());
			}
			mRealmController.updateTimestampLanguage(mCurrentTargetLangCode);
		}
	}

	public String getCurrentTargetLangCode() {
		return mCurrentTargetLangCode;
	}

	public Language getCurrentOriginalLanguage() {
		return mRealmController.getLanguageByCode(mCurrentOriginalLangCode);
	}

	public Language getCurrentTargetLanguage() {
		return mRealmController.getLanguageByCode(mCurrentTargetLangCode);
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
		if(mListener != null){
			mListener.onChangeOriginalLang(getCurrentOriginalLanguage());
			mListener.onChangeTargetLang(getCurrentTargetLanguage());
		}
		mRealmController.updateTimestampLanguage(mCurrentOriginalLangCode);
		mRealmController.updateTimestampLanguage(mCurrentTargetLangCode);
	}

	private void saveOriginalLanguage() {
		mPrefsManager.setLastUsedOriginalLang(mCurrentOriginalLangCode);
	}

	private void saveTargetLanguage() {
		mPrefsManager.setLastUsedTargetLang(mCurrentTargetLangCode);
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
