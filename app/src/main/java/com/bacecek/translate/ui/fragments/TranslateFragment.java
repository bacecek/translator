package com.bacecek.translate.ui.fragments;

import android.Manifest.permission;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.PopupMenu.OnMenuItemClickListener;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.bacecek.translate.data.entities.DictionaryItem;
import com.bacecek.translate.data.entities.Translation;
import com.bacecek.translate.data.network.APIGenerator;
import com.bacecek.translate.data.network.DictionaryAPI;
import com.bacecek.translate.data.network.TranslatorAPI;
import com.bacecek.translate.ui.adapters.DictionaryAdapter;
import com.bacecek.translate.ui.adapters.DictionaryAdapter.OnWordClickListener;
import com.bacecek.translate.ui.adapters.HistoryAdapter;
import com.bacecek.translate.ui.adapters.HistoryAdapter.OnItemClickListener;
import com.bacecek.translate.ui.events.ClickMenuEvent;
import com.bacecek.translate.ui.events.TranslateEvent;
import com.bacecek.translate.ui.views.ListenButton;
import com.bacecek.translate.utils.Consts;
import com.bacecek.translate.utils.HistoryDismissTouchHelper;
import com.bacecek.translate.utils.SpeechVocalizeListener;
import com.bacecek.translate.utils.Utils;
import java.util.List;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Recognizer.Model;
import ru.yandex.speechkit.Vocalizer;
import ru.yandex.speechkit.gui.RecognizerActivity;
import timber.log.Timber;

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
	@BindView(R.id.btn_favourite)
	ImageButton mBtnFavourite;
	@BindView(R.id.btn_more)
	ImageButton mBtnMore;
	@BindView(R.id.btn_listen_original)
	ListenButton mBtnListenOriginal;
	@BindView(R.id.btn_listen_translated)
	ListenButton mBtnListenTranslated;
	@BindView(R.id.list_history)
	RecyclerView mRecyclerHistory;
	@BindView(R.id.list_dictionary)
	RecyclerView mRecyclerDictionary;
	@BindView(R.id.view_translated_text)
	View mViewTranslated;
	@BindView(R.id.view_dictionary)
	View mViewDictionary;
	@BindView(R.id.txt_translated)
	TextView mTxtTranslated;
	@BindView(R.id.scroll_view)
	NestedScrollView mScrollView;

	private TranslatorAPI mTranslatorAPI;
	private DictionaryAPI mDictionaryAPI;
	private Handler mDelayInputHandler = new Handler(Looper.getMainLooper());
	private Runnable mDelayInputRunnable;
	private Call<Translation> mTranslationCall;
	private Call<List<DictionaryItem>> mDictionaryCall;
	private boolean isSavingEnabled = false;
	private Vocalizer mSpeechVocalizer;

	@OnClick(R.id.btn_favourite)
	void onClickFavourite(View view) {
		Translation translation = RealmController.getInstance().getTranslation(getOriginalText());
		if(translation == null) {
			saveTranslation();
		}
		RealmController.getInstance().changeFavourite(getOriginalText());

		view.setActivated(!view.isActivated());
	}

	@OnClick(R.id.btn_clear)
	void onClickClear() {
		saveTranslation();
		mEditOriginal.setText("");
	}

	@OnClick(R.id.btn_mic)
	void onClickMic() {
		Intent intent = new Intent(getActivity(), RecognizerActivity.class);
		intent.putExtra(RecognizerActivity.EXTRA_MODEL, Model.QUERIES);
		intent.putExtra(RecognizerActivity.EXTRA_LANGUAGE, "ru");
		startActivityForResult(intent, Consts.RECOGNITION_REQUEST_CODE);
	}

	@OnClick(R.id.btn_listen_original)
	void onClickListenOriginalText(View v) {
		try {
			ListenButton button = (ListenButton) v;
			int state = button.getState();
			switch (state) {
				case ListenButton.STATE_PLAY:
					mSpeechVocalizerListener.setButton(button);
					startListen(getOriginalText(), "ru");
					break;
				case ListenButton.STATE_STOP:
					stopListen();
					button.setState(ListenButton.STATE_PLAY);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	@OnClick(R.id.btn_listen_translated)
	void onClickListenTranslatedText(View v) {
		try {
			ListenButton button = (ListenButton) v;
			int state = button.getState();
			switch (state) {
				case ListenButton.STATE_PLAY:
					mSpeechVocalizerListener.setButton(button);
					startListen(mTxtTranslated.getText().toString(), "en");
					break;
				case ListenButton.STATE_STOP:
					stopListen();
					button.setState(ListenButton.STATE_PLAY);
			}
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
	}

	@OnClick(R.id.btn_menu)
	void onClickMenu() {
		EventBus.getDefault().post(new ClickMenuEvent());
	}

	@OnClick(R.id.btn_more)
	void onClickMore() {
		PopupMenu popupMenu = new PopupMenu(getActivity(), mBtnMore);
		popupMenu.getMenuInflater().inflate(R.menu.menu_more, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(mOnMenuMoreItemClickListener);
		popupMenu.show();
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
		mDelayInputHandler.postDelayed(mDelayInputRunnable, Consts.DELAY_INPUT);
	}

	private final OnItemClickListener mOnItemHistoryClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(Translation translation) {
			changeOriginalText(translation.getOriginalText());
			mEditOriginal.setSelection(translation.getOriginalText().length());
		}
	};

	private final SpeechVocalizeListener mSpeechVocalizerListener = new SpeechVocalizeListener();

	private final OnWordClickListener mOnWordClickListener = new OnWordClickListener() {
		@Override
		public void onWordClick(String word) {
			changeOriginalText(word);
		}
	};

	private final PopupMenu.OnMenuItemClickListener mOnMenuMoreItemClickListener = new OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
				case R.id.action_copy:
					Utils.copyToClipboard(getActivity(), mTxtTranslated.getText().toString());
					Toast.makeText(getActivity(), R.string.text_has_been_copied, Toast.LENGTH_SHORT).show();
					break;
				case R.id.action_share:
					Utils.shareText(getActivity(), mTxtTranslated.getText().toString());
					break;
				case R.id.action_fullscreen:
					break;
				case R.id.action_reverse_translate:
					break;
			}
			return false;
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
		mRecyclerDictionary.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecyclerDictionary.setNestedScrollingEnabled(false);
	}

	private void loadTranslation() {
		mViewDictionary.setVisibility(View.GONE);
		final String originalText = getOriginalText();
		mTranslationCall = mTranslatorAPI.translate(originalText, "en");
		mDictionaryCall = mDictionaryAPI.translate(originalText, "ru-en");
		isSavingEnabled = false;
		mTranslationCall.enqueue(new Callback<Translation>() {
			@Override
			public void onResponse(Call<Translation> call,
					Response<Translation> response) {
				mViewTranslated.setVisibility(View.VISIBLE);
				mTxtTranslated.setText(response.body().getTranslatedText());
				mBtnListenTranslated.setEnabled(mTxtTranslated.getText().length() <= Consts.MAX_LISTEN_SYMBOLS);
				mBtnFavourite.setActivated(RealmController.getInstance().isTranslationFavourite(originalText));
				isSavingEnabled = true;
			}

			@Override
			public void onFailure(Call<Translation> call, Throwable t) {

			}
		});
		mDictionaryCall.enqueue(new Callback<List<DictionaryItem>>() {
			@Override
			public void onResponse(Call<List<DictionaryItem>> call,
					Response<List<DictionaryItem>> response) {
				if(response.body().size() > 0) {
					mViewDictionary.setVisibility(View.VISIBLE);
				} else {
					mViewDictionary.setVisibility(View.GONE);
				}
				DictionaryAdapter adapter = new DictionaryAdapter(response.body(), mOnWordClickListener);
				mRecyclerDictionary.setAdapter(adapter);
			}

			@Override
			public void onFailure(Call<List<DictionaryItem>> call, Throwable t) {
				Timber.d(t.getMessage());
			}
		});
	}

	private void saveTranslation() {
		if(getOriginalText().length() > 0 && mTranslationCall != null && mDictionaryCall != null&& isSavingEnabled) {
			mTranslationCall.cancel();
			mDictionaryCall.cancel();
			RealmController.getInstance().insertTranslation(getOriginalText(), mTxtTranslated.getText().toString(), "ru", "en");
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
	}

	private String getOriginalText() {
		return mEditOriginal.getText().toString().trim();
	}

	private void changeOriginalText(String text) {
		mEditOriginal.setText("");
		mEditOriginal.setText(text);
	}

	@Override
	public void onResume() {
		super.onResume();

		boolean isPermissionRecordAudioGranted = ContextCompat.checkSelfPermission(getActivity(), permission.RECORD_AUDIO)
				== PackageManager.PERMISSION_GRANTED;

		mBtnMic.setActivated(isPermissionRecordAudioGranted);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Consts.RECOGNITION_REQUEST_CODE) {
			if(resultCode == RecognizerActivity.RESULT_OK && data != null) {
				String result = data.getStringExtra(RecognizerActivity.EXTRA_RESULT);
				mEditOriginal.append(result);
			} else if(resultCode == RecognizerActivity.RESULT_ERROR) {
				String error = ((Error) data.getSerializableExtra(RecognizerActivity.EXTRA_ERROR)).getString();
				Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
			}
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onTranslateEvent(TranslateEvent event) {
		saveTranslation();
		changeOriginalText(event.text);
	}
}
