package com.bacecek.translate.ui.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entity.DictionaryExample;
import com.bacecek.translate.data.entity.DictionaryItem;
import com.bacecek.translate.data.entity.DictionaryMeanList;
import com.bacecek.translate.data.entity.DictionaryMeanList.DictionaryMean;
import com.bacecek.translate.data.entity.DictionaryPos;
import com.bacecek.translate.data.entity.DictionarySynonymList;
import com.bacecek.translate.data.entity.DictionarySynonymList.DictionarySynonym;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denis Buzmakov on 06/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class DictionaryAdapter extends RecyclerView.Adapter<ViewHolder> {
	private final static int TYPE_POS = 0;
	private final static int TYPE_SYNONYM = 1;
	private final static int TYPE_MEAN = 2;
	private final static int TYPE_EXAMPLE = 3;

	private List<DictionaryItem> mData;
	private OnWordClickListener mListener;

	public DictionaryAdapter(List<DictionaryItem> data, OnWordClickListener listener) {
		mData = data;
		mListener = listener;
	}

	@Override
	public int getItemViewType(int position) {
		if(mData.get(position) instanceof DictionaryPos) {
			return TYPE_POS;
		} else if(mData.get(position) instanceof DictionarySynonymList) {
			return TYPE_SYNONYM;
		} else if(mData.get(position) instanceof DictionaryMeanList) {
			return TYPE_MEAN;
		} else if(mData.get(position) instanceof DictionaryExample) {
			return TYPE_EXAMPLE;
		} else return -1;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		switch (viewType) {
			case TYPE_POS:
				view = inflater.inflate(R.layout.item_dictionary_pos, parent, false);
				return new PosViewHolder(view);
			case TYPE_SYNONYM:
				view = inflater.inflate(R.layout.item_dictionary_synonym, parent, false);
				return new SynonymViewHolder(view, mListener);
			case TYPE_MEAN:
				view = inflater.inflate(R.layout.item_dictionary_mean, parent, false);
				return new MeanViewHolder(view);
			case TYPE_EXAMPLE:
				view = inflater.inflate(R.layout.item_dictionary_example, parent, false);
				return new ExampleViewHolder(view);
			default:
				return null;
		}
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		switch (holder.getItemViewType()) {
			case TYPE_POS:
				PosViewHolder pvh = (PosViewHolder)holder;
				pvh.bind((DictionaryPos) mData.get(position));
				break;
			case TYPE_SYNONYM:
				SynonymViewHolder svh = (SynonymViewHolder)holder;
				svh.bind((DictionarySynonymList) mData.get(position));
				break;
			case TYPE_MEAN:
				MeanViewHolder mvh = (MeanViewHolder)holder;
				mvh.bind((DictionaryMeanList) mData.get(position));
				break;
			case TYPE_EXAMPLE:
				ExampleViewHolder evh = (ExampleViewHolder)holder;
				evh.bind((DictionaryExample) mData.get(position));
				break;
		}
	}

	@Override
	public int getItemCount() {
		return mData.size();
	}

	static class PosViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.txt_pos)
		TextView mTxtPos;

		PosViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		void bind(DictionaryPos item) {
			mTxtPos.setText(item.getText());
		}
	}

	static class SynonymViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.txt_synonym)
		TextView mTxtSynonyms;
		private OnWordClickListener mListener;

		SynonymViewHolder(View itemView, OnWordClickListener listener) {
			super(itemView);
			mListener = listener;
			ButterKnife.bind(this, itemView);
		}

		void bind(DictionarySynonymList list) {
			mTxtSynonyms.setMovementMethod(LinkMovementMethod.getInstance());
			SpannableStringBuilder result = new SpannableStringBuilder();
			ArrayList<DictionarySynonym> synonyms = list.getSynonyms();

			for (int i = 0; i < synonyms.size(); i++) {
				DictionarySynonym synonym = synonyms.get(i);
				if(i != 0) {
					result.append(", ");
				}
				int startClickableSpan = result.length();
				result.append(synonym.getText());
				int endClickableSpan = result.length();

				result.setSpan(new WordClickableSpan(mListener), startClickableSpan, endClickableSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				if(synonym.getGen() != null) {
					result.append(" ");
					int startGenSpan = result.length();
					result.append(synonym.getGen());
					int endGenSpan = result.length();
					result.setSpan(new ForegroundColorSpan(
							ContextCompat.getColor(itemView.getContext(), R.color.colorTextGrey)),
							startGenSpan,
							endGenSpan,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}

			mTxtSynonyms.setText(result);
		}

		private static class WordClickableSpan extends ClickableSpan {
			private OnWordClickListener mListener;

			public WordClickableSpan(OnWordClickListener listener) {
				mListener = listener;
			}

			@Override
			public void onClick(View view) {
				if(mListener != null) {
					TextView textView = (TextView)view;
					Spannable s = (Spannable) textView.getText();
					int startSpan = s.getSpanStart(this);
					int endSpan = s.getSpanEnd(this);
					String word = s.subSequence(startSpan, endSpan).toString();
					mListener.onWordClick(word);
				}
			}

			@Override
			public void updateDrawState(TextPaint ds) {
				super.updateDrawState(ds);
				ds.setUnderlineText(false);
			}
		}
	}

	static class MeanViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.txt_mean)
		TextView mTxtMeans;

		MeanViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		void bind(DictionaryMeanList list) {
			StringBuilder result = new StringBuilder();
			ArrayList<DictionaryMean> means = list.getMeans();

			for (int i = 0; i < means.size(); i++) {
				if(i != 0) {
					result.append(", ");
				}
				result.append(means.get(i).getText());
			}

			mTxtMeans.setText(result.toString());
		}
	}

	static class ExampleViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.txt_example)
		TextView mTxtExample;

		ExampleViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		void bind(DictionaryExample item) {
			StringBuilder result = new StringBuilder();
			result.append(item.getText());
			for (int i = 0; i < item.getTranslations().length; i++) {
				if(i == 0) {
					result.append(" - ");
				} else {
					result.append(", ");
				}
				result.append(item.getTranslations()[i]);
			}
			mTxtExample.setText(result.toString());
		}
	}

	public interface OnWordClickListener {
		void onWordClick(String word);
	}
}
