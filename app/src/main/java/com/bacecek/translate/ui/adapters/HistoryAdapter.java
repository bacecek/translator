package com.bacecek.translate.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entities.Translation;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Denis Buzmakov on 20/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class HistoryAdapter extends RealmRecyclerViewAdapter<Translation, HistoryAdapter.ViewHolder> {
	private OnItemClickListener mListener;

	public HistoryAdapter(@Nullable OrderedRealmCollection<Translation> data,
			OnItemClickListener listener) {
		super(data, true);
		mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
		return new ViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		OrderedRealmCollection<Translation> data = getData();
		if(data != null) {
			holder.bind(data.get(position), mListener);
		}
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.txt_original)
		TextView mTxtOriginal;
		@BindView(R.id.txt_translated)
		TextView mTxtTranslated;
		@BindView(R.id.btn_favourite)
		ImageButton mBtnFavourite;
		@BindView(R.id.txt_direction)
		TextView mTxtDirection;

		ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		void bind(final Translation translation, final OnItemClickListener listener) {
			mTxtOriginal.setText(translation.getOriginalText());
			mTxtTranslated.setText(translation.getTranslatedText());
			mBtnFavourite.setActivated(translation.isFavourite());
			String direction = (translation.getOriginalLang() + "-" + translation.getTargetLang()).toUpperCase();
			mTxtDirection.setText(direction);
			if(listener != null) {
				itemView.setOnClickListener(view -> listener.onItemClick(translation));
				mBtnFavourite.setOnClickListener(view -> listener.onClickFavourite(translation));
			}
		}
	}

	public interface OnItemClickListener {
		void onItemClick(Translation translation);
		void onClickFavourite(Translation translation);
	}
}
