package com.bacecek.translate.data.entities;

/**
 * Created by Denis Buzmakov on 04/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionarySynonym extends DictionaryItem {
	private String pos;
	private String gen;

	public DictionarySynonym(String text, String pos, String gen) {
		super(text);
		this.pos = pos;
		this.gen = gen;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getGen() {
		return gen;
	}

	public void setGen(String gen) {
		this.gen = gen;
	}
}
