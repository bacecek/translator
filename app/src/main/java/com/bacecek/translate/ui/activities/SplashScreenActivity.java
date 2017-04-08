package com.bacecek.translate.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bacecek.translate.R;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entities.Language;
import com.bacecek.translate.data.network.APIGenerator;
import com.bacecek.translate.data.network.TranslatorAPI;
import io.realm.RealmList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {
	@BindView(R.id.view_error)
	View mViewError;
	@BindView(R.id.view_loading)
	View mViewLoading;
	@BindView(R.id.view_progress)
	ProgressBar mProgressBar;

	private Call<List<Language>> mLoadLanguageCall;

	@OnClick(R.id.btn_retry)
	void onClickRetry() {
		mViewLoading.setVisibility(View.VISIBLE);
		mViewError.setVisibility(View.GONE);
		loadLangs();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		ButterKnife.bind(this);
		initUI();

		if(RealmController.getInstance().getLanguages().size() > 0) {
			goToMainScreen();
		} else {
			mViewLoading.setVisibility(View.VISIBLE);
			loadLangs();
		}
	}

	private void initUI() {
		mProgressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, Mode.MULTIPLY);
	}

	private void goToMainScreen() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		startActivity(intent);
		finish();
	}

	private void loadLangs() {
		TranslatorAPI api = APIGenerator.createTranslatorService();
		mLoadLanguageCall = api.getLangs("ru");
		mLoadLanguageCall.enqueue(new Callback<List<Language>>() {
			@Override
			public void onResponse(Call<List<Language>> call, final Response<List<Language>> response) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mViewLoading.setVisibility(View.GONE);
						RealmList<Language> list = new RealmList<Language>();
						list.addAll(response.body());
						RealmController.getInstance().insertLanguages(list);
						goToMainScreen();
					}
				});
			}

			@Override
			public void onFailure(Call<List<Language>> call, Throwable t) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mViewLoading.setVisibility(View.GONE);
						mViewError.setVisibility(View.VISIBLE);
					}
				});
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mLoadLanguageCall != null) {
			mLoadLanguageCall.cancel();
		}
	}
}
