package com.bacecek.translate.mvp.choose_language;

import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entity.Language;
import io.realm.RealmResults;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 06/05/2017.
 * <buzmakov.da@gmail.com>
 */

public class ChooseLanguageInteractor {
	private RealmController mRealmController;

	@Inject
	public ChooseLanguageInteractor(RealmController realmController) {
		mRealmController = realmController;
	}

	public RealmResults<Language> getRecentlyUsedLanguages() {
		return mRealmController.getRecentlyUsedLanguages();
	}

	public RealmResults<Language> getAllLanguages() {
		return mRealmController.getLanguagesAsync();
	}
}
