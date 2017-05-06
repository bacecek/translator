package com.bacecek.translate.data.network.util;

import com.bacecek.translate.data.entity.DictionaryItem;
import com.bacecek.translate.data.entity.Translation;
import java.util.List;

/**
 * Created by Denis Buzmakov on 06/05/2017.
 * <buzmakov.da@gmail.com>
 */

public class TranslateResult {
	private Translation translation;
	private List<DictionaryItem> items;

	public TranslateResult(Translation translation,
			List<DictionaryItem> items) {
		this.translation = translation;
		this.items = items;
	}

	public Translation getTranslation() {
		return translation;
	}

	public List<DictionaryItem> getItems() {
		return items;
	}
}
