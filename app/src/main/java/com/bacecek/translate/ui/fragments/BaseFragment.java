package com.bacecek.translate.ui.fragments;

import android.app.Fragment;
import android.view.View;
import android.widget.TextView;
import com.bacecek.translate.R;

/**
 * Created by Denis Buzmakov on 03/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class BaseFragment extends Fragment {

	public void setTitle(View parent, String text) {
		TextView txtToolbarTitle = (TextView)parent.findViewById(R.id.txt_toolbar_title);
		if(txtToolbarTitle != null) {
			txtToolbarTitle.setText(text);
		}
	}

}
