package com.bacecek.translate.mvp.translate;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.bacecek.translate.data.entity.DictionaryItem;
import com.bacecek.translate.data.entity.Translation;
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
	void setProgressVisibility(boolean visible);
	void setErrorVisibility(boolean visible);
	void setHistoryVisibility(boolean visible);
	void setTranslationVisibility(boolean visible);
	void setDictionaryVisibility(boolean visible);
	void setButtonClearVisibility(boolean visible);
	void setButtonVocalizeVisibility(boolean visible);
	void setErrorData(Throwable error);
	void setHistoryData(RealmResults<Translation> history);
	void setTranslationData(Translation translation);
	void setDictionaryData(List<DictionaryItem> items);
	void setOriginalLangName(String name);
	void setTargetLangName(String name);
	@StateStrategyType(OneExecutionStateStrategy.class)
	void setOriginalText(String text);
	@StateStrategyType(OneExecutionStateStrategy.class)
	void startDictation(String originalLang);
	void setTranslationFavourite(boolean isFavourite);
	void setOriginalVocalizeButtonState(int state);
	void setTranslatedVocalizeButtonState(int state);
	@StateStrategyType(OneExecutionStateStrategy.class)
	void showToast(String text);
	void setOriginalVocalizeButtonEnabled(boolean enabled);
	void setTranslatedVocalizeButtonEnabled(boolean enabled);
	void setMicButtonEnabled(boolean enabled);
	void setInputImeOptions(int imeOptions);
}
