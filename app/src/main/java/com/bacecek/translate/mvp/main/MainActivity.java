package com.bacecek.translate.mvp.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.translate.BuildConfig;
import com.bacecek.translate.R;
import com.bacecek.translate.data.network.util.NetworkStateReceiver;
import com.bacecek.translate.event.ClickFavouriteEvent;
import com.bacecek.translate.event.ClickMenuEvent;
import com.bacecek.translate.event.IntentTranslateEvent;
import com.bacecek.translate.event.TranslateEvent;
import com.bacecek.translate.mvp.about.AboutFragment;
import com.bacecek.translate.mvp.favourites.FavouriteFragment;
import com.bacecek.translate.mvp.settings.SettingsFragment;
import com.bacecek.translate.util.Consts.Extra;
import com.bacecek.translate.util.Utils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import ru.yandex.speechkit.SpeechKit;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {
	@BindView(R.id.view_navigation)
	NavigationView mNavigationView;
	@BindView(R.id.layout_drawer)
	DrawerLayout mDrawerLayout;

	private NetworkStateReceiver mNetworkStateReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		initUI();
		SpeechKit.getInstance().configure(getApplicationContext(), BuildConfig.YANDEX_SPEECHKIT_API_KEY);
		mNetworkStateReceiver = new NetworkStateReceiver();
		registerReceiver(mNetworkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		String incomingTranslation = getIntent().getStringExtra(Extra.EXTRA_INCOMING_TRANSLATION);
		if(incomingTranslation != null) {
			EventBus.getDefault().postSticky(new IntentTranslateEvent(incomingTranslation));
		}
	}

	private void initUI() {
		mNavigationView.setNavigationItemSelectedListener(this);
		mNavigationView.setCheckedItem(R.id.action_home);
		//скрытие клавиатуры при открытии NavigationDrawer
		DrawerListener drawerListener = new DrawerListener() {
			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				Utils.hideKeyboard(MainActivity.this);
			}

			@Override
			public void onDrawerOpened(View drawerView) {

			}

			@Override
			public void onDrawerClosed(View drawerView) {

			}

			@Override
			public void onDrawerStateChanged(int newState) {

			}
		};
		mDrawerLayout.addDrawerListener(drawerListener);
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		mDrawerLayout.closeDrawers();
		setTitle(item.getTitle());
		item.setChecked(true);
		navigate(item.getItemId());
		return true;
	}

	/**
	 * Добавление фрагмента на экран в зависимости от выбранного итема в drawer
	 * @param menuId - выбранный итем в drawer
	 */
	private void navigate(@MenuRes int menuId) {
		switch (menuId) {
			case R.id.action_home:
				removeFromBackStack();
				break;
			case R.id.action_favourites:
				addToBackStack(FavouriteFragment.getInstance());
				break;
			case R.id.action_settings:
				addToBackStack(SettingsFragment.getInstance());
				break;
			case R.id.action_about:
				addToBackStack(AboutFragment.getInstance());
				break;
		}
	}

	/**
	 * Удаление фрагмента из контейнера.
	 * @return - был ли вообще фрагмент в контейнере
	 */
	private boolean removeFromBackStack() {
		FragmentManager fm = getFragmentManager();
		if(fm.getBackStackEntryCount() > 0) {
			fm.popBackStack();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Добавление фрагмента в контейнер.
	 * @param fragment
	 */
	private void addToBackStack(Fragment fragment) {
		removeFromBackStack();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.container_main, fragment)
				.addToBackStack(null)
				.commit();
	}

	@Override
	public void onBackPressed() {
		if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			//Если мы не на основном экране - удаляем фрагмент из контейнера
			if(removeFromBackStack()) {
				MenuItem item = mNavigationView.getMenu().getItem(0);
				item.setChecked(true);
				setTitle(item.getTitle());
			}
			//А если на основном - то закрываем.
			else {
				super.onBackPressed();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onStop() {
		EventBus.getDefault().unregister(this);
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mNetworkStateReceiver);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onClickFavouriteEvent(ClickFavouriteEvent event) {
		onNavigationItemSelected(mNavigationView.getMenu().getItem(0));
		EventBus.getDefault().post(new TranslateEvent(event.translation));
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onClickMenuEvent(ClickMenuEvent event) {
		mDrawerLayout.openDrawer(GravityCompat.START);
	}
}
