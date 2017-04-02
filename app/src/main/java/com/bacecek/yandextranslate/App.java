package com.bacecek.yandextranslate;

import android.app.Application;
import io.realm.Realm;
import timber.log.Timber;
import timber.log.Timber.DebugTree;

/**
 * Created by Denis Buzmakov on 18/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class App extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		Realm.init(this);
		Timber.plant(new DebugTree());
	}
}
