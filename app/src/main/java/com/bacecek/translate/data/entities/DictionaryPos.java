package com.bacecek.translate.data.entities;

/**
 * Created by Denis Buzmakov on 04/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryPos extends DictionaryItem{
	private String text;

	public DictionaryPos(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
}
