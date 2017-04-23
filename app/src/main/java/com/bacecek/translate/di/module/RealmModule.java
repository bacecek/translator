package com.bacecek.translate.di.module;

import com.bacecek.translate.data.db.RealmController;
import dagger.Module;
import dagger.Provides;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import javax.inject.Singleton;

/**
 * Created by Denis Buzmakov on 10/04/2017.
 * <buzmakov.da@gmail.com>
 */

@Module
public class RealmModule {

	@Provides
	@Singleton
	RealmConfiguration provideRealmConfiguration() {
		return new RealmConfiguration.Builder()
				.name(Realm.DEFAULT_REALM_NAME)
				.deleteRealmIfMigrationNeeded()
				.build();
	}

	@Provides
	@Singleton
	Realm provideRealm(RealmConfiguration config) {
		return Realm.getInstance(config);
	}

	@Provides
	@Singleton
	RealmController provideRealmController(Realm realm) {
		return new RealmController(realm);
	}

}
