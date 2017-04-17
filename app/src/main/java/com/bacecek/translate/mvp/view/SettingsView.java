package com.bacecek.translate.mvp.view;

import android.support.annotation.StringRes;
import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.bacecek.translate.data.entity.Setting;
import java.util.ArrayList;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

public interface SettingsView extends MvpView {
	void setSettingsList(ArrayList<Setting> settingsList);
	@StateStrategyType(OneExecutionStateStrategy.class)
	void showToast(@StringRes int res);
}