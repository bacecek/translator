package com.bacecek.translate.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bacecek.translate.R;
import com.bacecek.translate.ui.events.ClickMenuEvent;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Denis Buzmakov on 19/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class SettingsFragment extends BaseFragment {

	@OnClick(R.id.btn_menu)
	void onClickMenu() {
		EventBus.getDefault().post(new ClickMenuEvent());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_settings, container, false);
		ButterKnife.bind(this, parent);
		setTitle(parent, getString(R.string.action_settings));
		return parent;
	}

	public static SettingsFragment getInstance() {
		return new SettingsFragment();
	}
}
