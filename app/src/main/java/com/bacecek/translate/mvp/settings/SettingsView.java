package com.bacecek.translate.mvp.settings;

import android.support.annotation.StringRes;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface SettingsView extends MvpView {
	void setCheckedSwitchSimultaneous(boolean checked);
	void setCheckedSwitchDictionary(boolean checked);
	void setCheckedSwitchReturn(boolean checked);
	@StateStrategyType(OneExecutionStateStrategy.class)
	void showToast(@StringRes int res);
}