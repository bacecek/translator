package com.bacecek.translate.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entities.Error;
import com.bacecek.translate.data.network.RetrofitException;
import java.io.IOException;

/**
 * Created by Denis Buzmakov on 13/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class ErrorView extends FrameLayout {
	@BindView(R.id.btn_retry)
	AppCompatButton mBtnRetry;
	@BindView(R.id.txt_error_subtitle)
	TextView mTxtErrorSubtitle;

	public ErrorView(@NonNull Context context) {
		super(context);
		init(context);
	}

	public ErrorView(@NonNull Context context,
			@Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ErrorView(@NonNull Context context,
			@Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		View parent = LayoutInflater.from(context).inflate(R.layout.view_error_translate, this, true);
		ButterKnife.bind(this, parent);
	}

	public void setError(Throwable throwable) {
		RetrofitException exception = (RetrofitException) throwable;
		Error error;
		try {
			error = exception.getErrorBodyAs(Error.class);
			switch (exception.getKind()) {
				case HTTP:
					mTxtErrorSubtitle.setText(error.getCode() + ": " + error.getMessage());
					break;
				case NETWORK:
					mTxtErrorSubtitle.setText(R.string.error_bad_network);
					break;
				case UNEXPECTED:
					mTxtErrorSubtitle.setText(R.string.error_unexpected);
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
			mTxtErrorSubtitle.setText(R.string.error_unexpected);
		}
	}

	@Override
	public void setOnClickListener(@Nullable OnClickListener l) {
		mBtnRetry.setOnClickListener(l);
	}
}
