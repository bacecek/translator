package com.bacecek.translate.ui.fullscreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.WindowManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.translate.R;
import com.bacecek.translate.util.widget.AutoResizeTextView;
import com.bacecek.translate.util.Consts;
import com.bacecek.translate.util.Consts.Extra;

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
		String text = intent.getStringExtra(Extra.EXTRA_FULLSCREEN);
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
