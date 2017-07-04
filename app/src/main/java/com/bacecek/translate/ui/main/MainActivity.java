package com.bacecek.translate.ui.main;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.bacecek.translate.BuildConfig;
import com.bacecek.translate.R;
import com.bacecek.translate.data.network.util.NetworkStateReceiver;
import com.bacecek.translate.util.Consts;
import com.bacecek.translate.util.adapter.ViewPagerAdapter;
import com.roughike.bottombar.BottomBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.yandex.speechkit.SpeechKit;

/**
 * Created by Denis Buzmakov on 17.06.2017.
 * <buzmakov.da@gmail.com>
 */

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.view_bottom_bar)
    BottomBar mBottomBar;
    @BindView(R.id.view_pager)
	ViewPager mViewPager;

	private NetworkStateReceiver mNetworkStateReceiver;

	static {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		initUI();

		SpeechKit.getInstance().configure(getApplicationContext(), BuildConfig.YANDEX_SPEECHKIT_API_KEY);
		mNetworkStateReceiver = new NetworkStateReceiver();
		registerReceiver(mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //TODO:ДОДЕЛАТЬ
        String incomingTranslation = getIntent().getStringExtra(Consts.Extra.EXTRA_INCOMING_TRANSLATION);
    }

    private void initUI(){
		ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
		mViewPager.setAdapter(adapter);
		mViewPager.setOffscreenPageLimit(3);
		mBottomBar.setOnTabSelectListener(tabId -> {
			switch (tabId) {
				case R.id.tab_translate:
					mViewPager.setCurrentItem(Consts.Pager.PAGE_TRANSLATE, false);
					break;
				case R.id.tab_history:
					mViewPager.setCurrentItem(Consts.Pager.PAGE_HISTORY, false);
					break;
				case R.id.tab_favorites:
					mViewPager.setCurrentItem(Consts.Pager.PAGE_FAVOURITES, false);
					break;
				case R.id.tab_settings:
					mViewPager.setCurrentItem(Consts.Pager.PAGE_SETTINGS, false);
					break;
			}
		});
    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNetworkStateReceiver);
	}
}
