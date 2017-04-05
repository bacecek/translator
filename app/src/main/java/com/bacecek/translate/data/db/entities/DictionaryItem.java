package com.bacecek.translate.data.db.entities;

import java.util.List;

/**
 * Created by Denis Buzmakov on 05/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryItem {
	private String text;
	private String pos;
	private String ts;
	private String gen;
	private String anm;
	private List<DictionaryTranslation> translations;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getGen() {
		return gen;
	}

	public void setGen(String gen) {
		this.gen = gen;
	}

	public String getAnm() {
		return anm;
	}

	public void setAnm(String anm) {
		this.anm = anm;
	}

	public List<DictionaryTranslation> getTranslations() {
		return translations;
	}

	public void setTranslations(
			List<DictionaryTranslation> translations) {
		this.translations = translations;
	}
}
