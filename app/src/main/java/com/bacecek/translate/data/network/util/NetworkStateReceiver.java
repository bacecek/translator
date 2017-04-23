package com.bacecek.translate.data.network.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.bacecek.translate.event.ChangeNetworkStateEvent;
import com.bacecek.translate.util.Utils;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Denis Buzmakov on 19/04/2017.
 * <buzmakov.da@gmail.com>
 */

/**
 * Простой BroadcastReceiver, слушающий изменение состояния сети
 */
public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		EventBus.getDefault().post(new ChangeNetworkStateEvent(Utils.isOnline(context)));
	}
}
