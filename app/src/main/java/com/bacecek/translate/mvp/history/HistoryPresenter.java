package com.bacecek.translate.mvp.history;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entity.Translation;

import javax.inject.Inject;

import io.realm.RealmResults;

/**
 * Created by Denis Buzmakov on 18.06.2017.
 * <buzmakov.da@gmail.com>
 */

@InjectViewState
public class HistoryPresenter extends MvpPresenter<HistoryView> {
    @Inject
    HistoryInteractor mInteractor;

    private String mCurrentSearchText = "";

    public HistoryPresenter() {
        App.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        if(mCurrentSearchText.isEmpty()) {
            getViewState().setData(mInteractor.getHistory());
        }
    }

    public void onItemSwiped(Translation translation) {
        mInteractor.removeFromHistory(translation);
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
        RealmResults<Translation> history = mInteractor.getHistory(mCurrentSearchText);
        getViewState().updateData(history);
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
