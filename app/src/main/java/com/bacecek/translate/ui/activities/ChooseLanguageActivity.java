package com.bacecek.translate.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.Toolbar;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entities.Language;
import com.bacecek.translate.mvp.presenters.ChooseLanguagePresenter;
import com.bacecek.translate.mvp.views.ChooseLanguageView;
import com.bacecek.translate.ui.adapters.LanguagesAdapter;
import com.bacecek.translate.ui.adapters.LanguagesAdapter.OnItemClickListener;
import com.bacecek.translate.utils.Consts.Extra;
import io.realm.RealmResults;

public class ChooseLanguageActivity extends MvpAppCompatActivity implements ChooseLanguageView {
	@InjectPresenter
	ChooseLanguagePresenter mPresenter;

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

	private LanguagesAdapter mRecentlyLangsAdapter;

	private final OnItemClickListener mOnItemClickListener = lang -> mPresenter.onItemClick(lang);

	private final AdapterDataObserver mLangsDataObserver = new AdapterDataObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			mPresenter.datasetRecentlyUsedChanged(mRecentlyLangsAdapter.getItemCount());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_language);
		ButterKnife.bind(this);
		initUi();
		mPresenter.setChooseLangType(getIntent().getIntExtra(Extra.EXTRA_CHOOSE_LANG_TYPE, 0));
	}

	private void initUi() {
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.title_choose_lang);
		mToolbar.setNavigationOnClickListener(view -> onBackPressed());

		mRecyclerRecentlyUsed.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
		mRecyclerRecentlyUsed.setHasFixedSize(true);
		mRecyclerRecentlyUsed.setNestedScrollingEnabled(false);

		mRecyclerAll.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
		mRecyclerAll.setHasFixedSize(true);
		mRecyclerAll.setNestedScrollingEnabled(false);

		mScrollView.scrollTo(0, 0);
	}

	@Override
	public void showRecentlyUsed() {
		mViewRecentlyUsed.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideRecentlyUsed() {
		mViewRecentlyUsed.setVisibility(View.GONE);
	}

	@Override
	public void setRecentlyUsedLanguages(RealmResults<Language> languages) {
		mRecentlyLangsAdapter = new LanguagesAdapter(languages, mOnItemClickListener);
		mRecentlyLangsAdapter.registerAdapterDataObserver(mLangsDataObserver);
		mRecyclerRecentlyUsed.setAdapter(mRecentlyLangsAdapter);
		mLangsDataObserver.onChanged();
	}

	@Override
	public void setAllLanguages(RealmResults<Language> languages) {
		String currentLang = getIntent().getStringExtra(Extra.EXTRA_CHOOSE_LANG_CURRENT);
		LanguagesAdapter allAdapter = new LanguagesAdapter(languages, mOnItemClickListener, currentLang);
		mRecyclerAll.setAdapter(allAdapter);
	}

	@Override
	public void setResultAndFinish(String lang, int langType) {
		Intent data = new Intent();
		data.putExtra(Extra.EXTRA_CHOOSE_LANG_TYPE, langType);
		data.putExtra(Extra.EXTRA_CHOOSE_LANG_RETURN, lang);
		setResult(Activity.RESULT_OK, data);
		finish();
	}
}
