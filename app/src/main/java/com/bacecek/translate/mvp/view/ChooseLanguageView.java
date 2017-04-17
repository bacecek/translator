package com.bacecek.translate.mvp.view;

import com.arellomobile.mvp.MvpView;
import com.bacecek.translate.data.entity.Language;
import io.realm.RealmResults;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

public interface ChooseLanguageView extends MvpView {
	void showRecentlyUsed();
	void hideRecentlyUsed();
	void setRecentlyUsedLanguages(RealmResults<Language> languages);
	void setAllLanguages(RealmResults<Language> languages);
	void setResultAndFinish(String lang, int langType);
}
