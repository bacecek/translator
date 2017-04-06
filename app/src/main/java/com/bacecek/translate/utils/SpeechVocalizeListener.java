package com.bacecek.translate.utils;

import android.widget.Toast;
import com.bacecek.translate.ui.views.ListenButton;
import ru.yandex.speechkit.Error;
import ru.yandex.speechkit.Synthesis;
import ru.yandex.speechkit.Vocalizer;
import ru.yandex.speechkit.VocalizerListener;

/**
 * Created by Denis Buzmakov on 06/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class SpeechVocalizeListener implements VocalizerListener {
	private ListenButton mButton;

	public void setButton(ListenButton button) {
		if(mButton != null) {
			mButton.setState(ListenButton.STATE_PLAY);
		}
		mButton = button;

	}

	@Override
	public void onSynthesisBegin(Vocalizer vocalizer) {
		if(mButton != null) {
			mButton.setState(ListenButton.STATE_INIT);
		}
	}

	@Override
	public void onSynthesisDone(Vocalizer vocalizer, Synthesis synthesis) {}

	@Override
	public void onPlayingBegin(Vocalizer vocalizer) {
		if(mButton != null) {
			mButton.setState(ListenButton.STATE_STOP);
		}
	}

	@Override
	public void onPlayingDone(Vocalizer vocalizer) {
		if(mButton != null) {
			mButton.setState(ListenButton.STATE_PLAY);
		}
	}

	@Override
	public void onVocalizerError(Vocalizer vocalizer, Error error) {
		if(mButton != null) {
			Toast.makeText(mButton.getContext(), error.getString(), Toast.LENGTH_LONG).show();
		}
	}
}
