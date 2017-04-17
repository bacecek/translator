package com.bacecek.translate.data.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Denis Buzmakov on 17/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class Translation extends RealmObject{
	@PrimaryKey
	private int id;
	private String originalText;
	private String translatedText;
	private String originalLang;
	private String targetLang;
	private boolean isFavourite;
	private boolean showInHistory;
	private long historyTimestamp;
	private long favouriteTimestamp;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOriginalText() {
		return originalText;
	}

	public void setOriginalText(String originalText) {
		this.originalText = originalText;
	}

	public String getTranslatedText() {
		return translatedText;
	}

	public void setTranslatedText(String translatedText) {
		this.translatedText = translatedText;
	}

	public String getOriginalLang() {
		return originalLang;
	}

	public void setOriginalLang(String originalLang) {
		this.originalLang = originalLang;
	}

	public String getTargetLang() {
		return targetLang;
	}

	public void setTargetLang(String targetLang) {
		this.targetLang = targetLang;
	}

	public boolean isFavourite() {
		return isFavourite;
	}

	public void setFavourite(boolean favourite) {
		isFavourite = favourite;
	}

	public boolean isShowInHistory() {
		return showInHistory;
	}

	public void setShowInHistory(boolean showInHistory) {
		this.showInHistory = showInHistory;
	}

	public long getHistoryTimestamp() {
		return historyTimestamp;
	}

	public void setHistoryTimestamp(long historyTimestamp) {
		this.historyTimestamp = historyTimestamp;
	}

	public long getFavouriteTimestamp() {
		return favouriteTimestamp;
	}

	public void setFavouriteTimestamp(long favouriteTimestamp) {
		this.favouriteTimestamp = favouriteTimestamp;
	}
}
