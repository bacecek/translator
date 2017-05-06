package com.bacecek.translate.mvp.interactor;

import com.bacecek.translate.data.db.PrefsManager;
import com.bacecek.translate.data.db.RealmController;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 06/05/2017.
 * <buzmakov.da@gmail.com>
 */

public class SettingsInteractor {
	private PrefsManager mPrefsManager;
	private RealmController mRealmController;

	@Inject
	public SettingsInteractor(PrefsManager prefsManager,
			RealmController realmController) {
		mPrefsManager = prefsManager;
		mRealmController = realmController;
	}

	public boolean getSettingSimultaneousTranslation() {
		return mPrefsManager.simultaneousTranslation();
	}

	public void setSettingSimultaneousTranslation(boolean value) {
		mPrefsManager.setSimultaneousTranslation(value);
	}

	public boolean getSettingShowDictionary() {
		return mPrefsManager.showDictionary();
	}

	public void setSettingShowDictionary(boolean value) {
		mPrefsManager.setShowDictionary(value);
	}

	public boolean getSettingReturnForTranslate() {
		return mPrefsManager.returnForTranslate();
	}

	public void setSettingReturnForTranslate(boolean value) {
		mPrefsManager.setReturnForTranslate(value);
	}

	public void clearHistory() {
		mRealmController.clearHistory();
	}
}
