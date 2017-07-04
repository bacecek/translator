package com.bacecek.translate.ui.favourites;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entity.Translation;
import com.bacecek.translate.event.ClickFavouriteEvent;
import com.bacecek.translate.mvp.favourites.FavouritePresenter;
import com.bacecek.translate.mvp.favourites.FavouriteView;
import com.bacecek.translate.ui.base.BaseFragment;
import com.bacecek.translate.util.adapter.TranslateAdapter;
import com.bacecek.translate.util.adapter.TranslateAdapter.OnItemClickListener;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import butterknife.OnTextChanged.Callback;
import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

/**
 * Created by Denis Buzmakov on 17/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class FavouriteFragment extends BaseFragment implements FavouriteView {
	@InjectPresenter
	FavouritePresenter mPresenter;

	@BindView(R.id.list_favourites)
	RecyclerView mRecyclerFavourites;
	@BindView(R.id.view_empty)
	View mViewEmpty;
	@BindView(R.id.edit_search)
	EditText mEditSearch;
	@BindView(R.id.txt_empty_title)
	TextView mTxtEmptyTitle;
	@BindView(R.id.btn_clear)
	ImageButton mBtnClear;

	@OnTextChanged(value = R.id.edit_search, callback = Callback.AFTER_TEXT_CHANGED)
	void onSearchTextChanged(Editable s) {
		mPresenter.onInputChanged(s.toString());
	}

	private final ItemTouchHelper.Callback mDismissCallback = new SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
		@Override
		public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder,
				ViewHolder target) {
			return false;
		}

		@Override
		public void onSwiped(ViewHolder viewHolder, int direction) {
			int position = viewHolder.getAdapterPosition();
			Translation translation = null;
			OrderedRealmCollection data = mTranslateAdapter.getData();
			if(data != null) {
				translation = (Translation) data.get(position);
			}

			mPresenter.onItemSwiped(translation);
		}
	};

	private final AdapterDataObserver mFavouritesDataObserver = new AdapterDataObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			mPresenter.onDataChanged(mTranslateAdapter.getItemCount());
		}
	};

	private final OnItemClickListener mOnFavouritesItemClickListener = translation -> EventBus
			.getDefault().post(new ClickFavouriteEvent(translation));

	private TranslateAdapter mTranslateAdapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_favourites, container, false);
		ButterKnife.bind(this, parent);

		setTitle(parent, getString(R.string.action_favourites));
		initUI();
		initClickListeners();

		return parent;
	}

	private void initUI() {
		Drawable leftDrawable = AppCompatResources.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_search);
		mEditSearch.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
		mRecyclerFavourites.setHasFixedSize(true);
		mRecyclerFavourites.setLayoutManager(new LinearLayoutManager(getActivity()));
		DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
		divider.setDrawable(getResources().getDrawable(R.drawable.list_divider));
		mRecyclerFavourites.addItemDecoration(divider);
		ItemTouchHelper helper = new ItemTouchHelper(mDismissCallback);
		helper.attachToRecyclerView(mRecyclerFavourites);
	}

	private void initClickListeners() {
		mBtnClear.setOnClickListener(v -> mEditSearch.setText(""));
	}

	@Override
	public void setSearchVisibility(boolean visible) {
		mEditSearch.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setEmptyViewVisibility(boolean visible) {
		mViewEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setListVisibility(boolean visible) {
		mRecyclerFavourites.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setButtonClearVisibility(boolean visible) {
		mBtnClear.setVisibility(visible ? View.VISIBLE : View.GONE);
	}

	@Override
	public void setEmptyViewText(@StringRes int text) {
		mTxtEmptyTitle.setText(text);
	}

	@Override
	public void setData(RealmResults<Translation> favourites) {
		mTranslateAdapter = new TranslateAdapter(getActivity(), favourites, mOnFavouritesItemClickListener);
		mTranslateAdapter.registerAdapterDataObserver(mFavouritesDataObserver);
		mFavouritesDataObserver.onChanged();
		mRecyclerFavourites.setAdapter(mTranslateAdapter);
	}

	@Override
	public void updateData(RealmResults<Translation> favourites) {
		if(mTranslateAdapter == null) {
			setData(favourites);
		} else {
			mTranslateAdapter.updateData(favourites);
		}
	}

	public static FavouriteFragment newInstance() {
		return new FavouriteFragment();
	}
}
