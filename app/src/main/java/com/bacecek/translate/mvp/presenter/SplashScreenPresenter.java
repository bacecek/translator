package com.bacecek.translate.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.data.entity.Language;
import com.bacecek.translate.mvp.interactor.SplashScreenInteractor;
import com.bacecek.translate.mvp.view.SplashScreenView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@InjectViewState
public class SplashScreenPresenter extends MvpPresenter<SplashScreenView> {
	private String mIncomingTranslation = null;

	@Inject
	SplashScreenInteractor mInteractor;

	public SplashScreenPresenter() {
		App.getAppComponent().inject(this);
	}

	@Override
	protected void onFirstViewAttach() {
		super.onFirstViewAttach();
		if(mInteractor.isLoadingLangsNeeded()) {
			loadLangs();
		} else {
			 getViewState().goToMainScreen(mIncomingTranslation);
		}
	}

	public void incomingTranslation(String text) {
		mIncomingTranslation = text;
	}

	public void loadLangs() {
		mInteractor.getLangs()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe(d -> onLoadStart())
				.doFinally(this::onLoadFinish)
				.subscribe(this::onSuccess, throwable -> onError());
	}

	private void onLoadStart() {
		getViewState().setLoadingVisibility(true);
		getViewState().setErrorVisibility(false);
	}

	private void onLoadFinish() {
		getViewState().setLoadingVisibility(false);
	}

	private void onSuccess(List<Language> languages) {
		mInteractor.saveLangs(languages);
		getViewState().goToMainScreen(mIncomingTranslation);
	}

	private void onError() {
		getViewState().setErrorVisibility(true);
	}
}
