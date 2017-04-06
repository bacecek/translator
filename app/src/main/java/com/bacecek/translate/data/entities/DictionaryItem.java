package com.bacecek.translate.data.entities;

/**
 * Created by Denis Buzmakov on 05/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryItem {
	private String text;

	public DictionaryItem(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
