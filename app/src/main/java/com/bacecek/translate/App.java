package com.bacecek.translate;

import android.app.Application;
import com.bacecek.translate.di.component.AppComponent;
import com.bacecek.translate.di.component.DaggerAppComponent;
import com.bacecek.translate.di.module.AppModule;
import com.bacecek.translate.di.module.DictionaryModule;
import com.bacecek.translate.di.module.NetworkModule;
import com.bacecek.translate.di.module.RealmModule;
import com.bacecek.translate.di.module.TranslatorModule;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

/**
 * Created by Denis Buzmakov on 18/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class App extends Application {
	private static AppComponent sAppComponent;

	@Override
	public void onCreate() {
		super.onCreate();

		sAppComponent = DaggerAppComponent.builder()
				.appModule(new AppModule(this))
				.networkModule(new NetworkModule())
				.realmModule(new RealmModule())
				.translatorModule(new TranslatorModule())
				.dictionaryModule(new DictionaryModule())
				.build();

		Fabric.with(this, new Crashlytics());
		Realm.init(this);
		Timber.plant(new DebugTree());
		Stetho.initialize(
				Stetho.newInitializerBuilder(this)
						.enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
						.enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
						.build());
	}

	public static AppComponent getAppComponent() {
		return sAppComponent;
	}
}
