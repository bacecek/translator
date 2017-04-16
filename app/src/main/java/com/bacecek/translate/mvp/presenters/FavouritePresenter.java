package com.bacecek.translate.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.R;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entities.Translation;
import com.bacecek.translate.mvp.views.FavouriteView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@InjectViewState
public class FavouritePresenter extends MvpPresenter<FavouriteView> {
	@Inject
	RealmController mRealmController;

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

	public void setSearchObservable(Observable<TextViewAfterTextChangeEvent> observable) {
		observable
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.map(event -> {
					String text = event.view().getText().toString();
					if(text.isEmpty()) {
						getViewState().hideButtonClear();
						getViewState().setEmptyViewText(R.string.empty_list_favourites);
					} else {
						getViewState().showButtonClear();
						getViewState().setEmptyViewText(R.string.empty_search);
					}
					return text;
				})
				.map(text -> mRealmController.getFavourites(text))
				.subscribe(favourites -> getViewState().updateData(favourites));
	}

	public void onDataChanged(int size, String searchText) {
		if(size == 0) {
			getViewState().showEmptyView();
			getViewState().hideList();
			if(searchText.isEmpty())
				getViewState().hideSearch();
		} else {
			getViewState().hideEmptyView();
			getViewState().showList();
			getViewState().showSearch();
		}
	}
}
