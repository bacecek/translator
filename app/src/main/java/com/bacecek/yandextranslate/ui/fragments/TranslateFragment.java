package com.bacecek.yandextranslate.ui.fragments;

import android.app.Fragment;
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
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.bacecek.yandextranslate.R;
import com.bacecek.yandextranslate.data.db.RealmController;
import com.bacecek.yandextranslate.data.db.entities.Translation;
import com.bacecek.yandextranslate.data.network.APIGenerator;
import com.bacecek.yandextranslate.data.network.DictionaryAPI;
import com.bacecek.yandextranslate.data.network.TranslatorAPI;
import com.bacecek.yandextranslate.ui.adapters.HistoryAdapter;
import com.bacecek.yandextranslate.utils.HistoryDismissTouchHelper;
import com.bacecek.yandextranslate.utils.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Denis Buzmakov on 17/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class TranslateFragment extends Fragment{
	@BindView(R.id.edit_original_text)
	EditText mEditOriginal;
	@BindView(R.id.btn_clear)
	ImageButton mBtnClear;
	@BindView(R.id.btn_mic)
	ImageButton mBtnMic;
	@BindView(R.id.btn_listen)
	ImageButton mBtnListen;
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
	private static int DELAY_INPUT = 700;
	private Handler mDelayInputHandler = new Handler(Looper.getMainLooper());
	private Runnable mDelayInputRunnable;

	@OnClick(R.id.btn_clear)
	void onClickClear() {
		mEditOriginal.setText("");
	}

	@OnClick(R.id.btn_mic)
	void onClickMic() {

	}

	@OnTextChanged(value = R.id.edit_original_text, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
	void onTextChanged(Editable s) {
		if(s.length() == 0) {
			mBtnClear.setVisibility(View.INVISIBLE);
			mBtnListen.setVisibility(View.INVISIBLE);
			mViewTranslated.setVisibility(View.INVISIBLE);
			mRecyclerHistory.setVisibility(View.VISIBLE);
		} else {
			mBtnClear.setVisibility(View.VISIBLE);
			mBtnListen.setVisibility(View.VISIBLE);
			mRecyclerHistory.setVisibility(View.GONE);
		}

		mDelayInputHandler.removeCallbacks(mDelayInputRunnable);
		mDelayInputRunnable = new Runnable() {
			@Override
			public void run() {
				if(mEditOriginal.getText().length() != 0) {
					loadTranslation();
				}
			}
		};
		mDelayInputHandler.postDelayed(mDelayInputRunnable, DELAY_INPUT);
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_translate, container, false);
		ButterKnife.bind(this, parent);
		mTranslatorAPI = APIGenerator.createTranslatorService();
		mDictionaryAPI = APIGenerator.createDictionaryService();
		initUI();
		return parent;
	}

	private void initUI() {
		mRecyclerHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
		HistoryAdapter adapter = new HistoryAdapter(
				getActivity(),
				RealmController.getInstance().getHistory(),
				null);
		mRecyclerHistory.setAdapter(adapter);
		mRecyclerHistory.setNestedScrollingEnabled(false);
		mRecyclerHistory.setHasFixedSize(true);
		DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
		divider.setDrawable(getResources().getDrawable(R.drawable.list_divider));
		mRecyclerHistory.addItemDecoration(divider);
		ItemTouchHelper.Callback callback = new HistoryDismissTouchHelper(adapter);
		ItemTouchHelper helper = new ItemTouchHelper(callback);
		helper.attachToRecyclerView(mRecyclerHistory);
	}

	private void loadTranslation() {
		Call<Translation> call = mTranslatorAPI.translate(mEditOriginal.getText().toString(), "en");
		call.enqueue(new Callback<Translation>() {
			@Override
			public void onResponse(Call<Translation> call,
					Response<Translation> response) {
				mViewTranslated.setVisibility(View.VISIBLE);
				mTxtTranslated.setText(response.body().getTranslatedText());
			}

			@Override
			public void onFailure(Call<Translation> call, Throwable t) {

			}
		});
	}

	@Override
	public void onStop() {
		super.onStop();
		Utils.hideKeyboard(getActivity());
	}
}
