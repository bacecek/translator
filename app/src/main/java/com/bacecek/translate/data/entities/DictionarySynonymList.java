package com.bacecek.translate.data.entities;

import java.util.ArrayList;

/**
 * Created by Denis Buzmakov on 04/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionarySynonymList extends DictionaryItem {
	private ArrayList<DictionarySynonym> mSynonyms;

	public DictionarySynonymList(
			ArrayList<DictionarySynonym> synonyms) {
		mSynonyms = synonyms;
	}

	public ArrayList<DictionarySynonym> getSynonyms() {
		return mSynonyms;
	}

	public static class DictionarySynonym {
		private String text;
		private String pos;
		private String gen;

		public DictionarySynonym(String text, String pos, String gen) {
			this.text = text;
			this.pos = pos;
			this.gen = gen;
		}

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
	}
}
