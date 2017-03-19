package com.bacecek.yandextranslate;

import android.app.Application;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Denis Buzmakov on 18/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Realm.init(this);

		RealmConfiguration config = new RealmConfiguration.Builder()
				.name(Realm.DEFAULT_REALM_NAME)
				.schemaVersion(0)
				.deleteRealmIfMigrationNeeded()
				.build();
		Realm.setDefaultConfiguration(config);
	}
}
