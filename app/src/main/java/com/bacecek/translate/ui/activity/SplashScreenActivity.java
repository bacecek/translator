package com.bacecek.translate.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bacecek.translate.R;
import com.bacecek.translate.mvp.presenter.SplashScreenPresenter;
import com.bacecek.translate.mvp.view.SplashScreenView;

public class SplashScreenActivity extends MvpAppCompatActivity implements SplashScreenView {
	@InjectPresenter
	SplashScreenPresenter mPresenter;

	@BindView(R.id.view_error)
	View mViewError;
	@BindView(R.id.view_loading)
	View mViewLoading;
	@BindView(R.id.view_progress)
	ProgressBar mProgressBar;

	@OnClick(R.id.btn_retry)
	void onClickRetry() {
		mPresenter.loadLangs();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		ButterKnife.bind(this);
		initUI();
	}

	private void initUI() {
		mProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, Mode.MULTIPLY);
	}

	@Override
	public void setErrorVisibility(boolean visible) {
		mViewError.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setLoadingVisibility(boolean visible) {
		mViewLoading.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void goToMainScreen() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
		finish();
	}
}
