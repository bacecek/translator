package com.bacecek.translate.ui.events;

import com.bacecek.translate.data.entities.Translation;

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
