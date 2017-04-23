package com.bacecek.translate.mvp.presenter;

import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.EditorInfo;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.data.db.LanguageManager;
import com.bacecek.translate.data.db.LanguageManager.OnChangeLanguageListener;
import com.bacecek.translate.data.db.PrefsManager;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entity.DictionaryItem;
import com.bacecek.translate.data.entity.Language;
import com.bacecek.translate.data.entity.Translation;
import com.bacecek.translate.data.network.api.DictionaryAPI;
import com.bacecek.translate.data.network.api.TranslatorAPI;
import com.bacecek.translate.event.ChangeInputImeOptionsEvent;
import com.bacecek.translate.event.ChangeNetworkStateEvent;
import com.bacecek.translate.event.ShowDictionaryEvent;
import com.bacecek.translate.event.TranslateEvent;
import com.bacecek.translate.mvp.view.TranslateView;
import com.bacecek.translate.ui.widget.VocalizeButton;
import com.bacecek.translate.util.Consts;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmChangeListener;
import java.util.List;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
	private static final int VOCALIZE_BUTTON_ORIGINAL = 0;
	private static final int VOCALIZE_BUTTON_TRANSLATED = 1;

	private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
	private Disposable mTranslateDisposable;
	private String mCurrentOriginalText = "";
	private String mCurrentTranslatedText = "";
	private boolean mIsLoading;
	private boolean mIsError; //нужна при изменении состояния сети. только если показана ошибка, загружать перевод
	private Handler mDelayInputHandler = new Handler(Looper.getMainLooper());
	private Runnable mDelayInputRunnable;
	private Translation mCurrentTranslation;
	private int mCurrentResponseCount = 0; //счетчик запросов. нужен для проверки, тот ли запрос, который только что завершился, который нам нужен. вдруг он опоздал
	private Vocalizer mSpeechVocalizer;
	private int mCurrentVocalizeButton;
	private List<DictionaryItem> mCurrentDictionaryItems;

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
			updateVocalizeAndMicButtonsState();
		}

		@Override
		public void onChangeTargetLang(Language lang) {
			getViewState().setTargetLangName(lang.getName());
			updateVocalizeAndMicButtonsState();
		}
	};

	private final RealmChangeListener<Translation> mChangeFavouriteListener = translation -> {
		if(translation.isValid()) {
			getViewState().setTranslationFavourite(translation.isFavourite());
		} else {
			mCurrentTranslation = null;
		}
	};

	private final VocalizerListener mVocalizerListener = new VocalizerListener() {
		@Override
		public void onSynthesisBegin(Vocalizer vocalizer) {
			setButtonState(VocalizeButton.STATE_INIT);
		}

		@Override
		public void onSynthesisDone(Vocalizer vocalizer, Synthesis synthesis) {

		}

		@Override
		public void onPlayingBegin(Vocalizer vocalizer) {
			setButtonState(VocalizeButton.STATE_STOP);
		}

		@Override
		public void onPlayingDone(Vocalizer vocalizer) {
			setButtonState(VocalizeButton.STATE_PLAY);
		}

		@Override
		public void onVocalizerError(Vocalizer vocalizer, Error error) {
			getViewState().showToast(error.getString());
			setButtonState(VocalizeButton.STATE_PLAY);
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
		updateVocalizeAndMicButtonsState();
		EventBus.getDefault().register(this);
		updateInputImeOptions();
	}

	public void onHistoryItemSwipe(Translation translation) {
		mRealmController.removeTranslationFromHistory(translation);
	}

	public void saveTranslation(boolean async) {
		if(!mCurrentOriginalText.isEmpty() && !mCurrentTranslatedText.isEmpty() && !mIsLoading) {
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

	public void loadTranslation() {
		//загружать пустой текст нам ни к чему
		if(mCurrentOriginalText.length() == 0) {
			return;
		}
		String direction = mLanguageManager.getCurrentOriginalLangCode() + "-" + mLanguageManager.getCurrentTargetLangCode();
		Observable<Translation> translationObservable = mTranslatorAPI.translate(mCurrentOriginalText, direction);
		Observable<List<DictionaryItem>> dictionaryObservable = mDictionaryAPI.translate(mCurrentOriginalText, direction, mPrefsManager.getSavedSystemLocale());
		mCurrentResponseCount++;
		final int requestCount = mCurrentResponseCount;
		//если какой-то запрос сейчас и идет, то отменить его не помешает
		if(mTranslateDisposable != null) {
			mTranslateDisposable.dispose();
		}
		mTranslateDisposable = Observable.combineLatest(translationObservable,
				dictionaryObservable, this::combine)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe(disposable -> onLoadStart())
				.doFinally(() -> {
					//проверка запроса на валидность - вдруг он опоздал. дальше то же самое
					if(requestCount == mCurrentResponseCount) {
						onLoadFinish();
					}
				})
				.subscribe(combineResult -> {
					if(requestCount == mCurrentResponseCount) {
						onSuccess(combineResult);
					}
				}, throwable -> {
					if(requestCount == mCurrentResponseCount) {
						onError(throwable);
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
		mDelayInputHandler.removeCallbacks(mDelayInputRunnable);
		if(mCurrentOriginalText.length() == 0) {
			//если инпут пустой, все чо не надо завершаем, скрываем и тд
			mCurrentTranslation = null;
			onLoadFinish();
			getViewState().setButtonClearVisibility(false);
			getViewState().setButtonVocalizeVisibility(false);
			getViewState().setHistoryVisibility(true);
			getViewState().setTranslationVisibility(false);
			getViewState().setDictionaryVisibility(false);
			getViewState().setErrorVisibility(false);
			mIsError = false;
		} else {
			getViewState().setButtonClearVisibility(true);
			getViewState().setButtonVocalizeVisibility(true);
			getViewState().setHistoryVisibility(false);
			if(mPrefsManager.simultaneousTranslation()) {
				mDelayInputRunnable = this::loadTranslation;
				mDelayInputHandler.postDelayed(mDelayInputRunnable, Consts.DELAY_INPUT);
			}
		}
	}

	private void onLoadStart() {
		mIsLoading = true;
		mIsError = false;
		getViewState().setProgressVisibility(true);
		getViewState().setErrorVisibility(false);
	}

	private void onLoadFinish() {
		mIsLoading = false;
		getViewState().setProgressVisibility(false);
	}

	private void onSuccess(CombineResult result) {
		if(mIsLoading) {
			mIsError = false;
			//удалить все слушатели, а то вдруг чо утечет
			if(mCurrentTranslation != null) {
				mCurrentTranslation.removeAllChangeListeners();
			}
			mCurrentTranslation = mRealmController.getTranslation(mCurrentOriginalText,
					mLanguageManager.getCurrentOriginalLangCode(),
					mLanguageManager.getCurrentTargetLangCode());
			getViewState().setTranslationVisibility(true);
			if(mCurrentTranslation != null) {
				mCurrentTranslation.addChangeListener(mChangeFavouriteListener);
				getViewState().setTranslationData(mCurrentTranslation);
			} else {
				getViewState().setTranslationData(result.translation);
			}
			mCurrentDictionaryItems = result.items;
			mCurrentTranslatedText = result.translation.getTranslatedText();
			getViewState().setErrorVisibility(false);
			getViewState().setDictionaryData(mCurrentDictionaryItems);
			if(mCurrentDictionaryItems.size() > 0) {
				getViewState().setDictionaryVisibility(mPrefsManager.showDictionary());
			} else {
				getViewState().setDictionaryVisibility(false);
			}
		}
	}

	private void onError(Throwable error) {
		if(mIsLoading) {
			mIsError = true;
			mCurrentTranslation = null;
			getViewState().setTranslationVisibility(false);
			getViewState().setDictionaryVisibility(false);
			getViewState().setErrorVisibility(true);
			getViewState().setErrorData(error);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mCompositeDisposable.clear();
		EventBus.getDefault().unregister(this);
	}

	public void onClickClear() {
		if(!mIsError) {
			//если вдруг ошибка, текущие тексты в инпуте и переведенные могут не соответствовать
			saveTranslation(true);
		}
		onLoadFinish();
		getViewState().setOriginalText("");
	}

	public void onClickHistoryItem(Translation translation) {
		mLanguageManager.setCurrentOriginalLangCode(translation.getOriginalLang());
		mLanguageManager.setCurrentTargetLangCode(translation.getTargetLang());
		getViewState().setOriginalText(translation.getOriginalText());
		//если отключен синхронный перевод, то надо загрузить перевод
		if(!mPrefsManager.simultaneousTranslation()) {
			loadTranslation();
		}
	}

	public void onClickHistoryFavourite(Translation translation) {
		mRealmController.changeFavourite(translation);
	}

	public void onClickDictionaryWord(String word) {
		saveTranslation(true);
		getViewState().setOriginalText(word);
		mLanguageManager.swapLanguages(); //смена языков местами нужна, т.к. при слово из словаря всегда на другом языке
		if(!mPrefsManager.simultaneousTranslation()) {
			loadTranslation();
		}
	}

	public void onReverseTranslate(String translatedText) {
		getViewState().setOriginalText(translatedText);
		mLanguageManager.swapLanguages();
	}

	public void onClickSwap() {
		mLanguageManager.swapLanguages();
		loadTranslation();
	}

	public void onClickMic() {
		getViewState().startDictation(mLanguageManager.getCurrentOriginalLangCode());
	}

	public void onClickRetry() {
		loadTranslation();
	}

	public void onClickFavourite() {
		//если текущий перевод отсутствует в базе, то сначала нужно его записать туда и повешать слушатель
		if(mCurrentTranslation == null) {
			saveTranslation(false);
			mCurrentTranslation = mRealmController.getTranslation(mCurrentOriginalText,
					mLanguageManager.getCurrentOriginalLangCode(),
					mLanguageManager.getCurrentTargetLangCode());
			mCurrentTranslation.addChangeListener(mChangeFavouriteListener);
		}
		mRealmController.changeFavourite(mCurrentTranslation);
	}

	public void onClickVocalizeOriginal(int buttonState) {
		switch (buttonState) {
			case VocalizeButton.STATE_PLAY:
				stopVocalize();
				mCurrentVocalizeButton = VOCALIZE_BUTTON_ORIGINAL;
				startVocalize(mCurrentOriginalText, mLanguageManager.getCurrentOriginalLangCode());
				break;
			case VocalizeButton.STATE_STOP:
				stopVocalize();
				getViewState().setOriginalVocalizeButtonState(VocalizeButton.STATE_PLAY);
				break;
		}
	}

	public void onClickVocalizeTranslated(int buttonState) {
		switch (buttonState) {
			case VocalizeButton.STATE_PLAY:
				stopVocalize();
				mCurrentVocalizeButton = VOCALIZE_BUTTON_TRANSLATED;
				startVocalize(mCurrentTranslatedText, mLanguageManager.getCurrentTargetLangCode());
				break;
			case VocalizeButton.STATE_STOP:
				stopVocalize();
				getViewState().setTranslatedVocalizeButtonState(VocalizeButton.STATE_PLAY);
				break;
		}
	}

	private void startVocalize(String text, String lang) {
		mSpeechVocalizer = Vocalizer.createVocalizer(lang, text, true);
		mSpeechVocalizer.setListener(mVocalizerListener);
		mSpeechVocalizer.start();
	}

	private void stopVocalize() {
		resetVocalizer();
		setButtonState(VocalizeButton.STATE_PLAY);
	}

	private void resetVocalizer() {
		if(mSpeechVocalizer != null) {
			mSpeechVocalizer.cancel();
			mSpeechVocalizer = null;
		}
	}

	private void setButtonState(int state) {
		if(mCurrentVocalizeButton == VOCALIZE_BUTTON_ORIGINAL) {
			getViewState().setOriginalVocalizeButtonState(state);
		} else if(mCurrentVocalizeButton == VOCALIZE_BUTTON_TRANSLATED) {
			getViewState().setTranslatedVocalizeButtonState(state);
		}
	}

	private void updateVocalizeAndMicButtonsState() {
		//помимо проверки на доступность языка для озвучивания текста проверяем еще на количество символов в тексте - вдруг много слишком
		boolean isOriginalVocalizeButtonEnabled = mLanguageManager.isRecognitionAndVocalizeAvailable(mLanguageManager.getCurrentOriginalLangCode()) &&
				mCurrentOriginalText.length() < Consts.MAX_VOCALIZE_SYMBOLS;
		boolean isTranslatedVocalizeButtonEnabled = mLanguageManager.isRecognitionAndVocalizeAvailable(mLanguageManager.getCurrentTargetLangCode()) &&
				mCurrentTranslatedText.length() < Consts.MAX_VOCALIZE_SYMBOLS;
		getViewState().setOriginalVocalizeButtonEnabled(isOriginalVocalizeButtonEnabled);
		getViewState().setTranslatedVocalizeButtonEnabled(isTranslatedVocalizeButtonEnabled);
		getViewState().setMicButtonEnabled(mLanguageManager.isRecognitionAndVocalizeAvailable(mLanguageManager.getCurrentOriginalLangCode()));
	}

	public void onDictationSuccess(String text) {
		mCurrentOriginalText = mCurrentOriginalText + text;
		getViewState().setOriginalText(mCurrentOriginalText);
		if(!mPrefsManager.simultaneousTranslation()) {
			loadTranslation();
		}
	}

	private void updateInputImeOptions() {
		if(mPrefsManager.returnForTranslate()) {
			getViewState().setInputImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_DONE);
		} else {
			getViewState().setInputImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI | EditorInfo.IME_ACTION_NONE);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onTranslateEvent(TranslateEvent event) {
		saveTranslation(true);
		onClickHistoryItem(event.translation);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onShowDictionaryEvent(ShowDictionaryEvent event) {
		if(mCurrentDictionaryItems != null && mCurrentDictionaryItems.size() > 0) {
			getViewState().setDictionaryVisibility(mPrefsManager.showDictionary());
		} else {
			getViewState().setDictionaryVisibility(false);
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onChangeInputImeOptionsEvent(ChangeInputImeOptionsEvent event) {
		updateInputImeOptions();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onChangeNetworkStateEvent(ChangeNetworkStateEvent event) {
		if(mIsError && event.isOnline) {
			loadTranslation();
		}
	}

	//простой способ объединения ответов с сервера
	private CombineResult combine(Translation translation, List<DictionaryItem> items) {
		return new CombineResult(translation, items);
	}

	private static class CombineResult {
		private Translation translation;
		private List<DictionaryItem> items;

		CombineResult(Translation translation,
				List<DictionaryItem> items) {
			this.translation = translation;
			this.items = items;
		}
	}
}
