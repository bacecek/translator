package com.bacecek.translate.ui.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.KeyEvent;

/**
 * Created by Denis Buzmakov on 20/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class CustomEditText extends AppCompatEditText {
	private OnKeyBackDownListener mListener;

	public CustomEditText(Context context) {
		super(context);
	}

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomEditText(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setKeyBackDownListener(OnKeyBackDownListener listener) {
		mListener = listener;
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP && mListener != null) {
			mListener.onKeyBackDown();
		}
		return super.dispatchKeyEvent(event);
	}

	public interface OnKeyBackDownListener {
		void onKeyBackDown();
	}
}
