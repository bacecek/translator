package com.bacecek.translate.mvp.view;

import android.support.annotation.StringRes;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.bacecek.translate.data.entity.Translation;
import io.realm.RealmResults;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface FavouriteView extends MvpView {
	void setSearchVisibility(boolean visible);
	void setEmptyViewVisibility(boolean visible);
	void setListVisibility(boolean visible);
	void setButtonClearVisibility(boolean visible);
	void setEmptyViewText(@StringRes int text);
	void setData(RealmResults<Translation> favourites);
	void updateData(RealmResults<Translation> favourites);
}
