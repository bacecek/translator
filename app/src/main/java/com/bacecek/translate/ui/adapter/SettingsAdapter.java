package com.bacecek.translate.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entity.Setting;
import java.util.ArrayList;

/**
 * Created by Denis Buzmakov on 16/04/2017.
 * <buzmakov.da@gmail.com>
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {
	private ArrayList<Setting> mData = new ArrayList<Setting>();
	private OnClickItemSwitchListener mListener;

	public SettingsAdapter(ArrayList<Setting> data, OnClickItemSwitchListener listener) {
		mData = data;
		mListener = listener;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting, parent, false);
		return new ViewHolder(view);
	}

	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		holder.bind(mData.get(position), mListener);
	}

	@Override
	public int getItemCount() {
		return mData.size();
	}

	static class ViewHolder extends RecyclerView.ViewHolder {
		@BindView(R.id.txt_title)
		TextView mTxtTitle;
		@BindView(R.id.view_switch)
		SwitchCompat mSwitch;

		public ViewHolder(View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}

		public void bind(Setting setting, OnClickItemSwitchListener listener) {
			mTxtTitle.setText(setting.getName());
			mSwitch.setChecked(setting.getValue());
			if(listener != null) {
				mSwitch.setOnCheckedChangeListener((button, value) -> listener.onClickItemSwitch(setting, value));
			}
		}
	}

	public interface OnClickItemSwitchListener {
		void onClickItemSwitch(Setting setting, boolean value);
	}
}
