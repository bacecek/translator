package com.bacecek.translate.data.entity;

/**
 * Created by Denis Buzmakov on 05/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryExample extends DictionaryItem {
	private String text;
	private String[] translations;

	public DictionaryExample(String text, String[] translations) {
		this.text = text;
		this.translations = translations;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String[] getTranslations() {
		return translations;
	}

	public void setTranslations(String[] translations) {
		this.translations = translations;
	}
}
