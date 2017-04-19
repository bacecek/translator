package com.bacecek.translate.mvp.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

public interface SplashScreenView extends MvpView {
	void setErrorVisibility(boolean visible);
	void setLoadingVisibility(boolean visible);
	@StateStrategyType(OneExecutionStateStrategy.class)
	void goToMainScreen();
}
