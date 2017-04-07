package com.bacecek.translate.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.translate.R;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entities.Language;

/**
 * Created by Denis Buzmakov on 07/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class LanguagesButton extends FrameLayout {
	@BindView(R.id.btn_swap)
	ImageButton mBtnSwap;
	@BindView(R.id.btn_original_lang)
	Button mBtnOriginalLang;
	@BindView(R.id.btn_target_lang)
	Button mBtnTargetLang;

	public LanguagesButton(@NonNull Context context) {
		super(context);
		init(context);
	}

	public LanguagesButton(@NonNull Context context,
			@Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public LanguagesButton(@NonNull Context context,
			@Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		View parent = LayoutInflater.from(context).inflate(R.layout.view_languages, this, true);
		ButterKnife.bind(this, parent);
	}

	public void setOriginalLang(String codeLang) {
		Language originalLang = RealmController.getInstance().getLanguageByCode(codeLang);
		if(originalLang != null) {
			mBtnOriginalLang.setText(originalLang.getName());
		}
	}

	public void setTargetLang(String codeLang) {
		Language targetLang = RealmController.getInstance().getLanguageByCode(codeLang);
		if(targetLang != null) {
			mBtnTargetLang.setText(targetLang.getName());
		}
	}
}
