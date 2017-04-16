package com.bacecek.translate.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.R;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entities.Translation;
import com.bacecek.translate.mvp.views.FavouriteView;
import io.realm.RealmResults;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@InjectViewState
public class FavouritePresenter extends MvpPresenter<FavouriteView> {
	@Inject
	RealmController mRealmController;

	private String mCurrentSearchText = "";

	public FavouritePresenter() {
		App.getAppComponent().inject(this);
	}

	@Override
	protected void onFirstViewAttach() {
		super.onFirstViewAttach();
		getViewState().setData(mRealmController.getFavourites());
	}

	public void onItemSwiped(Translation translation) {
		mRealmController.changeFavourite(translation);
	}

	public void onInputChanged(String search) {
		mCurrentSearchText = search;
		if(mCurrentSearchText.isEmpty()) {
			getViewState().hideButtonClear();
			getViewState().setEmptyViewText(R.string.empty_list_favourites);
		} else {
			getViewState().showButtonClear();
			getViewState().setEmptyViewText(R.string.empty_search);
		}
		RealmResults<Translation> favourites = mRealmController.getFavourites(mCurrentSearchText);
		getViewState().updateData(favourites);
	}

	public void onDataChanged(int size) {
		if(size == 0) {
			getViewState().showEmptyView();
			getViewState().hideList();
			if(mCurrentSearchText.isEmpty())
				getViewState().hideSearch();
		} else {
			getViewState().hideEmptyView();
			getViewState().showList();
			getViewState().showSearch();
		}
	}
}
