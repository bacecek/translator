package com.bacecek.translate.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entities.Setting;
import com.bacecek.translate.mvp.presenters.SettingsPresenter;
import com.bacecek.translate.mvp.views.SettingsView;
import com.bacecek.translate.ui.adapters.SettingsAdapter;
import java.util.ArrayList;

/**
 * Created by Denis Buzmakov on 19/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class SettingsFragment extends BaseFragment implements SettingsView{
	@InjectPresenter
	SettingsPresenter mPresenter;

	@BindView(R.id.list_settings)
	RecyclerView mRecyclerSettings;
	@BindView(R.id.btn_clear_history)
	AppCompatButton mBtnClearHistory;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_settings, container, false);
		ButterKnife.bind(this, parent);

		setTitle(parent, getString(R.string.action_settings));
		initUi();
		initClickListeners();

		return parent;
	}

	private void initUi() {
		mRecyclerSettings.setHasFixedSize(true);
		mRecyclerSettings.setLayoutManager(new LinearLayoutManager(getActivity()));
		DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
		divider.setDrawable(getResources().getDrawable(R.drawable.list_divider_favourite));
		mRecyclerSettings.addItemDecoration(divider);
	}

	private void initClickListeners() {
		mBtnClearHistory.setOnClickListener(v -> mPresenter.onClickClearHistory());
	}

	@Override
	public void setSettingsList(ArrayList<Setting> settingsList) {
		SettingsAdapter adapter = new SettingsAdapter(settingsList, (setting, value) -> mPresenter.onSwitchValue(setting, value));
		mRecyclerSettings.setAdapter(adapter);
	}

	@Override
	public void showToast(@StringRes int res) {
		Toast.makeText(getActivity(), res, Toast.LENGTH_SHORT).show();
	}

	public static SettingsFragment getInstance() {
		return new SettingsFragment();
	}
}
