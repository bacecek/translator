package com.bacecek.translate.mvp.favourites;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entity.Translation;
import com.bacecek.translate.mvp.favourites.FavouritesInteractor;
import com.bacecek.translate.mvp.favourites.FavouriteView;
import io.realm.RealmResults;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@InjectViewState
public class FavouritePresenter extends MvpPresenter<FavouriteView> {
	@Inject
	FavouritesInteractor mInteractor;

	private String mCurrentSearchText = "";

	public FavouritePresenter() {
		App.getAppComponent().inject(this);
	}

	@Override
	protected void onFirstViewAttach() {
		super.onFirstViewAttach();
		if(mCurrentSearchText.isEmpty()) {
			getViewState().setData(mInteractor.getFavourites());
		}
	}

	public void onItemSwiped(Translation translation) {
		mInteractor.changeFavourite(translation);
	}

	public void onInputChanged(String search) {
		mCurrentSearchText = search;
		if(mCurrentSearchText.isEmpty()) {
			getViewState().setButtonClearVisibility(false);
			getViewState().setEmptyViewText(R.string.empty_list_favourites);
		} else {
			getViewState().setButtonClearVisibility(true);
			getViewState().setEmptyViewText(R.string.empty_search);
		}
		RealmResults<Translation> favourites = mInteractor.getFavourites(mCurrentSearchText);
		getViewState().updateData(favourites);
	}

	public void onDataChanged(int size) {
		if(size == 0) {
			getViewState().setEmptyViewVisibility(true);
			getViewState().setListVisibility(false);
			if(mCurrentSearchText.isEmpty())
				getViewState().setSearchVisibility(false);
		} else {
			getViewState().setEmptyViewVisibility(false);
			getViewState().setListVisibility(true);
		}
	}
}
