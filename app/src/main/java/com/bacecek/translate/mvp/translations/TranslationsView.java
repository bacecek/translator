package com.bacecek.translate.mvp.translations;

import android.support.annotation.StringRes;

import com.arellomobile.mvp.MvpView;
import com.bacecek.translate.data.entity.Translation;

import io.realm.RealmResults;

/**
 * Created by Denis Buzmakov on 05.07.2017.
 * <buzmakov.da@gmail.com>
 */

public interface TranslationsView extends MvpView {
    void setSearchVisibility(boolean visible);
    void setEmptyViewVisibility(boolean visible);
    void setListVisibility(boolean visible);
    void setButtonClearVisibility(boolean visible);
    void setEmptyViewText(@StringRes int text);
    void setData(RealmResults<Translation> favourites);
    void updateData(RealmResults<Translation> favourites);
}
