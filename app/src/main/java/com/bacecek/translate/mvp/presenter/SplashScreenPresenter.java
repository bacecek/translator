package com.bacecek.translate.mvp.presenter;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.data.db.LanguageManager;
import com.bacecek.translate.data.db.PrefsManager;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entity.Language;
import com.bacecek.translate.data.network.api.TranslatorAPI;
import com.bacecek.translate.mvp.view.SplashScreenView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@InjectViewState
public class SplashScreenPresenter extends MvpPresenter<SplashScreenView> {
	private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

	@Inject
	TranslatorAPI mTranslatorAPI;
	@Inject
	RealmController mRealmController;
	@Inject
	PrefsManager mPrefsManager;
	@Inject
	LanguageManager mLanguageManager;

	public SplashScreenPresenter() {
		App.getAppComponent().inject(this);
	}

	@Override
	protected void onFirstViewAttach() {
		super.onFirstViewAttach();
		if(isLoadLangsNeeded()) {
			loadLangs();
		} else {
			 getViewState().goToMainScreen();
		}
	}

	private boolean isLoadLangsNeeded() {
		int langsCount = mRealmController.getLanguages().size();
		return langsCount == 0 ||
				!mPrefsManager.getSavedSystemLocale().equals(Locale.getDefault().getLanguage());
	}

	public void loadLangs() {
		Observable<List<Language>> observable = mTranslatorAPI.getLangs(Locale.getDefault().getLanguage());
		Disposable disposable = observable
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe(d -> onLoadStart())
				.doFinally(this::onLoadFinish)
				.subscribe(this::onSuccess, throwable -> onError());
		mCompositeDisposable.add(disposable);
	}

	private void onLoadStart() {
		getViewState().setLoadingVisibility(true);
		getViewState().setErrorVisibility(false);
	}

	private void onLoadFinish() {
		getViewState().setLoadingVisibility(false);
	}

	private void onSuccess(List<Language> languages) {
		ArrayList<Language> list = new ArrayList<Language>();
		list.addAll(languages);
		mRealmController.insertLanguages(list);
		mPrefsManager.saveSystemLocale();
		getViewState().goToMainScreen();
		//это все дело нужно, чтобы при первом запуске 2 дефолтных языка сразу появлялись в "недавно использованных"
		mLanguageManager.setCurrentOriginalLangCode(mPrefsManager.getLastUsedOriginalLang());
		mLanguageManager.setCurrentTargetLangCode(mPrefsManager.getLastUsedTargetLang());
	}

	private void onError() {
		getViewState().setErrorVisibility(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mCompositeDisposable.clear();
	}
}
