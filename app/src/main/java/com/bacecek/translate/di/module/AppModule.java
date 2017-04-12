package com.bacecek.translate.di.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.bacecek.translate.utils.Consts;
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
	@Named(Consts.PREFS_LANGS_KEY)
	SharedPreferences provideLangsPreferences(Application app) {
		return app.getSharedPreferences(Consts.PREFS_LANGS_KEY, Context.MODE_PRIVATE);
	}

	@Provides
	@Singleton
	@Named(Consts.PREFS_SETTINGS_KEY)
	SharedPreferences provideSettingsPreferences(Application app) {
		return app.getSharedPreferences(Consts.PREFS_SETTINGS_KEY, Context.MODE_PRIVATE);
	}
}
