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
import com.bacecek.translate.mvp.presenters.AboutPresenter;
import com.bacecek.translate.mvp.presenters.ChooseLanguagePresenter;
import com.bacecek.translate.mvp.presenters.FavouritePresenter;
import com.bacecek.translate.mvp.presenters.SettingsPresenter;
import com.bacecek.translate.mvp.presenters.SplashScreenPresenter;
import com.bacecek.translate.mvp.presenters.TranslatePresenter;
import dagger.Component;
import javax.inject.Singleton;

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
	void inject(AboutPresenter presenter);
	void inject(FavouritePresenter presenter);
	void inject(SettingsPresenter presenter);
	void inject(TranslatePresenter presenter);
}
