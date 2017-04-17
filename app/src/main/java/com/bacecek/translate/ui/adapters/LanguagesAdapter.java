package com.bacecek.translate.ui.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entities.Language;
import com.bacecek.translate.ui.adapters.LanguagesAdapter.ViewHolder;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Denis Buzmakov on 07/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class LanguagesAdapter extends RealmRecyclerViewAdapter<Language, ViewHolder> {
	private OnItemClickListener mListener;
	private String mCurrentLang;

	public LanguagesAdapter(Context context,
			@Nullable OrderedRealmCollection<Language> data,
			OnItemClickListener listener) {
		super(context, data, true);
		mListener = listener;
	}

	public LanguagesAdapter(Context context,
			@Nullable OrderedRealmCollection<Language> data,
			OnItemClickListener listener,
			String currentLang) {
		super(context, data, true);
		mListener = listener;
		mCurrentLang = currentLang;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		OrderedRealmCollection<Language> data = getData();
		if(data != null) {
			holder.bind(data.get(position), mListener, mCurrentLang);
		}
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.txt_lang)
		TextView mTxtLang;
		@BindView(R.id.img_check)
		ImageView mImgCheck;

		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		public void bind(final Language language, final OnItemClickListener listener, String currentLang) {
			mTxtLang.setText(language.getName());
			if(listener != null) {
				itemView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						listener.onItemClick(language.getCode());
					}
				});
			}
			if(currentLang != null) {
				if(language.getCode().equals(currentLang)) {
					mImgCheck.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	public interface OnItemClickListener {
		void onItemClick(String lang);
	}
}
