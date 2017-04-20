package com.bacecek.translate.event;

/**
 * Created by Denis Buzmakov on 19/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class ChangeNetworkStateEvent {
	public final boolean isOnline;

	public ChangeNetworkStateEvent(boolean isOnline) {
		this.isOnline = isOnline;
	}
}
