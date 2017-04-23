package com.bacecek.translate.data.entity;

import java.util.ArrayList;

/**
 * Created by Denis Buzmakov on 06/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryMeanList extends DictionaryItem {
	private ArrayList<DictionaryMean> mMeans;

	public DictionaryMeanList(ArrayList<DictionaryMean> means) {
		mMeans = means;
	}

	public ArrayList<DictionaryMean> getMeans() {
		return mMeans;
	}

	public static class DictionaryMean {
		private String text;

		public DictionaryMean(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	}
}
