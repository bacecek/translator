package com.bacecek.translate.ui.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.bacecek.translate.ui.events.ClickFavouriteEvent;
import com.bacecek.translate.ui.events.ClickMenuEvent;
import com.bacecek.translate.ui.events.TranslateEvent;
import com.bacecek.translate.ui.fragments.AboutFragment;
import com.bacecek.translate.ui.fragments.FavouriteFragment;
import com.bacecek.translate.ui.fragments.SettingsFragment;
import com.bacecek.translate.utils.Utils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import ru.yandex.speechkit.SpeechKit;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {
	@BindView(R.id.view_navigation)
	NavigationView mNavigationView;
	@BindView(R.id.layout_drawer)
	DrawerLayout mDrawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		initUI();
		SpeechKit.getInstance().configure(getApplicationContext(), BuildConfig.YANDEX_SPEECHKIT_API_KEY);
	}

	private void initUI() {
		mNavigationView.setNavigationItemSelectedListener(this);
		mNavigationView.setCheckedItem(R.id.action_home);
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
		setTitle(item.getTitle());
		item.setChecked(true);
		navigate(item.getItemId());
		Timber.d(String.valueOf(item.getTitle()));
		mDrawerLayout.closeDrawers();
		return true;
	}

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

	private boolean removeFromBackStack() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		Fragment fragment = fm.findFragmentById(R.id.container_main);
		if (fragment != null) {
			transaction.remove(fragment);
			transaction.commit();
			return true;
		}
		return false;
	}

	private void addToBackStack(Fragment fragment) {
		removeFromBackStack();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.add(R.id.container_main, fragment);
		transaction.commit();
	}

	@Override
	public void onBackPressed() {
		if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			if(removeFromBackStack()) {
				MenuItem item = mNavigationView.getMenu().getItem(0);
				item.setChecked(true);
				setTitle(item.getTitle());
			}
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
