package com.bacecek.translate.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import com.bacecek.translate.R;

/**
 * Created by Denis Buzmakov on 19/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class AboutFragment extends BaseFragment {

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_about, container, false);
		ButterKnife.bind(this, parent);
		setTitle(parent, getString(R.string.action_about));
		return parent;
	}

	public static AboutFragment getInstance() {
		return new AboutFragment();
	}
}
