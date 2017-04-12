package com.bacecek.translate.mvp.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.data.db.LanguageManager;
import com.bacecek.translate.data.db.PrefsManager;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entities.Translation;
import com.bacecek.translate.data.network.DictionaryAPI;
import com.bacecek.translate.data.network.TranslatorAPI;
import com.bacecek.translate.mvp.views.TranslateView;
import com.bacecek.translate.utils.Consts;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@InjectViewState
public class TranslatePresenter extends MvpPresenter<TranslateView> {
	private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
	private Disposable mChangeTextDisposable;
	private Observable<Translation> mTranslateObservable;
	private Observable<Translation> mDictionaryObservable;
	private String mCurrentText = "";
	private boolean mIsLoading;

	@Inject
	LanguageManager mLanguageManager;
	@Inject
	RealmController mRealmController;
	@Inject
	PrefsManager mPrefsManager;
	@Inject
	TranslatorAPI mTranslatorAPI;
	@Inject
	DictionaryAPI mDictionaryAPI;

	public TranslatePresenter() {
		App.getAppComponent().inject(this);
	}

	@Override
	protected void onFirstViewAttach() {
		super.onFirstViewAttach();
		getViewState().setHistoryData(mRealmController.getHistory());
	}

	public void setInputObservable(Observable<TextViewTextChangeEvent> observable) {
		mChangeTextDisposable = observable
				.map(event -> event.view().getText().toString())
				.filter(text -> !mCurrentText.equals(text))
				.observeOn(AndroidSchedulers.mainThread())
				.doOnNext(this::onInputChanged)
				.debounce(Consts.DELAY_INPUT, TimeUnit.MILLISECONDS)
				.filter(text -> text.length() > 0)
				.doOnNext(text -> mCurrentText = text)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(text -> loadTranslation(text, mLanguageManager.getCurrentOriginalLangCode() + "-" + mLanguageManager.getCurrentTargetLangCode()));
		mCompositeDisposable.add(mChangeTextDisposable);
	}

	public void onHistoryItemSwipe(Translation translation) {
		mRealmController.removeTranslation(translation);
	}

	public void saveTranslation(String originalText, String translatedText) {
		onLoadFinish();
		if(!originalText.isEmpty() && !mIsLoading) {
			mRealmController.insertTranslation(
					originalText,
					translatedText,
					mLanguageManager.getCurrentOriginalLangCode(),
					mLanguageManager.getCurrentTargetLangCode());
		}
	}

	public void loadTranslation(String text, String direction) {
		onLoadStart();
		mTranslateObservable = mTranslatorAPI.translate(text, direction);
		mCompositeDisposable.add(mTranslateObservable
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(translation -> {
					onSuccess(translation);
					onLoadFinish();
				}, throwable -> {
					onError(throwable);
					onLoadFinish();
				}));
	}

	public void onClickChooseOriginalLang() {
		getViewState().goToChooseOriginalLanguage(mLanguageManager.getCurrentOriginalLangCode());
	}

	public void onClickChooseTargetLang() {
		getViewState().goToChooseTargetLanguage(mLanguageManager.getCurrentTargetLangCode());
	}

	private void onInputChanged(String text) {
		mCurrentText = text;
		if(mCurrentText.length() == 0) {
			getViewState().hideButtonClear();
			getViewState().hideButtonVocalize();
			getViewState().showHistory();
			getViewState().hideTranslation();
			getViewState().hideDictionary();
		} else {
			getViewState().showButtonClear();
			getViewState().showButtonVocalize();
			getViewState().hideHistory();
		}
	}

	private void onLoadStart() {
		mIsLoading = true;
		getViewState().showProgress();
	}

	private void onLoadFinish() {
		mIsLoading = false;
		getViewState().hideProgress();
	}

	private void onSuccess(Translation translation) {
		if(mIsLoading) {
			getViewState().hideError();
			getViewState().showTranslation(translation);
		}
	}

	private void onError(Throwable error) {
		getViewState().hideTranslation();
		getViewState().hideDictionary();
		getViewState().showError();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mCompositeDisposable.clear();
	}
}
