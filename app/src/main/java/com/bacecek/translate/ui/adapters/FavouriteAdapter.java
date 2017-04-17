package com.bacecek.translate.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entities.Translation;
import com.bacecek.translate.ui.adapters.FavouriteAdapter.ViewHolder;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Denis Buzmakov on 17/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class FavouriteAdapter extends RealmRecyclerViewAdapter<Translation, ViewHolder> {
	private OnItemClickListener mListener;

	public FavouriteAdapter(Context context,
			@Nullable OrderedRealmCollection<Translation> data,
			OnItemClickListener listener) {
		super(context, data, true);
		mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favourite, parent, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		OrderedRealmCollection<Translation> data = getData();
		if(data != null) {
			holder.bind(data.get(position), mListener);
		}
	}

	static class ViewHolder extends RecyclerView.ViewHolder{
		@BindView(R.id.txt_original)
		TextView mTxtOriginal;
		@BindView(R.id.txt_translated)
		TextView mTxtTranslated;
		@BindView(R.id.txt_langs)
		TextView mTxtLangs;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		public void bind(final Translation translation, final OnItemClickListener listener) {
			mTxtOriginal.setText(translation.getOriginalText());
			mTxtTranslated.setText(translation.getTranslatedText());
			mTxtLangs.setText(translation.getOriginalLang().toUpperCase() + "-" + translation.getTargetLang().toUpperCase());
			if(listener != null) {
				itemView.setOnClickListener(view -> listener.onItemClick(translation));
			}
		}
	}

	public interface OnItemClickListener {
		void onItemClick(Translation translation);
	}
}
