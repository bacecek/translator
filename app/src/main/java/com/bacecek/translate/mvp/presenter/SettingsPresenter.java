package com.bacecek.translate.mvp.presenter;

import android.content.Context;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.bacecek.translate.App;
import com.bacecek.translate.R;
import com.bacecek.translate.data.db.PrefsManager;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.event.ChangeInputImeOptionsEvent;
import com.bacecek.translate.event.ShowDictionaryEvent;
import com.bacecek.translate.event.SimultaneousTranslateEvent;
import com.bacecek.translate.mvp.view.SettingsView;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@InjectViewState
public class SettingsPresenter extends MvpPresenter<SettingsView> {

	@Inject
	Context mContext;
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
		getViewState().setCheckedSwitchSimultaneous(mPrefsManager.simultaneousTranslation());
		getViewState().setCheckedSwitchDictionary(mPrefsManager.showDictionary());
		getViewState().setCheckedSwitchReturn(mPrefsManager.returnForTranslate());
	}

	public void onChangeCheckedSwitchSimultaneous(boolean checked) {
		mPrefsManager.setSimultaneousTranslation(checked);
		EventBus.getDefault().post(new SimultaneousTranslateEvent());
	}

	public void onChangeCheckedSwitchDictionary(boolean checked) {
		mPrefsManager.setShowDictionary(checked);
		EventBus.getDefault().post(new ShowDictionaryEvent());
	}

	public void onChangeCheckedSwitchReturn(boolean checked) {
		mPrefsManager.setReturnForTranslate(checked);
		EventBus.getDefault().post(new ChangeInputImeOptionsEvent());
	}

	public void onClickClearHistory() {
		mRealmController.clearHistory();
		getViewState().showToast(R.string.history_was_cleared);
	}
}
