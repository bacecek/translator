package com.bacecek.translate.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.bacecek.translate.utils.Consts.DI;
import com.bacecek.translate.utils.Consts.PrefsLangs;
import com.bacecek.translate.utils.Consts.PrefsSettings;
import dagger.Module;
import dagger.Provides;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by Denis Buzmakov on 10/04/2017.
 * <buzmakov.da@gmail.com>
 */

@Module
public class AppModule {
	private Application mApp;

	public AppModule(Application app) {
		mApp = app;
	}

	@Provides
	@Singleton
	Application provideApplication() {
		return mApp;
	}

	@Provides
	@Singleton
	Context provideApplicationContext() {
		return mApp.getApplicationContext();
	}

	@Provides
	@Singleton
	@Named(DI.DI_PREFS_LANGS)
	SharedPreferences provideLangsPreferences(Application app) {
		return app.getSharedPreferences(PrefsLangs.KEY, Context.MODE_PRIVATE);
	}

	@Provides
	@Singleton
	@Named(DI.DI_PREFS_SETTINGS)
	SharedPreferences provideSettingsPreferences(Application app) {
		return app.getSharedPreferences(PrefsSettings.KEY, Context.MODE_PRIVATE);
	}
}
