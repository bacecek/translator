package com.bacecek.translate.mvp.translations;

import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entity.Translation;

import javax.inject.Inject;

import io.realm.RealmResults;

/**
 * Created by Denis Buzmakov on 05.07.2017.
 * <buzmakov.da@gmail.com>
 */

public class TranslationsInteractor {
    private RealmController mRealmController;

    @Inject
    public TranslationsInteractor(RealmController realmController) {
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

    public RealmResults<Translation> getHistory() {
        return mRealmController.getHistory();
    }

    public void removeFromHistory(Translation translation) {
        mRealmController.removeTranslationFromHistory(translation);
    }

    public void removeFromFavourites(Translation translation) {
        mRealmController.changeFavourite(translation);
    }

    public RealmResults<Translation> getHistory(String search) {
        return mRealmController.getHistory(search);
    }

}
