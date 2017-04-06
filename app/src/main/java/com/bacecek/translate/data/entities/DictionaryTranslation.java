package com.bacecek.translate.data.entities;

/**
 * Created by Denis Buzmakov on 04/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryTranslation {
	private String text;
	private String pos;
	private String gen;
	private DictionaryWord[] synonyms;
	private DictionaryWord[] means;
	private DictionaryExample[] examples;

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

	public String getGen() {
		return gen;
	}

	public void setGen(String gen) {
		this.gen = gen;
	}

	public DictionaryWord[] getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(
			DictionaryWord[] synonyms) {
		this.synonyms = synonyms;
	}

	public DictionaryWord[] getMeans() {
		return means;
	}

	public void setMeans(DictionaryWord[] means) {
		this.means = means;
	}

	public DictionaryExample[] getExamples() {
		return examples;
	}

	public void setExamples(
			DictionaryExample[] examples) {
		this.examples = examples;
	}
}
