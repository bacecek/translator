package com.bacecek.translate.data.db.entities;

import java.util.ArrayList;

/**
 * Created by Denis Buzmakov on 05/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryExample extends DictionaryWord{
	private ArrayList<String> examples;

	public ArrayList<String> getExamples() {
		return examples;
	}

	public void setExamples(ArrayList<String> examples) {
		this.examples = examples;
	}
}
