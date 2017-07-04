package com.bacecek.translate.ui.choose_language;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entity.Language;
import com.bacecek.translate.mvp.choose_language.ChooseLanguagePresenter;
import com.bacecek.translate.mvp.choose_language.ChooseLanguageView;
import com.bacecek.translate.util.Consts.Extra;
import com.bacecek.translate.util.adapter.LanguagesAdapter;
import com.bacecek.translate.util.adapter.LanguagesAdapter.OnItemClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;
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

		DividerItemDecoration divider = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
		divider.setDrawable(getResources().getDrawable(R.drawable.list_langs_divider));

		mRecyclerRecentlyUsed.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
		mRecyclerRecentlyUsed.setHasFixedSize(true);
		mRecyclerRecentlyUsed.setNestedScrollingEnabled(false);
		mRecyclerRecentlyUsed.addItemDecoration(divider);

		mRecyclerAll.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
		mRecyclerAll.setHasFixedSize(true);
		mRecyclerAll.setNestedScrollingEnabled(false);
		mRecyclerAll.addItemDecoration(divider);

		//какого-то **** скроллится при открытии списка языков. я не понимаю почему, это вроде помогает
		mScrollView.scrollTo(0, 0);
	}

	@Override
	public void setRecentlyUsedVisibility(boolean visible) {
		mViewRecentlyUsed.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setRecentlyUsedLanguages(RealmResults<Language> languages) {
		mRecentlyLangsAdapter = new LanguagesAdapter(getApplicationContext(), languages, mOnItemClickListener);
		mRecentlyLangsAdapter.registerAdapterDataObserver(mLangsDataObserver);
		mRecyclerRecentlyUsed.setAdapter(mRecentlyLangsAdapter);
		mLangsDataObserver.onChanged();
	}

	@Override
	public void setAllLanguages(RealmResults<Language> languages) {
		String currentLang = getIntent().getStringExtra(Extra.EXTRA_CHOOSE_LANG_CURRENT);
		LanguagesAdapter allAdapter = new LanguagesAdapter(getApplicationContext(), languages, mOnItemClickListener, currentLang);
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
