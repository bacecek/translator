package com.bacecek.translate.ui.events;

import com.bacecek.translate.data.entities.Translation;

/**
 * Created by Denis Buzmakov on 03/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class ClickFavouriteEvent {

	public final Translation translation;

	public ClickFavouriteEvent(Translation translation) {
		this.translation = translation;
	}
}
