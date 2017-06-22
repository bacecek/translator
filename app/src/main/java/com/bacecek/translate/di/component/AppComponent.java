package com.bacecek.translate.di.component;

import android.content.Context;

import com.bacecek.translate.data.db.LanguageManager;
import com.bacecek.translate.data.db.PrefsManager;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.di.module.AppModule;
import com.bacecek.translate.di.module.DictionaryModule;
import com.bacecek.translate.di.module.NetworkModule;
import com.bacecek.translate.di.module.RealmModule;
import com.bacecek.translate.di.module.TranslatorModule;
import com.bacecek.translate.mvp.choose_language.ChooseLanguagePresenter;
import com.bacecek.translate.mvp.favourites.FavouritePresenter;
import com.bacecek.translate.mvp.history.HistoryPresenter;
import com.bacecek.translate.mvp.settings.SettingsPresenter;
import com.bacecek.translate.mvp.splash.SplashScreenPresenter;
import com.bacecek.translate.mvp.translate.TranslatePresenter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Denis Buzmakov on 10/04/2017.
 * <buzmakov.da@gmail.com>
 */

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class, RealmModule.class, DictionaryModule.class,
		TranslatorModule.class})
public interface AppComponent {
	RealmController getRealmController();
	LanguageManager getLanguageManager();
	PrefsManager getPrefsManager();
	Context context();

	void inject(ChooseLanguagePresenter presenter);
	void inject(SplashScreenPresenter presenter);
	void inject(HistoryPresenter presenter);
	void inject(FavouritePresenter presenter);
	void inject(SettingsPresenter presenter);
	void inject(TranslatePresenter presenter);
}
