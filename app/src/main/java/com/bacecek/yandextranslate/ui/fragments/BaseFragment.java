package com.bacecek.yandextranslate.ui.fragments;

import android.app.Fragment;
import android.view.View;
import android.widget.TextView;
import com.bacecek.yandextranslate.R;

/**
 * Created by Denis Buzmakov on 03/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class BaseFragment extends Fragment {

	public void setTitle(View parent, String text) {
		TextView txtToolbatTitle = (TextView)parent.findViewById(R.id.txt_toolbar_title);
		if(txtToolbatTitle != null) {
			txtToolbatTitle.setText(text);
		}
	}

}
