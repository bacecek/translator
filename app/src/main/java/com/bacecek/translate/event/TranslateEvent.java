package com.bacecek.translate.event;

import com.bacecek.translate.data.entity.Translation;

/**
 * Created by Denis Buzmakov on 03/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class TranslateEvent {

	public final Translation translation;

	public TranslateEvent(Translation translation) {
		this.translation = translation;
	}
}
