package com.bacecek.translate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
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

	private int mChooseLangType;

	private LanguagesAdapter mRecentlyLangsAdapter;

	private final OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(String lang) {
			chooseLanguageAndFinish(lang);
		}
	};

	private final AdapterDataObserver mLangsDataObserver = new AdapterDataObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			if(mRecentlyLangsAdapter.getItemCount() == 0) {
				mViewRecentlyUsed.setVisibility(View.GONE);
			} else {
				mViewRecentlyUsed.setVisibility(View.VISIBLE);
			}
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
		String currentLang = intent.getStringExtra(Consts.EXTRA_CHOOSE_LANG_CURRENT);
		mChooseLangType = intent.getIntExtra(Consts.EXTRA_CHOOSE_LANG_TYPE, 0);

		mRecyclerRecentlyUsed.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
		mRecyclerRecentlyUsed.setHasFixedSize(true);
		mRecyclerRecentlyUsed.setNestedScrollingEnabled(false);
		mRecentlyLangsAdapter = new LanguagesAdapter(getApplicationContext(),
				RealmController.getInstance().getRecentlyUsedLanguages(),
				mOnItemClickListener);
		mRecentlyLangsAdapter.registerAdapterDataObserver(mLangsDataObserver);
		mLangsDataObserver.onChanged();
		mRecyclerRecentlyUsed.setAdapter(mRecentlyLangsAdapter);
		mRecyclerAll.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
		mRecyclerAll.setHasFixedSize(true);
		mRecyclerAll.setNestedScrollingEnabled(false);
		LanguagesAdapter allAdapter = new LanguagesAdapter(getApplicationContext(),
				RealmController.getInstance().getLanguagesAsync(),
				mOnItemClickListener,
				currentLang);
		mRecyclerAll.setAdapter(allAdapter);

		mScrollView.scrollTo(0, 0);
	}

	private void chooseLanguageAndFinish(String lang) {
		if(mChooseLangType == Consts.CHOOSE_LANG_TYPE_ORIGINAL) {
			LanguageManager.getInstance().setCurrentOriginalLangCode(lang);
		} else if (mChooseLangType == Consts.CHOOSE_LANG_TYPE_TARGET){
			LanguageManager.getInstance().setCurrentTargetLangCode(lang);
		}
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mRecentlyLangsAdapter.unregisterAdapterDataObserver(mLangsDataObserver);
	}
}
