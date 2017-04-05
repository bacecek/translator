package com.bacecek.translate.data.db.entities;

import java.util.ArrayList;

/**
 * Created by Denis Buzmakov on 04/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryTranslation {
	private String text;
	private String pos;
	private String gen;
	private ArrayList<DictionaryWord> synonyms;
	private ArrayList<DictionaryWord> means;
	private ArrayList<DictionaryExample> examples;

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

	public ArrayList<DictionaryWord> getSynonyms() {
		return synonyms;
	}

	public void setSynonyms(
			ArrayList<DictionaryWord> synonyms) {
		this.synonyms = synonyms;
	}

	public ArrayList<DictionaryWord> getMeans() {
		return means;
	}

	public void setMeans(ArrayList<DictionaryWord> means) {
		this.means = means;
	}

	public ArrayList<DictionaryExample> getExamples() {
		return examples;
	}

	public void setExamples(
			ArrayList<DictionaryExample> examples) {
		this.examples = examples;
	}
}
