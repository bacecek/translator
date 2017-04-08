package com.bacecek.translate;

import android.app.Application;
import com.bacecek.translate.data.db.PrefsManager;
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

		PrefsManager.init(this);
		Realm.init(this);
		Timber.plant(new DebugTree());
	}
}
