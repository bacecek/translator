package com.bacecek.translate.ui.events;

/**
 * Created by Denis Buzmakov on 03/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class ClickFavouriteEvent {

	public final String text;
	public final String originalLang;
	public final String targetLang;

	public ClickFavouriteEvent(String text, String originalLang, String targetLang) {
		this.text = text;
		this.originalLang = originalLang;
		this.targetLang = targetLang;
	}
}
