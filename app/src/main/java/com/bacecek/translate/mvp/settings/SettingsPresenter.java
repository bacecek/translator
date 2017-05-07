package com.bacecek.translate.mvp.settings;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.R;
import com.bacecek.translate.data.db.PrefsManager;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.event.ChangeInputImeOptionsEvent;
import com.bacecek.translate.event.ShowDictionaryEvent;
import com.bacecek.translate.mvp.settings.SettingsInteractor;
import com.bacecek.translate.mvp.settings.SettingsView;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@InjectViewState
public class SettingsPresenter extends MvpPresenter<SettingsView> {
	@Inject
	SettingsInteractor mInteractor;
	@Inject
	PrefsManager mPrefsManager;
	@Inject
	RealmController mRealmController;

	public SettingsPresenter() {
		App.getAppComponent().inject(this);
	}

	@Override
	protected void onFirstViewAttach() {
		super.onFirstViewAttach();
		getViewState().setCheckedSwitchSimultaneous(mInteractor.getSettingSimultaneousTranslation());
		getViewState().setCheckedSwitchDictionary(mInteractor.getSettingShowDictionary());
		getViewState().setCheckedSwitchReturn(mInteractor.getSettingReturnForTranslate());
	}

	public void onChangeCheckedSwitchSimultaneous(boolean checked) {
		mInteractor.setSettingSimultaneousTranslation(checked);
	}

	public void onChangeCheckedSwitchDictionary(boolean checked) {
		mInteractor.setSettingShowDictionary(checked);
		EventBus.getDefault().post(new ShowDictionaryEvent());
	}

	public void onChangeCheckedSwitchReturn(boolean checked) {
		mInteractor.setSettingReturnForTranslate(checked);
		EventBus.getDefault().post(new ChangeInputImeOptionsEvent());
	}

	public void onClickClearHistory() {
		mInteractor.clearHistory();
		getViewState().showToast(R.string.history_was_cleared);
	}
}
