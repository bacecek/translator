package com.bacecek.translate.utils;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.bacecek.translate.data.db.RealmController;
import com.bacecek.translate.data.db.entities.Translation;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Denis Buzmakov on 02/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class HistoryDismissTouchHelper extends ItemTouchHelper.SimpleCallback {
	private RealmRecyclerViewAdapter mAdapter;

	public HistoryDismissTouchHelper(RealmRecyclerViewAdapter adapter) {
		super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
		mAdapter = adapter;
	}

	@Override
	public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
		return false;
	}

	@Override
	public void onSwiped(ViewHolder viewHolder, int direction) {
		OrderedRealmCollection data = mAdapter.getData();
		if(data != null) {
			Translation translation = (Translation) data.get(viewHolder.getAdapterPosition());
			RealmController.getInstance().removeTranslation(translation);
		}
	}
}
