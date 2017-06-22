package com.bacecek.translate.mvp.history;

import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entity.Translation;

import javax.inject.Inject;

import io.realm.RealmResults;

/**
 * Created by Denis Buzmakov on 18.06.2017.
 * <buzmakov.da@gmail.com>
 */

public class HistoryInteractor {
    private RealmController mRealmController;

    @Inject
    public HistoryInteractor(RealmController realmController) {
        mRealmController = realmController;
    }

    public RealmResults<Translation> getHistory() {
        return mRealmController.getHistory();
    }

    public void removeFromHistory(Translation translation) {
        mRealmController.removeTranslationFromHistory(translation);
    }

    public RealmResults<Translation> getHistory(String search) {
        return mRealmController.getHistory(search);
    }
}
