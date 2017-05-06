package com.bacecek.translate.mvp.interactor;

import com.bacecek.translate.data.db.PrefsManager;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entity.Language;
import com.bacecek.translate.data.network.api.TranslatorAPI;
import io.reactivex.Observable;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 05/05/2017.
 * <buzmakov.da@gmail.com>
 */

public class SplashScreenInteractor {
	private RealmController mRealmController;
	private PrefsManager mPrefsManager;
	private TranslatorAPI mAPI;

	@Inject
	public SplashScreenInteractor(
			RealmController realmController,
			PrefsManager prefsManager,
			TranslatorAPI api) {
		mRealmController = realmController;
		mPrefsManager = prefsManager;
		mAPI = api;
	}

	public Observable<List<Language>> getLangs() {
		return mAPI.getLangs(Locale.getDefault().getLanguage());
	}

	public void saveLangs(List<Language> langs) {
		mRealmController.insertLanguages(langs);
		mPrefsManager.saveSystemLocale();
		mRealmController.updateTimestampLanguage(mPrefsManager.getLastUsedOriginalLang());
		mRealmController.updateTimestampLanguage(mPrefsManager.getLastUsedTargetLang());
	}

	public boolean isLoadingLangsNeeded() {
		int langsCount = mRealmController.getLanguages().size();
		return langsCount == 0 ||
				!mPrefsManager.getSavedSystemLocale().equals(Locale.getDefault().getLanguage());
	}
}
