package com.bacecek.translate.data.entities;

/**
 * Created by Denis Buzmakov on 05/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryExample extends DictionaryItem {
	private String[] translations;

	public DictionaryExample(String text, String[] translations) {
		super(text);
		this.translations = translations;
	}

	public String[] getTranslations() {
		return translations;
	}

	public void setTranslations(String[] translations) {
		this.translations = translations;
	}
}
