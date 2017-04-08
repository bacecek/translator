package com.bacecek.translate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bacecek.translate.R;
import com.bacecek.translate.data.db.LanguageManager;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.ui.adapters.LanguagesAdapter;
import com.bacecek.translate.ui.adapters.LanguagesAdapter.OnItemClickListener;
import com.bacecek.translate.utils.Consts;
import timber.log.Timber;

public class ChooseLanguageActivity extends AppCompatActivity {
	@BindView(R.id.toolbar)
	Toolbar mToolbar;
	@BindView(R.id.txt_detect_lang)
	AppCompatTextView mTxtDetectLang;
	@BindView(R.id.view_recently)
	View mViewRecentlyUsed;
	@BindView(R.id.view_all)
	View mViewAll;
	@BindView(R.id.list_recently_langs)
	RecyclerView mRecyclerRecentlyUsed;
	@BindView(R.id.list_all_langs)
	RecyclerView mRecyclerAll;
	@BindView(R.id.scrollview)
	NestedScrollView mScrollView;

	private boolean mDetectLang;

	@OnClick(R.id.txt_detect_lang)
	void onClickDetect(View view) {
		AppCompatTextView textView = (AppCompatTextView) view;
		chooseLanguageAndFinish(textView.getText().toString());
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(String lang) {
			chooseLanguageAndFinish(lang);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_language);
		ButterKnife.bind(this);
		initUi();
	}

	private void initUi() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.title_choose_lang);
		mToolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		Intent intent = getIntent();
		mDetectLang = intent.getBooleanExtra(Consts.EXTRA_CHOOSE_LANG_DETECT, false);
		mTxtDetectLang.setVisibility(mDetectLang ? View.VISIBLE : View.GONE);
		String currentLang = intent.getStringExtra(Consts.EXTRA_CHOOSE_LANG_CURRENT);

		mRecyclerRecentlyUsed.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
		mRecyclerRecentlyUsed.setHasFixedSize(true);
		mRecyclerRecentlyUsed.setNestedScrollingEnabled(false);
		LanguagesAdapter recentlyAdapter = new LanguagesAdapter(getApplicationContext(),
				RealmController.getInstance().getRecentlyUsedLanguages(),
				mOnItemClickListener);
		mRecyclerRecentlyUsed.setAdapter(recentlyAdapter);
		mRecyclerAll.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
		mRecyclerAll.setHasFixedSize(true);
		mRecyclerAll.setNestedScrollingEnabled(false);
		LanguagesAdapter allAdapter = new LanguagesAdapter(getApplicationContext(),
				RealmController.getInstance().getLanguagesAsync(),
				mOnItemClickListener,
				currentLang);
		Timber.d(String.valueOf(System.currentTimeMillis()));
		mRecyclerAll.setAdapter(allAdapter);
		Timber.d(String.valueOf(System.currentTimeMillis()));

		mScrollView.scrollTo(0, 0);
	}

	private void chooseLanguageAndFinish(String lang) {
		if(mDetectLang) {
			LanguageManager.getInstance().setCurrentOriginalLangCode(lang);
		} else {
			LanguageManager.getInstance().setCurrentTargetLangCode(lang);
		}
		finish();
	}
}
