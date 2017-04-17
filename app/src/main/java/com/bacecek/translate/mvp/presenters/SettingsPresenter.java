package com.bacecek.translate.mvp.presenters;

import android.content.Context;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.R;
import com.bacecek.translate.data.db.PrefsManager;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.entities.Setting;
import com.bacecek.translate.mvp.views.SettingsView;
import com.bacecek.translate.ui.events.ChangeInputImeOptionsEvent;
import com.bacecek.translate.ui.events.ShowDictionaryEvent;
import com.bacecek.translate.utils.Consts.Settings;
import java.util.ArrayList;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@InjectViewState
public class SettingsPresenter extends MvpPresenter<SettingsView> {
	private ArrayList<Setting> mSettingsList = new ArrayList<Setting>(Settings.SETTINGS_COUNT);

	@Inject
	Context mContext;
	@Inject
	PrefsManager mPrefsManager;
	@Inject
	RealmController mRealmController;

	public SettingsPresenter() {
		App.getAppComponent().inject(this);
		initSettingsList();
	}

	@Override
	protected void onFirstViewAttach() {
		super.onFirstViewAttach();
		getViewState().setSettingsList(mSettingsList);
	}

	private void initSettingsList() {
		Setting setting = new Setting(Settings.SETTING_SIMULTANEOUS, mContext.getString(R.string.settings_simultaneous_translation), mPrefsManager.simultaneousTranslation());
		mSettingsList.add(Settings.SETTING_SIMULTANEOUS, setting);

		setting = new Setting(Settings.SETTING_DICTIONARY, mContext.getString(R.string.settings_show_dictionary), mPrefsManager.showDictionary());
		mSettingsList.add(Settings.SETTING_DICTIONARY, setting);

		setting = new Setting(Settings.SETTING_RETURN, mContext.getString(R.string.settings_return), mPrefsManager.returnForTranslate());
		mSettingsList.add(Settings.SETTING_RETURN, setting);
	}

	public void onSwitchValue(Setting setting, boolean value) {
		setting.setValue(value);
		switch (setting.getId()) {
			case Settings.SETTING_SIMULTANEOUS:
				mPrefsManager.setSimultaneousTranslation(value);
				break;
			case Settings.SETTING_DICTIONARY:
				mPrefsManager.setShowDictionary(value);
				EventBus.getDefault().post(new ShowDictionaryEvent());
				break;
			case Settings.SETTING_RETURN:
				mPrefsManager.setReturnForTranslate(value);
				EventBus.getDefault().post(new ChangeInputImeOptionsEvent());
				break;
		}
	}

	public void onClickClearHistory() {
		mRealmController.clearHistory();
		getViewState().showToast(R.string.history_was_cleared);
	}
}
