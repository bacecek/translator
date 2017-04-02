package com.bacecek.yandextranslate.utils;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import com.bacecek.yandextranslate.data.db.RealmController;
import com.bacecek.yandextranslate.data.db.entities.Translation;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Denis Buzmakov on 02/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class FavouriteDismissHelper extends ItemTouchHelper.SimpleCallback {
	private RealmRecyclerViewAdapter mAdapter;

	public FavouriteDismissHelper(RealmRecyclerViewAdapter adapter) {
		super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
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
			RealmController.getInstance().changeFavourite(translation);
		}
	}
}
