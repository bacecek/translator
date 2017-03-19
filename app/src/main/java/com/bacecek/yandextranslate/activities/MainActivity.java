package com.bacecek.yandextranslate.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.yandextranslate.R;
import com.bacecek.yandextranslate.fragments.AboutFragment;
import com.bacecek.yandextranslate.fragments.FavouritesFragment;
import com.bacecek.yandextranslate.fragments.SettingsFragment;
import com.bacecek.yandextranslate.fragments.TranslateFragment;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {
	@BindView(R.id.view_navigation)
	NavigationView mNavigationView;
	@BindView(R.id.layout_drawer)
	DrawerLayout mDrawerLayout;
	@BindView(R.id.toolbar)
	Toolbar mToolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		initUI();
	}

	private void initUI() {
		setSupportActionBar(mToolbar);

		mNavigationView.setNavigationItemSelectedListener(this);
		onNavigationItemSelected(mNavigationView.getMenu().getItem(0));
		ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open_drawer, R.string.close_drawer);
		mDrawerLayout.addDrawerListener(drawerToggle);
		drawerToggle.syncState();
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		setTitle(item.getTitle());
		item.setChecked(true);
		navigate(item.getItemId());
		mDrawerLayout.closeDrawers();
		return true;
	}

	private void navigate(@MenuRes int menuId) {
		Fragment fragment = null;
		switch (menuId) {
			case R.id.action_home:
				fragment = TranslateFragment.getInstance();
				break;
			case R.id.action_favourites:
				fragment = FavouritesFragment.getInstance();
				break;
			case R.id.action_settings:
				fragment = SettingsFragment.getInstance();
				break;
			case R.id.action_about:
				fragment = AboutFragment.getInstance();
				break;
		}

		getFragmentManager().beginTransaction()
				.replace(R.id.container, fragment)
				.commit();
	}
}
