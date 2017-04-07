package com.bacecek.translate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.WindowManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.translate.R;
import com.bacecek.translate.ui.views.AutoResizeTextView;
import com.bacecek.translate.utils.Consts;

public class FullscreenTextActivity extends AppCompatActivity {
	@BindView(R.id.txt_fullscreen)
	AutoResizeTextView mTxtFullscreen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreen_text);
		ButterKnife.bind(this);
		initUi();

		Intent intent = getIntent();
		String text = intent.getStringExtra(Consts.EXTRA_FULLSCREEN);
		if(text != null) {
			mTxtFullscreen.setText(text);
		}
	}

	private void initUi() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		mTxtFullscreen.setMinTextSize(TypedValue.COMPLEX_UNIT_SP, Consts.MIN_TEXTSIZE_FULLSCREEN);
		mTxtFullscreen.setMovementMethod(ScrollingMovementMethod.getInstance());
	}
}
