package com.bacecek.translate.data.db.entities;

import java.util.ArrayList;

/**
 * Created by Denis Buzmakov on 04/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryItem extends DictionaryWord{
	private ArrayList<DictionaryWord> synonyms;
	private ArrayList<DictionaryWord> means;
	private ArrayList<DictionaryExample> examples;

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
