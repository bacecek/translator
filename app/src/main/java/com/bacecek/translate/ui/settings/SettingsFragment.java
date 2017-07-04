package com.bacecek.translate.ui.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bacecek.translate.R;
import com.bacecek.translate.mvp.settings.SettingsPresenter;
import com.bacecek.translate.mvp.settings.SettingsView;
import com.bacecek.translate.ui.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Denis Buzmakov on 19/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class SettingsFragment extends BaseFragment implements SettingsView {
	@InjectPresenter
	SettingsPresenter mPresenter;

	@BindView(R.id.btn_clear_history)
	AppCompatButton mBtnClearHistory;
	@BindView(R.id.switch_sim)
	SwitchCompat mSwitchSimultaneous;
	@BindView(R.id.switch_dictionary)
	SwitchCompat mSwitchDictionary;
	@BindView(R.id.switch_return)
	SwitchCompat mSwitchReturn;
	@BindView(R.id.view_sim)
	View mItemSimultaneous;
	@BindView(R.id.view_dictionary)
	View mItemDictionary;
	@BindView(R.id.view_return)
	View mItemReturn;

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
		mSwitchSimultaneous.setOnCheckedChangeListener((button, value) -> mPresenter.onChangeCheckedSwitchSimultaneous(value));
		mSwitchDictionary.setOnCheckedChangeListener((button, value) -> mPresenter.onChangeCheckedSwitchDictionary(value));
		mSwitchReturn.setOnCheckedChangeListener((button, value) -> mPresenter.onChangeCheckedSwitchReturn(value));
	}

	private void initClickListeners() {
		mBtnClearHistory.setOnClickListener(v -> {
			AlertDialog.Builder dialog = new Builder(getActivity());
			dialog.setTitle(R.string.history_clear_dialog_title);
			dialog.setMessage(R.string.history_clear_dialog_subtitle);
			dialog.setNegativeButton(R.string.cancel, (listener, i) -> listener.dismiss());
			dialog.setPositiveButton(R.string.ok, (listener, i) -> mPresenter.onClickClearHistory());
			dialog.show();
		});

		mItemSimultaneous.setOnClickListener(v -> mSwitchSimultaneous.setChecked(!mSwitchSimultaneous.isChecked()));
		mItemDictionary.setOnClickListener(v -> mSwitchDictionary.setChecked(!mSwitchDictionary.isChecked()));
		mItemReturn.setOnClickListener(v -> mSwitchReturn.setChecked(!mSwitchReturn.isChecked()));
	}

	@Override
	public void setCheckedSwitchSimultaneous(boolean checked) {
		mSwitchSimultaneous.setChecked(checked);
	}

	@Override
	public void setCheckedSwitchDictionary(boolean checked) {
		mSwitchDictionary.setChecked(checked);
	}

	@Override
	public void setCheckedSwitchReturn(boolean checked) {
		mSwitchReturn.setChecked(checked);
	}

	//Стоило бы, наверно, перенести его в какой-нибудь BaseView и BaseFragment, но вроде нигде больше не надо, то и ладно. А так да, надо
	@Override
	public void showToast(@StringRes int res) {
		Toast.makeText(getActivity(), res, Toast.LENGTH_SHORT).show();
	}

	public static SettingsFragment newInstance() {
		return new SettingsFragment();
	}
}
