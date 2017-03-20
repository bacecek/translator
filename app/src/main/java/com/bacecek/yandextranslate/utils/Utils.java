package com.bacecek.yandextranslate.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Denis Buzmakov on 20/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class Utils {
	public static void copyToClipboard(Context context, String text) {
		ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
		ClipData data = ClipData.newPlainText("text", text);
		manager.setPrimaryClip(data);
	}

	public static void hideKeyboard(Activity activity) {
		InputMethodManager manager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		View view = activity.getCurrentFocus();
		if(view == null) {
			view = new View(activity);
		}
		manager.hideSoftInputFromInputMethod(view.getWindowToken(), 0);
	}
}
