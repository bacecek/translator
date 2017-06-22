package com.bacecek.translate.mvp.base;

import android.view.View;
import android.widget.TextView;
import com.arellomobile.mvp.MvpFragment;
import com.bacecek.translate.R;

/**
 * Created by Denis Buzmakov on 03/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class BaseFragment extends MvpFragment {

	/**
	 * Кратко - такой костыль с тулбаром намного удобнее обычного тулбара.
	 * И для дизайна надо было, а то там тени какие-то и вот это все.
	 * @param parent - layout, на котором лежит 'toolbar'
	 * @param text - собственно, текст
	 */
	public void setTitle(View parent, String text) {
		TextView txtToolbarTitle = (TextView)parent.findViewById(R.id.txt_toolbar_title);
		if(txtToolbarTitle != null) {
			txtToolbarTitle.setText(text);
		}
	}
}
