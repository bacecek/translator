package com.bacecek.yandextranslate.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.yandextranslate.R;

/**
 * Created by Denis Buzmakov on 20/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class ListenButton extends FrameLayout {
	@BindView(R.id.txt_lang)
	TextView mTxtLang;
	@BindView(R.id.icon)
	ImageView mIcon;
	@BindView(R.id.view_progress)
	ProgressBar mProgressBar;

	public ListenButton(@NonNull Context context) {
		super(context);
		init(context, null);
	}

	public ListenButton(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ListenButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	@TargetApi(VERSION_CODES.LOLLIPOP)
	public ListenButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		LayoutInflater.from(context).inflate(R.layout.view_listen, this, true);
		ButterKnife.bind(this);

		if(attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.tint}, 0, 0);
			ColorStateList tint = a.getColorStateList(0);
			if (tint != null) {
				mIcon.setColorFilter(tint.getDefaultColor());
				mTxtLang.setTextColor(tint.getDefaultColor());
			}
			a.recycle();
		}
	}

	public void startListening(String text) {
		mIcon.setImageResource(R.drawable.ic_stop);
	}

	public void stopListening() {
		mIcon.setImageResource(R.drawable.ic_play);
	}

	public void setLanguage(String lang) {
		mTxtLang.setText(lang);
	}

	public String getLanguage() {
		return mTxtLang.getText().toString().toLowerCase();
	}
}
