package com.bacecek.translate.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.bacecek.translate.R;
import com.bacecek.translate.data.entities.Translation;
import com.bacecek.translate.mvp.presenters.FavoritePresenter;
import com.bacecek.translate.mvp.views.FavoriteView;
import com.bacecek.translate.ui.adapters.FavoriteAdapter;
import com.bacecek.translate.ui.adapters.FavoriteAdapter.OnItemClickListener;
import com.bacecek.translate.ui.events.ClickFavouriteEvent;
import com.bacecek.translate.ui.events.ClickMenuEvent;
import com.jakewharton.rxbinding2.widget.RxTextView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;
import org.greenrobot.eventbus.EventBus;

/**
 * Created by Denis Buzmakov on 17/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class FavoriteFragment extends BaseFragment implements FavoriteView{
	@InjectPresenter
	FavoritePresenter mPresenter;

	@BindView(R.id.list_favourites)
	RecyclerView mRecyclerFavourites;
	@BindView(R.id.empty_view)
	View mViewEmpty;
	@BindView(R.id.view_search)
	View mSearchView;
	@BindView(R.id.edit_search)
	EditText mEditSearch;
	@BindView(R.id.txt_empty_title)
	TextView mTxtEmptyTitle;
	@BindView(R.id.btn_clear)
	ImageButton mBtnClear;

	@OnClick(R.id.btn_clear)
	void onClickClear() {
		mEditSearch.setText("");
	}

	@OnClick(R.id.btn_menu)
	void onClickMenu() {
		EventBus.getDefault().post(new ClickMenuEvent());
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
			OrderedRealmCollection data = mFavoriteAdapter.getData();
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
			mPresenter.onDataChanged(mFavoriteAdapter.getItemCount(), mEditSearch.getText().toString());
		}
	};

	private final OnItemClickListener mOnFavouritesItemClickListener = translation -> EventBus
			.getDefault().post(new ClickFavouriteEvent(
					translation.getOriginalText(),
					translation.getOriginalLang(),
					translation.getTargetLang()));

	private FavoriteAdapter mFavoriteAdapter;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_favourites, container, false);
		ButterKnife.bind(this, parent);

		setTitle(parent, getString(R.string.action_favourites));
		initUI();

		return parent;
	}

	private void initUI() {
		mRecyclerFavourites.setHasFixedSize(true);
		mRecyclerFavourites.setLayoutManager(new LinearLayoutManager(getActivity()));
		DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
		divider.setDrawable(getResources().getDrawable(R.drawable.list_divider_favourite));
		mRecyclerFavourites.addItemDecoration(divider);
		ItemTouchHelper helper = new ItemTouchHelper(mDismissCallback);
		helper.attachToRecyclerView(mRecyclerFavourites);
		mPresenter.setSearchObservable(RxTextView.afterTextChangeEvents(mEditSearch));
	}

	public static FavoriteFragment getInstance() {
		return new FavoriteFragment();
	}

	@Override
	public void showSearch() {
		mSearchView.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideSearch() {
		mSearchView.setVisibility(View.GONE);
	}

	@Override
	public void showEmptyView() {
		mViewEmpty.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideEmptyView() {
		mViewEmpty.setVisibility(View.GONE);
	}

	@Override
	public void showList() {
		mRecyclerFavourites.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideList() {
		mRecyclerFavourites.setVisibility(View.GONE);
	}

	@Override
	public void showButtonClear() {
		mBtnClear.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideButtonClear() {
		mBtnClear.setVisibility(View.GONE);
	}

	@Override
	public void setEmptyViewText(@StringRes int text) {
		mTxtEmptyTitle.setText(text);
	}

	@Override
	public void setData(RealmResults<Translation> favorites) {
		mFavoriteAdapter = new FavoriteAdapter(getActivity(), favorites, mOnFavouritesItemClickListener);
		mFavoriteAdapter.registerAdapterDataObserver(mFavouritesDataObserver);
		mFavouritesDataObserver.onChanged();
		mRecyclerFavourites.setAdapter(mFavoriteAdapter);
	}

	@Override
	public void updateData(RealmResults<Translation> favorites) {
		mFavoriteAdapter.updateData(favorites);
	}
}
