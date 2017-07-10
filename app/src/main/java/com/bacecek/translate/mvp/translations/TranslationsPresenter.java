package com.bacecek.translate.mvp.translations;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entity.Translation;
import com.bacecek.translate.ui.translations.TranslationsFragment;

import javax.inject.Inject;

import io.realm.RealmResults;

import static com.bacecek.translate.ui.translations.TranslationsFragment.Type.FAVOURITES;
import static com.bacecek.translate.ui.translations.TranslationsFragment.Type.HISTORY;

/**
 * Created by Denis Buzmakov on 05.07.2017.
 * <buzmakov.da@gmail.com>
 */

@InjectViewState
public class TranslationsPresenter extends MvpPresenter<TranslationsView> {

    @Inject
    TranslationsInteractor mInteractor;

    @TranslationsFragment.Type
    private int mType;
    private String mCurrentSearchText = "";

    public TranslationsPresenter() {
        App.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        if(mCurrentSearchText.isEmpty()) {
            switch (mType) {
                case HISTORY:
                    getViewState().setData(mInteractor.getHistory());
                    break;
                case FAVOURITES:
                    getViewState().setData(mInteractor.getFavourites());
                    break;
            }
        }
    }

    public void setType(@TranslationsFragment.Type int type) {
        mType = type;
    }

    public void onItemSwiped(Translation translation) {
        switch (mType) {
            case HISTORY:
                mInteractor.removeFromHistory(translation);
                break;
            case FAVOURITES:
                mInteractor.removeFromFavourites(translation);
                break;
        }
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
        RealmResults<Translation> data = null;
        switch (mType) {
            case HISTORY:
                data = mInteractor.getHistory(mCurrentSearchText);
                break;
            case FAVOURITES:
                data = mInteractor.getFavourites(mCurrentSearchText);
                break;
        }
        if(data != null) {
            getViewState().updateData(data);
        }
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
            getViewState().setSearchVisibility(true);
        }
    }

    public void onClickItemFavourite(Translation translation) {
        mInteractor.changeFavourite(translation);
    }

}
