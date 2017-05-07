package com.bacecek.translate.mvp.about;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.bacecek.translate.BuildConfig;
import com.bacecek.translate.R;
import com.bacecek.translate.mvp.base.BaseFragment;
import com.bacecek.translate.util.Utils;

/**
 * Created by Denis Buzmakov on 19/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class AboutFragment extends BaseFragment {
	@BindView(R.id.txt_version)
	TextView mTxtVersion;
	@BindView(R.id.img_logo)
	ImageView mImgLogo;

	private int mClickCount = 0; //а не скажу зачем оно надо =P
	private int mLongClickCount = 0;

	//это приколюха. ну весело же:)
	@OnClick(R.id.img_logo)
	void onClickLogo() {
		mClickCount++;
		if(mClickCount == 5) {
			Toast.makeText(getActivity(), "Зажать нужно!", Toast.LENGTH_SHORT).show();
		} else if(mClickCount == 10) {
			Toast.makeText(getActivity(), "Ну сказал же, ЗАЖАТЬ!1!", Toast.LENGTH_SHORT).show();
		}
	}

	@OnLongClick(R.id.img_logo)
	boolean onLongClickLogo() {
		mLongClickCount++;
		if(mLongClickCount < 2) {
			Toast.makeText(getActivity(), "Думал, зажал и тебе приколюха какая-то будет? Хе-хе:)",
					Toast.LENGTH_SHORT).show();
		} else {
			if(mLongClickCount == 2) {
				Toast.makeText(getActivity(), "Ну лаааадно, держи приколюху:)\nЗа такое грех в школу не взять;)",
						Toast.LENGTH_SHORT).show();
			}
			ViewPropertyAnimator animation = mImgLogo.animate()
					.setDuration(500)
					.setInterpolator(new FastOutSlowInInterpolator());
			if(mLongClickCount % 2 == 0) {
				animation.rotationBy(360);
			} else {
				animation.rotationBy(-360);
			}
			animation.start();
		}
		return true;
	}

	@OnClick(R.id.txt_source_code)
	void goToSourceCode() {
		goToSite(getString(R.string.about_source_code));
	}
	@OnClick(R.id.txt_contact_me)
	void sendEmail() {
		//Собираем строку для интента
		String uriText =
				"mailto:" + getString(R.string.about_email) +
						"?subject=" + Uri.encode(getString(R.string.app_name)) +
						"&body=" + Uri.encode("");
		//Парсим строку для интента
		Uri uri = Uri.parse(uriText);
		Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
		//Пихаем строку в интент
		sendIntent.setData(uri);
		try {
			startActivity(Intent.createChooser(sendIntent, getString(R.string.about_contact_via)));
		} catch (ActivityNotFoundException e) {
			//Не ну вдруг у пользователя нет приложения почты
			Toast.makeText(getActivity(), R.string.error_no_email_app, Toast.LENGTH_SHORT).show();
			Utils.copyToClipboard(getActivity(), getString(R.string.about_email));
		}
	}
	@OnClick(R.id.txt_site)
	void goToMySite() {
		Toast.makeText(getActivity(),
				"Когда-нибудь у меня будет свой сайт. Но это уже совсем другая история...",
				Toast.LENGTH_SHORT).show();
		//когда-нибудь здесь будет мой сайт, стопудово
		//goToSite(getString(R.string.about_site));
	}



	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_about, container, false);

		ButterKnife.bind(this, parent);
		setTitle(parent, getString(R.string.action_about));

		initUI();

		return parent;
	}

	private void initUI() {
		String version = getString(R.string.about_version_prefix)
				+ " "
				+ String.valueOf(BuildConfig.VERSION_NAME)
				+ "."
				+ String.valueOf(BuildConfig.VERSION_CODE);
		mTxtVersion.setText(version);
	}

	private void goToSite(String url) {
		CustomTabsIntent intent = new CustomTabsIntent.Builder()
				.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
				.build();
		intent.launchUrl(getActivity(), Uri.parse(url));
	}

	public static AboutFragment getInstance() {
		return new AboutFragment();
	}
}
