package com.bacecek.translate.data.db;

import com.bacecek.translate.data.entity.Language;
import com.bacecek.translate.data.entity.Translation;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import javax.inject.Inject;

/**
 * Created by Denis Buzmakov on 21/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class RealmController {
	private static RealmController instance;
	private Realm mRealm;

	public static RealmController getInstance() {
		if(instance == null) {
			instance = new RealmController();
		}

		return instance;
	}

	private RealmController() {
		RealmConfiguration config = new RealmConfiguration.Builder()
				.name(Realm.DEFAULT_REALM_NAME)
				.deleteRealmIfMigrationNeeded()
				.build();

		mRealm = Realm.getInstance(config);
	}

	@Inject
	public RealmController(Realm realm) {
		mRealm = realm;
	}

	public RealmResults<Language> getLanguages() {
		return mRealm.where(Language.class)
				.findAllSorted("name");
	}

	public RealmResults<Language> getLanguagesAsync() {
		return mRealm.where(Language.class)
				.findAllSortedAsync("name");
	}

	public RealmResults<Language> getRecentlyUsedLanguages() {
		return mRealm.where(Language.class)
				.greaterThan("lastUsedTimeStamp", 0)
				.findAllSortedAsync("lastUsedTimeStamp", Sort.DESCENDING);
	}

	private RealmResults<Language> getRecentlyUsedLanguages(Realm realm) {
		return realm.where(Language.class)
				.greaterThan("lastUsedTimeStamp", 0)
				.findAllSorted("lastUsedTimeStamp", Sort.DESCENDING);
	}

	public void updateTimestampLanguage(String codeLang) {
		mRealm.executeTransactionAsync(realm -> {
			Language language = getLanguageByCode(realm, codeLang);
			RealmResults<Language> recentlyLangs = getRecentlyUsedLanguages(realm);
			if(recentlyLangs.size() == 5 && language.getLastUsedTimeStamp() == 0) {
				recentlyLangs.deleteFromRealm(4);
			}
			language.setLastUsedTimeStamp(System.currentTimeMillis());
		});
	}

	public void insertLanguages(RealmList<Language> list) {
		mRealm.beginTransaction();
		mRealm.copyToRealmOrUpdate(list);
		mRealm.commitTransaction();
	}

	public Language getLanguageByCode(String code) {
		return mRealm.where(Language.class)
				.equalTo("code", code)
				.findFirst();
	}

	private Language getLanguageByCode(Realm realm, String code) {
		return realm.where(Language.class)
				.equalTo("code", code)
				.findFirst();
	}

	public Language getLanguageByName(String name) {
		return mRealm.where(Language.class)
				.equalTo("name", name)
				.findFirst();
	}

	public void changeFavourite(String originalText, String originalLang, String targetLang) {
		changeFavourite(getTranslation(originalText, originalLang, targetLang));
	}

	public void changeFavourite(Translation translation) {
		if(translation == null) return;
		if(isRemovingNeeded(translation)) {
			removeTranslation(translation);
		} else {
			mRealm.beginTransaction();
			translation.setFavourite(!translation.isFavourite());
			translation.setFavouriteTimestamp(System.currentTimeMillis());
			mRealm.commitTransaction();
		}
	}

	public Translation getTranslation(String text, String originalLang, String targetLang) {
		return mRealm.where(Translation.class)
				.equalTo("originalText", text)
				.equalTo("originalLang", originalLang)
				.equalTo("targetLang", targetLang)
				.findFirst();
	}

	private Translation getTranslation(Realm realm, String text, String originalLang, String targetLang) {
		return realm.where(Translation.class)
				.equalTo("originalText", text)
				.equalTo("originalLang", originalLang)
				.equalTo("targetLang", targetLang)
				.findFirst();
	}

	public void insertTranslationAsync(String originalText, String translatedText, String originalLang, String targetLang) {
		mRealm.executeTransactionAsync(realm -> {
			Translation translation = getTranslation(realm, originalText, originalLang, targetLang);
			if(translation == null) {
				translation = new Translation();
				translation.setId(getNextId(realm));
				translation.setOriginalText(originalText);
			}
			translation.setTranslatedText(translatedText);
			translation.setOriginalLang(originalLang);
			translation.setTargetLang(targetLang);
			translation.setHistoryTimestamp(System.currentTimeMillis());
			translation.setShowInHistory(true);
			realm.copyToRealmOrUpdate(translation);
		});
	}

	public void insertTranslation(String originalText, String translatedText, String originalLang, String targetLang) {
		Translation translation = getTranslation(originalText, originalLang, targetLang);
		mRealm.beginTransaction();
		if(translation == null) {
			translation = new Translation();
			translation.setId(getNextId(mRealm));
			translation.setOriginalText(originalText);
		}
		translation.setTranslatedText(translatedText);
		translation.setOriginalLang(originalLang);
		translation.setTargetLang(targetLang);
		translation.setHistoryTimestamp(System.currentTimeMillis());
		translation.setShowInHistory(true);
		mRealm.copyToRealmOrUpdate(translation);
		mRealm.commitTransaction();
	}

	public void removeTranslationFromHistory(Translation translation) {
		if(isRemovingNeeded(translation)) {
			removeTranslation(translation);
		} else {
			mRealm.beginTransaction();
			translation.setShowInHistory(!translation.isShowInHistory());
			mRealm.commitTransaction();
		}
	}

	public void clearHistory() {
		mRealm.executeTransactionAsync(realm -> {
			realm.where(Translation.class)
					.equalTo("showInHistory", true)
					.equalTo("isFavourite", false)
					.findAll()
					.deleteAllFromRealm();
			RealmResults<Translation> favourites = realm.where(Translation.class)
					.equalTo("isFavourite", true)
					.findAll();
			for (Translation item : favourites) {
				item.setShowInHistory(false);
			}
		});
	}

	private boolean isRemovingNeeded(Translation translation) {
		return !translation.isShowInHistory() && translation.isFavourite();
	}

	public void removeTranslation(Translation translation) {
		mRealm.beginTransaction();
		translation.deleteFromRealm();
		mRealm.commitTransaction();
	}

	public RealmResults<Translation> getFavourites(String search) {
		return mRealm.where(Translation.class)
				.beginGroup()
					.contains("originalText", search)
					.or()
					.contains("translatedText", search)
				.endGroup()
				.equalTo("isFavourite", true)
				.findAllSorted("favouriteTimestamp", Sort.DESCENDING);
	}

	public RealmResults<Translation> getFavourites() {
		return mRealm.where(Translation.class)
				.equalTo("isFavourite", true)
				.findAllSorted("favouriteTimestamp", Sort.DESCENDING);
	}

	public RealmResults<Translation> getHistory() {
		return mRealm.where(Translation.class)
				.equalTo("showInHistory", true)
				.findAllSortedAsync("historyTimestamp", Sort.DESCENDING);
	}

	public boolean isTranslationFavourite(String text, String originalLang, String targetLang) {
		Translation translation = getTranslation(text, originalLang, targetLang);
		if(translation == null) {
			return false;
		} else {
			return translation.isFavourite();
		}
	}

	private int getNextId(Realm realm) {
		Number nextId = realm.where(Translation.class).max("id");
		if(nextId == null) {
			return 0;
		} else {
			return realm.where(Translation.class).max("id").intValue() + 1;
		}
	}
}
