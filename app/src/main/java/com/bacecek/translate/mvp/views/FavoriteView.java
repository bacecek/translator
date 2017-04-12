package com.bacecek.translate.mvp.views;

import android.support.annotation.StringRes;
import com.arellomobile.mvp.MvpView;
import com.bacecek.translate.data.entities.Translation;
import io.realm.RealmResults;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

public interface FavoriteView extends MvpView {
	void showSearch();
	void hideSearch();
	void showEmptyView();
	void hideEmptyView();
	void showList();
	void hideList();
	void showButtonClear();
	void hideButtonClear();
	void setEmptyViewText(@StringRes int text);
	void setData(RealmResults<Translation> favorites);
	void updateData(RealmResults<Translation> favorites);
}
