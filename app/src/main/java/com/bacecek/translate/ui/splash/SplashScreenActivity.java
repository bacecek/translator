package com.bacecek.translate.ui.splash;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bacecek.translate.R;
import com.bacecek.translate.mvp.splash.SplashScreenPresenter;
import com.bacecek.translate.mvp.splash.SplashScreenView;
import com.bacecek.translate.ui.main.MainActivity;
import com.bacecek.translate.util.Consts.Extra;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

		Intent intent = getIntent();
		if (Intent.ACTION_SEND.equals(intent.getAction()) && intent.getType() != null) {
			String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
			if (sharedText != null) {
				mPresenter.incomingTranslation(sharedText);
			}
		}
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
	public void goToMainScreen(String text) {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		intent.putExtra(Extra.EXTRA_INCOMING_TRANSLATION, text);
		startActivity(intent);
		finish();
	}
}
