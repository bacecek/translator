package com.bacecek.translate.mvp.interactor;

import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entity.Translation;
import io.realm.RealmResults;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 06/05/2017.
 * <buzmakov.da@gmail.com>
 */

public class FavouritesInteractor {
	private RealmController mRealmController;

	@Inject
	public FavouritesInteractor(RealmController realmController) {
		mRealmController = realmController;
	}

	public RealmResults<Translation> getFavourites() {
		return mRealmController.getFavourites();
	}

	public void changeFavourite(Translation translation) {
		mRealmController.changeFavourite(translation);
	}

	public RealmResults<Translation> getFavourites(String search) {
		return mRealmController.getFavourites(search);
	}
}
