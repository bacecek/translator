package com.bacecek.translate.ui.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.translate.R;

/**
 * Created by Denis Buzmakov on 04/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class ListenButton extends FrameLayout {

	public static final int STATE_PLAY = 0;
	public static final int STATE_INIT = 1;
	public static final int STATE_STOP = 2;

	@BindView(R.id.img_listen)
	ImageView mImgListen;
	@BindView(R.id.img_stop)
	ImageView mImgStop;
	@BindView(R.id.view_progress)
	ProgressBar mProgressBar;

	private int mCurrentState;

	public ListenButton(@NonNull Context context) {
		super(context);
		init(context, null);
	}

	public ListenButton(@NonNull Context context,
			@Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ListenButton(@NonNull Context context,
			@Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		LayoutInflater.from(context).inflate(R.layout.view_listen, this, true);
		ButterKnife.bind(this);

		if(attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.tint}, 0, 0);
			ColorStateList tint = a.getColorStateList(0);
			if (tint != null) {
				mImgListen.setColorFilter(tint.getDefaultColor());
				mImgStop.setColorFilter(tint.getDefaultColor());
				mProgressBar.getIndeterminateDrawable().setColorFilter(tint.getDefaultColor(), Mode.MULTIPLY);
			}
			a.recycle();
		}
	}

	public void setState(int state) {
		switch (state) {
			case STATE_PLAY:
				mImgListen.setVisibility(VISIBLE);
				mImgStop.setVisibility(GONE);
				mProgressBar.setVisibility(GONE);
				mCurrentState = state;
				break;
			case STATE_INIT:
				mImgListen.setVisibility(GONE);
				mImgStop.setVisibility(GONE);
				mProgressBar.setVisibility(VISIBLE);
				mCurrentState = state;
				break;
			case STATE_STOP:
				mImgListen.setVisibility(GONE);
				mImgStop.setVisibility(VISIBLE);
				mProgressBar.setVisibility(GONE);
				mCurrentState = state;
				break;
		}
	}

	public int getState() {
		return mCurrentState;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mImgListen.setEnabled(enabled);
	}
}
