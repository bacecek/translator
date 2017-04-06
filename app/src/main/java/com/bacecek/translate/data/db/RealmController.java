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

	public void changeFavourite(Translation translation) {
		mRealm.beginTransaction();
		translation.setFavourite(!translation.isFavourite());
		mRealm.commitTransaction();
	}

	public void destroy() {
		mRealm.close();
		mRealm = null;
		instance = null;
	}

	public void insertTranslation(Translation translation, boolean changeFavourite) {
		if(!changeFavourite) {
			Translation foundTranslation = mRealm.where(Translation.class).equalTo("originalText", translation.getOriginalText()).findFirst();
			if(foundTranslation != null) {
				translation.setFavourite(foundTranslation.isFavourite());
			}
		}
		mRealm.beginTransaction();
		mRealm.copyToRealmOrUpdate(translation);
		mRealm.commitTransaction();
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
				.findAllSorted("timestamp", Sort.DESCENDING);
	}

	public RealmResults<Translation> getFavourites() {
		return mRealm.where(Translation.class)
				.equalTo("isFavourite", true)
				.findAllSorted("timestamp", Sort.DESCENDING);
	}

	public RealmResults<Translation> getHistory() {
		return mRealm.where(Translation.class)
				.findAllSortedAsync("timestamp", Sort.DESCENDING);
	}

	public boolean isTranslationFavourite(String text) {
		Translation translation = mRealm.where(Translation.class)
				.equalTo("originalText", text)
				.findFirst();
		if(translation == null) {
			return false;
		} else {
			return translation.isFavourite();
		}
	}
}
