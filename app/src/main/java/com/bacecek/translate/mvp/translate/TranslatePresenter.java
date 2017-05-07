package com.bacecek.translate.mvp.translate;

import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.EditorInfo;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.data.db.LanguageManager;
import com.bacecek.translate.data.db.LanguageManager.OnChangeLanguageListener;
import com.bacecek.translate.data.entity.DictionaryItem;
import com.bacecek.translate.data.entity.Language;
import com.bacecek.translate.data.entity.Translation;
import com.bacecek.translate.data.network.util.TranslateResult;
import com.bacecek.translate.event.ChangeInputImeOptionsEvent;
import com.bacecek.translate.event.ChangeNetworkStateEvent;
import com.bacecek.translate.event.IntentTranslateEvent;
import com.bacecek.translate.event.ShowDictionaryEvent;
import com.bacecek.translate.event.TranslateEvent;
import com.bacecek.translate.util.Consts;
import com.bacecek.translate.util.widget.VocalizeButton;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
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

	private Disposable mCurrentDisposable = Disposables.empty();
	private String mCurrentOriginalText = "";
	private String mCurrentTranslatedText = "";
	private boolean mIsLoading;
	private boolean mIsError; //нужна при изменении состояния сети. только если показана ошибка, загружать перевод
	private Handler mDelayInputHandler = new Handler(Looper.getMainLooper());
	private Runnable mDelayInputRunnable;
	private Translation mCurrentTranslation;
	private Vocalizer mSpeechVocalizer;
	private int mCurrentVocalizeButton;
	private List<DictionaryItem> mCurrentDictionaryItems;

	@Inject
	LanguageManager mLanguageManager;
	@Inject
	TranslateInteractor mInteractor;

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
		getViewState().setHistoryData(mInteractor.getHistory());
		getViewState().setOriginalLangName(mLanguageManager.getCurrentOriginalLangName());
		getViewState().setTargetLangName(mLanguageManager.getCurrentTargetLangName());
		updateVocalizeAndMicButtonsState();
		EventBus.getDefault().register(this);
		updateInputImeOptions();
	}

	public void onHistoryItemSwipe(Translation translation) {
		mInteractor.removeTranslationFromHistory(translation);
	}

	public void saveTranslation(boolean async) {
		if(!mCurrentOriginalText.isEmpty() && !mCurrentTranslatedText.isEmpty() && !mIsLoading) {
			mInteractor.saveTranslation(mCurrentOriginalText, mCurrentTranslatedText, async);
		}
	}

	public void loadTranslation() {
		//загружать пустой текст нам ни к чему
		if(mCurrentOriginalText.length() == 0) {
			return;
		}
		mCurrentDisposable.dispose();
		String direction = mLanguageManager.getCurrentOriginalLangCode() + "-" + mLanguageManager.getCurrentTargetLangCode();
		mCurrentDisposable = mInteractor.translate(mCurrentOriginalText)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe(disposable -> onLoadStart())
				.doFinally(this::onLoadFinish)
				.subscribe(this::onSuccess, this::onError);
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
			if(mInteractor.getSettingSimultaneousTranslation()) {
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

	private void onSuccess(TranslateResult result) {
		if(mIsLoading) {
			mIsError = false;
			//удалить все слушатели во избежании утечки
			if(mCurrentTranslation != null) {
				mCurrentTranslation.removeAllChangeListeners();
			}
			mCurrentTranslation = mInteractor.getTranslation(mCurrentOriginalText);
			getViewState().setTranslationVisibility(true);
			if(mCurrentTranslation != null) {
				mCurrentTranslation.addChangeListener(mChangeFavouriteListener);
				getViewState().setTranslationData(mCurrentTranslation);
			} else {
				getViewState().setTranslationData(result.getTranslation());
			}
			mCurrentDictionaryItems = result.getItems();
			mCurrentTranslatedText = result.getTranslation().getTranslatedText();
			getViewState().setErrorVisibility(false);
			getViewState().setDictionaryData(mCurrentDictionaryItems);
			if(mCurrentDictionaryItems.size() > 0) {
				getViewState().setDictionaryVisibility(mInteractor.getSettingShowDictionary());
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
		if(!mInteractor.getSettingSimultaneousTranslation()) {
			loadTranslation();
		}
	}

	public void onClickHistoryFavourite(Translation translation) {
		mInteractor.changeFavourite(translation);
	}

	public void onClickDictionaryWord(String word) {
		saveTranslation(true);
		getViewState().setOriginalText(word);
		mLanguageManager.swapLanguages(); //смена языков, т.к. слово в словаре всегда на другом языке
		if(!mInteractor.getSettingSimultaneousTranslation()) {
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
			mCurrentTranslation = mInteractor.getTranslation(mCurrentOriginalText);
			mCurrentTranslation.addChangeListener(mChangeFavouriteListener);
		}
		mInteractor.changeFavourite(mCurrentTranslation);
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
		if(!mInteractor.getSettingSimultaneousTranslation()) {
			loadTranslation();
		}
	}

	private void updateInputImeOptions() {
		if(mInteractor.getSettingReturnForTranslate()) {
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
			getViewState().setDictionaryVisibility(mInteractor.getSettingShowDictionary());
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

	@Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
	public void onIntentTranslateEvent(IntentTranslateEvent event) {
		saveTranslation(true);
		getViewState().setOriginalText(event.text);
	}
}
