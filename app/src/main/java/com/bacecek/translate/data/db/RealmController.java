package com.bacecek.translate.data.db;

import com.bacecek.translate.data.entities.Language;
import com.bacecek.translate.data.entities.Translation;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;

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
		mRealm = Realm.getDefaultInstance();

		RealmConfiguration config = new RealmConfiguration.Builder()
				.name(Realm.DEFAULT_REALM_NAME)
				.deleteRealmIfMigrationNeeded()
				.build();
		Realm.setDefaultConfiguration(config);
	}

	public RealmResults<Language> getLanguages() {
		return mRealm.where(Language.class).findAll();
	}

	public void insertLanguages(RealmList<Language> list) {
		mRealm.beginTransaction();
		mRealm.copyToRealmOrUpdate(list);
		mRealm.commitTransaction();
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

	public void destroy() {
		mRealm.close();
		mRealm = null;
		instance = null;
	}

	public Translation getTranslation(String text, String originalLang, String targetLang) {
		return mRealm.where(Translation.class)
					.equalTo("originalText", text)
					.equalTo("originalLang", originalLang)
					.equalTo("targetLang", targetLang)
				.findFirst();
	}

	public void insertTranslation(String originalText, String translatedText, String originalLang, String targetLang) {
		Translation translation = getTranslation(originalText, originalLang, targetLang);
		mRealm.beginTransaction();
		if(translation == null) {
			translation = new Translation();
			translation.setId(getNextId());
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

	private int getNextId() {
		Number nextId = mRealm.where(Translation.class).max("id");
		if(nextId == null) {
			return 0;
		} else {
			return mRealm.where(Translation.class).max("id").intValue() + 1;
		}
	}
}
