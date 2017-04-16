package com.bacecek.translate.mvp.presenters;

import android.os.Handler;
import android.os.Looper;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.data.db.LanguageManager;
import com.bacecek.translate.data.db.LanguageManager.OnChangeLanguageListener;
import com.bacecek.translate.data.db.PrefsManager;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entities.DictionaryItem;
import com.bacecek.translate.data.entities.Language;
import com.bacecek.translate.data.entities.Translation;
import com.bacecek.translate.data.network.DictionaryAPI;
import com.bacecek.translate.data.network.TranslatorAPI;
import com.bacecek.translate.mvp.views.TranslateView;
import com.bacecek.translate.utils.Consts;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmChangeListener;
import java.util.List;
import javax.inject.Inject;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Synthesis;
import ru.yandex.speechkit.Vocalizer;
import ru.yandex.speechkit.VocalizerListener;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@InjectViewState
public class TranslatePresenter extends MvpPresenter<TranslateView> {
	private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
	private Disposable mTranslateDisposable;
	private String mCurrentOriginalText = "";
	private String mCurrentTranslatedText = "";
	private boolean mIsLoading;
	private Handler mDelayInputHandler = new Handler(Looper.getMainLooper());
	private Runnable mDelayInputRunnable;
	private Translation mCurrentTranslation;
	private int mCurrentResponseCount = 0;

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

	private final OnChangeLanguageListener mLanguageListener = new OnChangeLanguageListener() {
		@Override
		public void onChangeOriginalLang(Language lang) {
			getViewState().setOriginalLangName(lang.getName());
		}

		@Override
		public void onChangeTargetLang(Language lang) {
			getViewState().setTargetLangName(lang.getName());
		}
	};

	private final RealmChangeListener<Translation> mChangeFavouriteListener = translation -> getViewState().setTranslationFavourite(translation.isFavourite());

	private final VocalizerListener mOriginalVocalizerListener = new VocalizerListener() {
		@Override
		public void onSynthesisBegin(Vocalizer vocalizer) {

		}

		@Override
		public void onSynthesisDone(Vocalizer vocalizer, Synthesis synthesis) {

		}

		@Override
		public void onPlayingBegin(Vocalizer vocalizer) {

		}

		@Override
		public void onPlayingDone(Vocalizer vocalizer) {

		}

		@Override
		public void onVocalizerError(Vocalizer vocalizer, Error error) {

		}
	};

	public TranslatePresenter() {
		App.getAppComponent().inject(this);
		mLanguageManager.setListener(mLanguageListener);
	}

	@Override
	protected void onFirstViewAttach() {
		super.onFirstViewAttach();
		getViewState().setHistoryData(mRealmController.getHistory());
		getViewState().setOriginalLangName(mLanguageManager.getCurrentOriginalLangName());
		getViewState().setTargetLangName(mLanguageManager.getCurrentTargetLangName());
	}

	public void onHistoryItemSwipe(Translation translation) {
		mRealmController.removeTranslationFromHistory(translation);
	}

	public void saveTranslation(boolean async) {
		if(!mCurrentOriginalText.isEmpty() && !mIsLoading) {
			if(async) {
				mRealmController.insertTranslationAsync(
						mCurrentOriginalText,
						mCurrentTranslatedText,
						mLanguageManager.getCurrentOriginalLangCode(),
						mLanguageManager.getCurrentTargetLangCode());
			} else {
				mRealmController.insertTranslation(
						mCurrentOriginalText,
						mCurrentTranslatedText,
						mLanguageManager.getCurrentOriginalLangCode(),
						mLanguageManager.getCurrentTargetLangCode());
			}
		}
	}

	public void loadTranslation(String text) {
		if(text.length() == 0) {
			return;
		}
		onLoadStart();
		String direction = mLanguageManager.getCurrentOriginalLangCode() + "-" + mLanguageManager.getCurrentTargetLangCode();
		Observable<Translation> translationObservable = mTranslatorAPI.translate(text, direction);
		Observable<List<DictionaryItem>> dictionaryObservable = mDictionaryAPI.translate(text, direction, mPrefsManager.getSavedSystemLocale());
		mCurrentResponseCount++;
		final int requestCount = mCurrentResponseCount;
		if(mTranslateDisposable != null) {
			mTranslateDisposable.dispose();
		}
		mTranslateDisposable = Observable.combineLatest(translationObservable,
				dictionaryObservable, this::combine)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(combineResult -> {
					if(requestCount == mCurrentResponseCount) {
						onSuccess(combineResult);
						onLoadFinish();
					}
				}, throwable -> {
					if(requestCount == mCurrentResponseCount) {
						onError(throwable);
						onLoadFinish();
					}
				});
		mCompositeDisposable.add(mTranslateDisposable);
	}

	public void onClickChooseOriginalLang() {
		getViewState().goToChooseOriginalLanguage(mLanguageManager.getCurrentOriginalLangCode());
	}

	public void onClickChooseTargetLang() {
		getViewState().goToChooseTargetLanguage(mLanguageManager.getCurrentTargetLangCode());
	}

	public void onChooseOriginalLang(String langCode) {
		mLanguageManager.setCurrentOriginalLangCode(langCode);
	}

	public void onChooseTargetLang(String langCode) {
		mLanguageManager.setCurrentTargetLangCode(langCode);
	}

	public void onInputChanged(String text) {
		if(mCurrentOriginalText.equals(text)) {
			return;
		}
		mCurrentOriginalText = text;
		if(mCurrentOriginalText.length() == 0) {
			onLoadFinish();
			getViewState().hideButtonClear();
			getViewState().hideButtonVocalize();
			getViewState().showHistory();
			getViewState().hideTranslation();
			getViewState().hideDictionary();
			getViewState().hideError();
		} else {
			getViewState().showButtonClear();
			getViewState().showButtonVocalize();
			getViewState().hideHistory();
			mDelayInputHandler.removeCallbacks(mDelayInputRunnable);
			mDelayInputRunnable = () -> loadTranslation(mCurrentOriginalText);
			mDelayInputHandler.postDelayed(mDelayInputRunnable, Consts.DELAY_INPUT);
		}
	}

	public void onLoadStart() {
		mIsLoading = true;
		getViewState().showProgress();
		getViewState().hideError();
	}

	public void onLoadFinish() {
		mIsLoading = false;
		getViewState().hideProgress();
	}

	private void onSuccess(CombineResult result) {
		if(mIsLoading) {
			if(mCurrentTranslation != null) {
				mCurrentTranslation.removeChangeListeners();
			}
			mCurrentTranslation = mRealmController.getTranslation(mCurrentOriginalText,
					mLanguageManager.getCurrentOriginalLangCode(),
					mLanguageManager.getCurrentTargetLangCode());
			if(mCurrentTranslation != null) {
				mCurrentTranslation.addChangeListener(mChangeFavouriteListener);
				getViewState().showTranslation(mCurrentTranslation);
			} else {
				getViewState().showTranslation(result.translation);
			}
			mCurrentTranslatedText = result.translation.getTranslatedText();
			getViewState().hideError();
			if(result.items.size() > 0) {
				getViewState().showDictionary(result.items);
			} else {
				getViewState().hideDictionary();
			}
		}
	}

	private void onError(Throwable error) {
		if(mIsLoading) {
			getViewState().hideTranslation();
			getViewState().hideDictionary();
			getViewState().showError(error);
		}
	}

	private void clearCurrentTranslation() {
		mCurrentTranslation.removeChangeListeners();
		mCurrentTranslation = null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mCompositeDisposable.clear();
	}

	public void onClickHistoryItem(Translation translation) {
		mLanguageManager.setCurrentOriginalLangCode(translation.getOriginalLang());
		mLanguageManager.setCurrentTargetLangCode(translation.getTargetLang());
		getViewState().setOriginalText(translation.getOriginalText());
	}

	public void onClickHistoryFavourite(Translation translation) {
		mRealmController.changeFavourite(translation);
	}

	public void onClickDictionaryWord(String word) {
		getViewState().setOriginalText(word);
		mLanguageManager.swapLanguages();
	}

	public void onReverseTranslate(String translatedText) {
		getViewState().setOriginalText(translatedText);
		mLanguageManager.swapLanguages();
	}

	public void onClickSwap() {
		mLanguageManager.swapLanguages();
		loadTranslation(mCurrentOriginalText);
	}

	public void onClickMic() {
		getViewState().startDictation(mLanguageManager.getCurrentOriginalLangCode());
	}

	public void onClickRetry() {
		loadTranslation(mCurrentOriginalText);
	}

	public void onClickFavourite() {
		if(mCurrentTranslation == null) {
			saveTranslation(false);
			mCurrentTranslation = mRealmController.getTranslation(mCurrentOriginalText,
					mLanguageManager.getCurrentOriginalLangCode(),
					mLanguageManager.getCurrentTargetLangCode());
			mCurrentTranslation.addChangeListener(mChangeFavouriteListener);
		}
		mRealmController.changeFavourite(mCurrentTranslation);
	}

	public void onDictationSuccess(String text) {
		mCurrentOriginalText = mCurrentOriginalText + text;
		getViewState().setOriginalText(mCurrentOriginalText);
	}

	private CombineResult combine(Translation translation, List<DictionaryItem> items) {
		return new CombineResult(translation, items);
	}

	private static class CombineResult {
		private Translation translation;
		private List<DictionaryItem> items;

		public CombineResult(Translation translation,
				List<DictionaryItem> items) {
			this.translation = translation;
			this.items = items;
		}
	}
}
