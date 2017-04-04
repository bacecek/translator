package com.bacecek.translate.ui.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.bacecek.translate.R;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.db.entities.Translation;
import com.bacecek.translate.data.network.APIGenerator;
import com.bacecek.translate.data.network.DictionaryAPI;
import com.bacecek.translate.data.network.TranslatorAPI;
import com.bacecek.translate.ui.adapters.HistoryAdapter;
import com.bacecek.translate.ui.adapters.HistoryAdapter.OnItemClickListener;
import com.bacecek.translate.ui.events.ClickMenuEvent;
import com.bacecek.translate.ui.events.TranslateEvent;
import com.bacecek.translate.ui.views.ListenButton;
import com.bacecek.translate.utils.Consts;
import com.bacecek.translate.utils.HistoryDismissTouchHelper;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Synthesis;
import ru.yandex.speechkit.Vocalizer;
import ru.yandex.speechkit.VocalizerListener;

/**
 * Created by Denis Buzmakov on 17/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class TranslateFragment extends BaseFragment{
	@BindView(R.id.edit_original_text)
	EditText mEditOriginal;
	@BindView(R.id.btn_clear)
	ImageButton mBtnClear;
	@BindView(R.id.btn_mic)
	ImageButton mBtnMic;
	@BindView(R.id.btn_listen_original)
	ListenButton mBtnListenOriginal;
	@BindView(R.id.btn_listen_translated)
	ListenButton mBtnListenTranslated;
	@BindView(R.id.btn_favourite)
	ImageButton mBtnFavourite;
	@BindView(R.id.list_history)
	RecyclerView mRecyclerHistory;
	@BindView(R.id.view_translated_text)
	View mViewTranslated;
	@BindView(R.id.txt_translated)
	TextView mTxtTranslated;
	@BindView(R.id.scroll_view)
	NestedScrollView mScrollView;

	private TranslatorAPI mTranslatorAPI;
	private DictionaryAPI mDictionaryAPI;
	private static final int DELAY_INPUT = 700;
	private Handler mDelayInputHandler = new Handler(Looper.getMainLooper());
	private Runnable mDelayInputRunnable;
	private Call<Translation> mCall;
	private boolean isSavingEnabled = false;
	private Vocalizer mSpeechVocalizer;

	@OnClick(R.id.btn_favourite)
	void onClickFavourite(View view) {
		boolean selected = view.isSelected();
		saveTranslation(true, !selected);
		view.setSelected(!selected);
	}

	@OnClick(R.id.btn_clear)
	void onClickClear() {
		saveTranslation();
		mEditOriginal.setText("");
	}

	@OnClick(R.id.btn_mic)
	void onClickMic() {

	}

	@OnClick(R.id.btn_listen_original)
	void onClickListenOriginalText(View v) {
		try {
			int state = ((ListenButton) v).getState();
			switch (state) {
				case ListenButton.STATE_PLAY:
					startListen(getOriginalText(), "ru");
					break;
				case ListenButton.STATE_STOP:
					stopListen();
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	@OnClick(R.id.btn_listen_translated)
	void onClickListenTranslatedText(View v) {
		try {
			int state = ((ListenButton) v).getState();
			switch (state) {
				case ListenButton.STATE_PLAY:
					startListen(mTxtTranslated.getText().toString(), "en");
					break;
				case ListenButton.STATE_STOP:
					stopListen();
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	@OnClick(R.id.btn_menu)
	void onClickMenu() {
		EventBus.getDefault().post(new ClickMenuEvent());
	}

	@OnTextChanged(value = R.id.edit_original_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
	void onTextChanged(Editable s) {
		mBtnListenOriginal.setEnabled(s.toString().trim().length() <= Consts.MAX_LISTEN_SYMBOLS);
		isSavingEnabled = false;
		if(s.toString().trim().length() == 0) {
			mBtnClear.setVisibility(View.INVISIBLE);
			mBtnListenOriginal.setVisibility(View.INVISIBLE);
			mViewTranslated.setVisibility(View.INVISIBLE);
			mRecyclerHistory.setVisibility(View.VISIBLE);
		} else {
			mBtnClear.setVisibility(View.VISIBLE);
			mBtnListenOriginal.setVisibility(View.VISIBLE);
			mRecyclerHistory.setVisibility(View.GONE);
		}

		mDelayInputHandler.removeCallbacks(mDelayInputRunnable);
		mDelayInputRunnable = new Runnable() {
			@Override
			public void run() {
				if(getOriginalText().length() != 0) {
					loadTranslation();
				}
			}
		};
		mDelayInputHandler.postDelayed(mDelayInputRunnable, DELAY_INPUT);
	}

	private final OnItemClickListener mOnItemHistoryClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(Translation translation) {
			mEditOriginal.setText(translation.getOriginalText());
			mEditOriginal.setSelection(translation.getOriginalText().length());
		}
	};

	private final VocalizerListener mSpeechVocalizerListener = new VocalizerListener() {
		@Override
		public void onSynthesisBegin(Vocalizer vocalizer) {
			mBtnListenOriginal.setState(ListenButton.STATE_INIT);
		}

		@Override
		public void onSynthesisDone(Vocalizer vocalizer, Synthesis synthesis) {}

		@Override
		public void onPlayingBegin(Vocalizer vocalizer) {
			mBtnListenOriginal.setState(ListenButton.STATE_STOP);
		}

		@Override
		public void onPlayingDone(Vocalizer vocalizer) {
			mBtnListenOriginal.setState(ListenButton.STATE_PLAY);
		}

		@Override
		public void onVocalizerError(Vocalizer vocalizer, Error error) {
			Toast.makeText(getActivity(), error.getString(), Toast.LENGTH_LONG).show();
		}
	};

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_translate, container, false);
		ButterKnife.bind(this, parent);
		mTranslatorAPI = APIGenerator.createTranslatorService();
		mDictionaryAPI = APIGenerator.createDictionaryService();
		setTitle(parent, getString(R.string.app_name));
		initUI();
		return parent;
	}

	private void initUI() {
		mRecyclerHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
		HistoryAdapter adapter = new HistoryAdapter(
				getActivity(),
				RealmController.getInstance().getHistory(),
				mOnItemHistoryClickListener);
		mRecyclerHistory.setAdapter(adapter);
		mRecyclerHistory.setNestedScrollingEnabled(false);
		mRecyclerHistory.setHasFixedSize(true);
		DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
		divider.setDrawable(getResources().getDrawable(R.drawable.list_divider_history));
		mRecyclerHistory.addItemDecoration(divider);
		ItemTouchHelper.Callback callback = new HistoryDismissTouchHelper(adapter);
		ItemTouchHelper helper = new ItemTouchHelper(callback);
		helper.attachToRecyclerView(mRecyclerHistory);
	}

	private void loadTranslation() {
		final String originalText = getOriginalText();
		mCall = mTranslatorAPI.translate(originalText, "en");
		isSavingEnabled = false;
		mCall.enqueue(new Callback<Translation>() {
			@Override
			public void onResponse(Call<Translation> call,
					Response<Translation> response) {
				mViewTranslated.setVisibility(View.VISIBLE);
				mTxtTranslated.setText(response.body().getTranslatedText());
				mBtnListenTranslated.setEnabled(mTxtTranslated.getText().length() <= Consts.MAX_LISTEN_SYMBOLS);
				mBtnFavourite.setSelected(RealmController.getInstance().isTranslationFavourite(originalText));
				isSavingEnabled = true;
			}

			@Override
			public void onFailure(Call<Translation> call, Throwable t) {

			}
		});
	}

	private void saveTranslation() {
		saveTranslation(false, false);
	}

	private void saveTranslation(boolean changeFavourite, boolean isFavourite) {
		if(getOriginalText().length() > 0 && mCall != null && isSavingEnabled) {
			mCall.cancel();
			Translation translation = new Translation();
			translation.setOriginalText(getOriginalText());
			translation.setTranslatedText(mTxtTranslated.getText().toString());
			translation.setOriginalLang("ru");//TODO:изменить получение языка
			translation.setTargetLang("en");
			translation.setTimestamp(System.currentTimeMillis() / 1000);
			if(changeFavourite) {
				translation.setFavourite(isFavourite);
			}
			RealmController.getInstance().insertTranslation(translation, changeFavourite);
		}
	}

	private void startListen(String text, String lang) {
		resetVocalizer();
		mSpeechVocalizer = Vocalizer.createVocalizer(lang, text, true);
		mSpeechVocalizer.setListener(mSpeechVocalizerListener);
		mSpeechVocalizer.start();
	}

	private void resetVocalizer() {
		if(mSpeechVocalizer != null) {
			mSpeechVocalizer.cancel();
			mSpeechVocalizer = null;
		}
	}

	private void stopListen() {
		resetVocalizer();
		mBtnListenOriginal.setState(ListenButton.STATE_PLAY);
	}

	private String getOriginalText() {
		return mEditOriginal.getText().toString().trim();
	}

	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		saveTranslation();
	}

	@Override
	public void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onTranslateEvent(TranslateEvent event) {
		mEditOriginal.setText(event.text);
	}
}
