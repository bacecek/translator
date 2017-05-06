package com.bacecek.translate.mvp.interactor;

import com.bacecek.translate.data.db.LanguageManager;
import com.bacecek.translate.data.db.PrefsManager;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entity.DictionaryItem;
import com.bacecek.translate.data.entity.Translation;
import com.bacecek.translate.data.network.api.DictionaryAPI;
import com.bacecek.translate.data.network.api.TranslatorAPI;
import com.bacecek.translate.data.network.util.TranslateResult;
import io.reactivex.Observable;
import io.realm.RealmResults;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 06/05/2017.
 * <buzmakov.da@gmail.com>
 */

public class TranslateInteractor {
	private LanguageManager mLanguageManager;
	private RealmController mRealmController;
	private PrefsManager mPrefsManager;
	private TranslatorAPI mTranslatorAPI;
	private DictionaryAPI mDictionaryAPI;

	@Inject
	public TranslateInteractor(LanguageManager languageManager,
			RealmController realmController, PrefsManager prefsManager,
			TranslatorAPI translatorAPI,
			DictionaryAPI dictionaryAPI) {
		mLanguageManager = languageManager;
		mRealmController = realmController;
		mPrefsManager = prefsManager;
		mTranslatorAPI = translatorAPI;
		mDictionaryAPI = dictionaryAPI;
	}

	public void saveTranslation(String originalText, String translatedText, boolean async) {
		if(async) {
			mRealmController.insertTranslationAsync(
					originalText,
					translatedText,
					mLanguageManager.getCurrentOriginalLangCode(),
					mLanguageManager.getCurrentTargetLangCode());
		} else {
			mRealmController.insertTranslation(
					originalText,
					translatedText,
					mLanguageManager.getCurrentOriginalLangCode(),
					mLanguageManager.getCurrentTargetLangCode());
		}
	}

	public Translation getTranslation(String originalText) {
		return mRealmController.getTranslation(originalText,
				mLanguageManager.getCurrentOriginalLangCode(),
				mLanguageManager.getCurrentTargetLangCode());
	}

	public void removeTranslationFromHistory(Translation translation) {
		mRealmController.removeTranslationFromHistory(translation);
	}

	public RealmResults<Translation> getHistory() {
		return mRealmController.getHistory();
	}

	public boolean getSettingSimultaneousTranslation() {
		return mPrefsManager.simultaneousTranslation();
	}

	public boolean getSettingShowDictionary() {
		return mPrefsManager.showDictionary();
	}

	public boolean getSettingReturnForTranslate() {
		return mPrefsManager.returnForTranslate();
	}

	public void changeFavourite(Translation translation) {
		mRealmController.changeFavourite(translation);
	}

	public Observable<TranslateResult> translate(String text) {
		String direction = mLanguageManager.getCurrentOriginalLangCode() + "-" + mLanguageManager.getCurrentTargetLangCode();
		Observable<Translation> translatorSingle = mTranslatorAPI.translate(text, direction);
		Observable<List<DictionaryItem>> dictionarySingle = mDictionaryAPI.translate(text, direction, mPrefsManager.getSavedSystemLocale());
		return Observable.combineLatest(translatorSingle,
				dictionarySingle, this::combine);
	}

	private TranslateResult combine(Translation translation, List<DictionaryItem> list) {
		return new TranslateResult(translation, list);
	}
}
