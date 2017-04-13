package com.bacecek.translate.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.bacecek.translate.data.entities.DictionaryItem;
import com.bacecek.translate.data.entities.Translation;
import io.realm.RealmResults;
import java.util.List;

/**
 * Created by Denis Buzmakov on 11/04/2017.
 * <buzmakov.da@gmail.com>
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface TranslateView extends MvpView {
	@StateStrategyType(OneExecutionStateStrategy.class)
	void goToChooseOriginalLanguage(String currentLang);
	@StateStrategyType(OneExecutionStateStrategy.class)
	void goToChooseTargetLanguage(String currentLang);
	void setHistoryData(RealmResults<Translation> history);
	void showProgress();
	void hideProgress();
	void showError();
	void hideError();
	void showHistory();
	void hideHistory();
	void showTranslation(Translation translation);
	void hideTranslation();
	void showDictionary(List<DictionaryItem> items);
	void hideDictionary();
	void showButtonClear();
	void hideButtonClear();
	void showButtonVocalize();
	void hideButtonVocalize();
	void setOriginalLangName(String name);
	void setTargetLangName(String name);
	@StateStrategyType(OneExecutionStateStrategy.class)
	void setOriginalText(String text);
}
