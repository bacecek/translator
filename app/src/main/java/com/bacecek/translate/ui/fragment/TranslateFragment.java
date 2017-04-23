package com.bacecek.translate.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entity.DictionaryItem;
import com.bacecek.translate.data.entity.Translation;
import com.bacecek.translate.mvp.presenter.TranslatePresenter;
import com.bacecek.translate.mvp.view.TranslateView;
import com.bacecek.translate.ui.activity.ChooseLanguageActivity;
import com.bacecek.translate.ui.activity.FullscreenTextActivity;
import com.bacecek.translate.ui.adapter.DictionaryAdapter;
import com.bacecek.translate.ui.adapter.DictionaryAdapter.OnWordClickListener;
import com.bacecek.translate.ui.adapter.HistoryAdapter;
import com.bacecek.translate.ui.adapter.HistoryAdapter.OnItemClickListener;
import com.bacecek.translate.ui.widget.CustomEditText;
import com.bacecek.translate.ui.widget.ErrorView;
import com.bacecek.translate.ui.widget.VocalizeButton;
import com.bacecek.translate.util.Consts;
import com.bacecek.translate.util.Consts.Extra;
import com.bacecek.translate.util.Utils;
import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;
import java.util.List;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Recognizer.Model;
import ru.yandex.speechkit.gui.RecognizerActivity;

/**
 * Created by Denis Buzmakov on 17/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class TranslateFragment extends BaseFragment implements TranslateView{
	@InjectPresenter
	TranslatePresenter mPresenter;

	@BindView(R.id.edit_original_text)
	CustomEditText mEditOriginal;
	@BindView(R.id.btn_clear)
	ImageButton mBtnClear;
	@BindView(R.id.btn_mic)
	ImageButton mBtnMic;
	@BindView(R.id.btn_favourite)
	ImageButton mBtnFavourite;
	@BindView(R.id.btn_more)
	ImageButton mBtnMore;
	@BindView(R.id.btn_swap)
	ImageButton mBtnSwap;
	@BindView(R.id.btn_original_lang)
	Button mBtnOriginalLang;
	@BindView(R.id.btn_target_lang)
	Button mBtnTargetLang;
	@BindView(R.id.btn_vocalize_original)
	VocalizeButton mBtnVocalizeOriginal;
	@BindView(R.id.btn_vocalize_translated)
	VocalizeButton mBtnVocalizeTranslated;
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
	@BindView(R.id.view_progress_loading)
	ProgressBar mProgressBar;
	@BindView(R.id.view_error)
	ErrorView mErrorView;

	private HistoryAdapter mHistoryAdapter;

	@OnTextChanged(value = R.id.edit_original_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
	void onTextChanged(Editable s) {
		mPresenter.onInputChanged(getOriginalText());
	}

	private final OnItemClickListener mOnItemHistoryClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(Translation translation) {
			mPresenter.onClickHistoryItem(translation);
		}

		@Override
		public void onClickFavourite(Translation translation) {
			mPresenter.onClickHistoryFavourite(translation);
		}
	};

	private final OnWordClickListener mOnWordClickListener = word -> {
		mPresenter.saveTranslation(true);
		mPresenter.onClickDictionaryWord(word);
	};

	private final PopupMenu.OnMenuItemClickListener mOnMenuMoreItemClickListener = item -> {
		switch (item.getItemId()) {
			case R.id.action_copy:
				Utils.copyToClipboard(getActivity(), getTranslatedText());
				Toast.makeText(getActivity(), R.string.text_has_been_copied, Toast.LENGTH_SHORT).show();
				break;
			case R.id.action_share:
				Utils.shareText(getActivity(), getTranslatedText());
				break;
			case R.id.action_fullscreen:
				Intent intent = new Intent(getActivity(), FullscreenTextActivity.class);
				intent.putExtra(Extra.EXTRA_FULLSCREEN, getTranslatedText());
				startActivity(intent);
				break;
			case R.id.action_reverse_translate:
				mPresenter.onReverseTranslate(getTranslatedText());
				break;
		}
		return false;
	};

	private final ItemTouchHelper.Callback mHistoryItemSwipeCallback = new SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
		@Override
		public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
			return false;
		}

		@Override
		public void onSwiped(ViewHolder viewHolder, int direction) {
			int position = viewHolder.getAdapterPosition();
			Translation translation = null;
			OrderedRealmCollection data = mHistoryAdapter.getData();
			if(data != null) {
				translation = (Translation) data.get(position);
			}
			mPresenter.onHistoryItemSwipe(translation);
		}
	};

	private final OnEditorActionListener mOnKeyDoneListener = (view, actionId, keyEvent) -> {
		if (actionId == EditorInfo.IME_ACTION_DONE) {
			mPresenter.loadTranslation();
			return true;
		}
		return false;
	};

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_translate, container, false);
		ButterKnife.bind(this, parent);

		setTitle(parent, getString(R.string.app_name));
		initUI();
		initClickListeners();

		return parent;
	}

	private void initUI() {
		mRecyclerHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecyclerHistory.setNestedScrollingEnabled(false);
		mRecyclerHistory.setHasFixedSize(true);
		DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
		divider.setDrawable(getResources().getDrawable(R.drawable.list_divider_history));
		mRecyclerHistory.addItemDecoration(divider);
		ItemTouchHelper helper = new ItemTouchHelper(mHistoryItemSwipeCallback);
		helper.attachToRecyclerView(mRecyclerHistory);
		mRecyclerDictionary.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecyclerDictionary.setNestedScrollingEnabled(false);
		mProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, Mode.SRC_IN);
		mEditOriginal.setRawInputType(InputType.TYPE_CLASS_TEXT);
		mEditOriginal.setOnEditorActionListener(mOnKeyDoneListener);
	}

	private void initClickListeners() {
		mBtnOriginalLang.setOnClickListener(v -> mPresenter.onClickChooseOriginalLang());
		mBtnTargetLang.setOnClickListener(v -> mPresenter.onClickChooseTargetLang());
		mBtnFavourite.setOnClickListener(v -> mPresenter.onClickFavourite());
		mBtnClear.setOnClickListener(v -> mPresenter.onClickClear(mErrorView.getVisibility() == View.VISIBLE));
		mBtnMic.setOnClickListener(v -> mPresenter.onClickMic());
		mBtnVocalizeOriginal.setOnClickListener(v -> {
			VocalizeButton button = (VocalizeButton) v;
			mPresenter.onClickVocalizeOriginal(button.getState());
		});
		mBtnVocalizeTranslated.setOnClickListener(v -> {
			VocalizeButton button = (VocalizeButton) v;
			mPresenter.onClickVocalizeTranslated(button.getState());
		});
		mBtnMore.setOnClickListener(v -> {
			PopupMenu popupMenu = new PopupMenu(getActivity(), mBtnMore);
			popupMenu.getMenuInflater().inflate(R.menu.menu_more, popupMenu.getMenu());
			popupMenu.setOnMenuItemClickListener(mOnMenuMoreItemClickListener);
			popupMenu.show();
		});
		mBtnSwap.setOnClickListener(v -> mPresenter.onClickSwap());
		mErrorView.setOnClickListener(v -> mPresenter.onClickRetry());
	}

	private String getTranslatedText() {
		return mTxtTranslated.getText().toString();
	}

	private String getOriginalText() {
		return mEditOriginal.getText().toString().trim();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();
		mPresenter.saveTranslation(true);
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Consts.RECOGNITION_REQUEST_CODE) {
			if(resultCode == RecognizerActivity.RESULT_OK && data != null) {
				String result = data.getStringExtra(RecognizerActivity.EXTRA_RESULT);
				mPresenter.onDictationSuccess(result);
			} else if(resultCode == RecognizerActivity.RESULT_ERROR) {
				String error = ((Error) data.getSerializableExtra(RecognizerActivity.EXTRA_ERROR)).getString();
				Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
			}
		} else if(requestCode == Consts.CHOOSE_LANG_REQUEST_CODE) {
			if(resultCode == Activity.RESULT_OK) {
				String chosenLang = data.getStringExtra(Extra.EXTRA_CHOOSE_LANG_RETURN);
				int typeLang = data.getIntExtra(Extra.EXTRA_CHOOSE_LANG_TYPE, 0);
				if(typeLang == Consts.CHOOSE_LANG_TYPE_ORIGINAL) {
					mPresenter.onChooseOriginalLang(chosenLang);
				} else if(typeLang == Consts.CHOOSE_LANG_TYPE_TARGET) {
					mPresenter.onChooseTargetLang(chosenLang);
				}
			}
		}
	}

	@Override
	public void goToChooseOriginalLanguage(String currentLang) {
		Intent intent = new Intent(getActivity(), ChooseLanguageActivity.class);
		intent.putExtra(Extra.EXTRA_CHOOSE_LANG_TYPE, Consts.CHOOSE_LANG_TYPE_ORIGINAL);
		intent.putExtra(Extra.EXTRA_CHOOSE_LANG_CURRENT, currentLang);
		startActivityForResult(intent, Consts.CHOOSE_LANG_REQUEST_CODE);
	}

	@Override
	public void goToChooseTargetLanguage(String currentLang) {
		Intent intent = new Intent(getActivity(), ChooseLanguageActivity.class);
		intent.putExtra(Extra.EXTRA_CHOOSE_LANG_TYPE, Consts.CHOOSE_LANG_TYPE_TARGET);
		intent.putExtra(Extra.EXTRA_CHOOSE_LANG_CURRENT, currentLang);
		startActivityForResult(intent, Consts.CHOOSE_LANG_REQUEST_CODE);
	}

	@Override
	public void setProgressVisibility(boolean visible) {
		mProgressBar.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
	}

	@Override
	public void setErrorVisibility(boolean visible) {
		mErrorView.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setHistoryVisibility(boolean visible) {
		mRecyclerHistory.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setTranslationVisibility(boolean visible) {
		mViewTranslated.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setDictionaryVisibility(boolean visible) {
		mViewDictionary.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setButtonClearVisibility(boolean visible) {
		mBtnClear.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setButtonVocalizeVisibility(boolean visible) {
		mBtnVocalizeOriginal.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setErrorData(Throwable error) {
		mErrorView.setError(error);
	}

	@Override
	public void setHistoryData(RealmResults<Translation> history) {
		mHistoryAdapter = new HistoryAdapter(getActivity(), history, mOnItemHistoryClickListener);
		mRecyclerHistory.setAdapter(mHistoryAdapter);
	}

	@Override
	public void setTranslationData(Translation translation) {
		mTxtTranslated.setText(translation.getTranslatedText());
		mBtnFavourite.setActivated(translation.isFavourite());
	}

	@Override
	public void setDictionaryData(List<DictionaryItem> items) {
		DictionaryAdapter adapter = new DictionaryAdapter(items, mOnWordClickListener);
		mRecyclerDictionary.setAdapter(adapter);
	}

	@Override
	public void setOriginalLangName(String name) {
		mBtnOriginalLang.setText(name);
	}

	@Override
	public void setTargetLangName(String name) {
		mBtnTargetLang.setText(name);
	}

	@Override
	public void setOriginalText(String text) {
		mEditOriginal.setText("");
		mEditOriginal.setText(text);
		mEditOriginal.setSelection(mEditOriginal.getText().length());
	}

	@Override
	public void startDictation(String originalLang) {
		Intent intent = new Intent(getActivity(), RecognizerActivity.class);
		intent.putExtra(RecognizerActivity.EXTRA_MODEL, Model.QUERIES);
		intent.putExtra(RecognizerActivity.EXTRA_LANGUAGE, originalLang);
		startActivityForResult(intent, Consts.RECOGNITION_REQUEST_CODE);
	}

	@Override
	public void setTranslationFavourite(boolean isFavourite) {
		mBtnFavourite.setActivated(isFavourite);
	}

	@Override
	public void setOriginalVocalizeButtonState(int state) {
		mBtnVocalizeOriginal.setState(state);
	}

	@Override
	public void setTranslatedVocalizeButtonState(int state) {
		mBtnVocalizeTranslated.setState(state);
	}

	@Override
	public void showToast(String text) {
		Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void setOriginalVocalizeButtonEnabled(boolean enabled) {
		mBtnVocalizeOriginal.setEnabled(enabled);
	}

	@Override
	public void setTranslatedVocalizeButtonEnabled(boolean enabled) {
		mBtnVocalizeTranslated.setEnabled(enabled);
	}

	@Override
	public void setMicButtonEnabled(boolean enabled) {
		mBtnMic.setEnabled(enabled);
	}

	@Override
	public void setInputImeOptions(int imeOptions) {
		removeFocusFromInput();
		mEditOriginal.setImeOptions(imeOptions);
	}

	private void removeFocusFromInput() {
		//очистка фокуса. каким-то образом другие методы не работали, а этот костылик работает:)
		mEditOriginal.setFocusableInTouchMode(false);
		mEditOriginal.setFocusable(false);
		mEditOriginal.setFocusableInTouchMode(true);
		mEditOriginal.setFocusable(true);
	}
}
