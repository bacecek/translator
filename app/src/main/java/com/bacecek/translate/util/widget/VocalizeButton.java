package com.bacecek.translate.util.widget;

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

/**
 * Класс для показа разных состояний кнопки озвучки - инициализация, начать озвучку и остановить
 */
public class VocalizeButton extends FrameLayout {
	public static final int STATE_PLAY = 0;
	public static final int STATE_INIT = 1;
	public static final int STATE_STOP = 2;

	@BindView(R.id.img_play)
	ImageView mImgPlay;
	@BindView(R.id.img_stop)
	ImageView mImgStop;
	@BindView(R.id.view_progress)
	ProgressBar mProgressBar;

	private int mCurrentState;

	public VocalizeButton(@NonNull Context context) {
		super(context);
		init(context, null);
	}

	public VocalizeButton(@NonNull Context context,
			@Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public VocalizeButton(@NonNull Context context,
			@Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		LayoutInflater.from(context).inflate(R.layout.view_vocalize, this, true);
		ButterKnife.bind(this);

		if(attrs != null) {
			TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.tint}, 0, 0);
			ColorStateList tint = a.getColorStateList(0);
			if (tint != null) {
				mImgPlay.setColorFilter(tint.getDefaultColor());
				mImgStop.setColorFilter(tint.getDefaultColor());
				mProgressBar.getIndeterminateDrawable().setColorFilter(tint.getDefaultColor(), Mode.MULTIPLY);
			}
			a.recycle();
		}
	}

	public void setState(int state) {
		switch (state) {
			case STATE_PLAY:
				mImgPlay.setVisibility(VISIBLE);
				mImgStop.setVisibility(GONE);
				mProgressBar.setVisibility(GONE);
				break;
			case STATE_INIT:
				mImgPlay.setVisibility(GONE);
				mImgStop.setVisibility(GONE);
				mProgressBar.setVisibility(VISIBLE);
				break;
			case STATE_STOP:
				mImgPlay.setVisibility(GONE);
				mImgStop.setVisibility(VISIBLE);
				mProgressBar.setVisibility(GONE);
				break;
		}
		mCurrentState = state;
	}

	public int getState() {
		return mCurrentState;
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		mImgPlay.setEnabled(enabled);
	}
}
