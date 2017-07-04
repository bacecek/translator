package com.bacecek.translate.ui.about;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bacecek.translate.BuildConfig;
import com.bacecek.translate.R;
import com.bacecek.translate.ui.base.BaseFragment;
import com.bacecek.translate.util.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by Denis Buzmakov on 19/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class AboutFragment extends BaseFragment {
	@BindView(R.id.txt_version)
	TextView mTxtVersion;

	private int mLongClickCount = 0;

	@OnLongClick(R.id.img_logo)
	boolean onLongClickLogo(View view) {
		mLongClickCount++;
		ViewPropertyAnimator animation = view.animate()
				.setDuration(500)
				.setInterpolator(new FastOutSlowInInterpolator());
		if(mLongClickCount % 2 == 0) {
			animation.rotationBy(360);
		} else {
			animation.rotationBy(-360);
		}
		animation.start();
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
