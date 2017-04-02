package com.bacecek.yandextranslate.ui.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.AdapterDataObserver;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTextChanged.Callback;
import com.bacecek.yandextranslate.R;
import com.bacecek.yandextranslate.data.db.RealmController;
import com.bacecek.yandextranslate.ui.adapters.FavouritesAdapter;
import com.bacecek.yandextranslate.utils.FavouriteDismissHelper;

/**
 * Created by Denis Buzmakov on 17/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class FavouritesFragment extends Fragment {
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

	@OnTextChanged(value = R.id.edit_search, callback = Callback.AFTER_TEXT_CHANGED)
	void onSearchTextChanged(Editable editable) {
		if(editable.length() != 0) {
			mBtnClear.setVisibility(View.VISIBLE);
			mTxtEmptyTitle.setText(R.string.empty_search);
		} else {
			mBtnClear.setVisibility(View.INVISIBLE);
			mTxtEmptyTitle.setText(R.string.empty_list_favourites);
		}
		filterFavourites(editable.toString());
	}

	private FavouritesAdapter mFavouritesAdapter;

	private AdapterDataObserver mFavouritesDataObserver = new AdapterDataObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			if(mFavouritesAdapter.getItemCount() == 0) {
				mViewEmpty.setVisibility(View.VISIBLE);
				mRecyclerFavourites.setVisibility(View.GONE);
				if(mEditSearch.getText().length() == 0)
					mSearchView.setVisibility(View.GONE);
			} else {
				mViewEmpty.setVisibility(View.GONE);
				mRecyclerFavourites.setVisibility(View.VISIBLE);
				mSearchView.setVisibility(View.VISIBLE);
			}
		}
	};

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_favourites, container, false);
		ButterKnife.bind(this, parent);

		initUI();

		return parent;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		mFavouritesAdapter.unregisterAdapterDataObserver(mFavouritesDataObserver);
	}

	private void initUI() {
		mFavouritesAdapter = new FavouritesAdapter(getActivity(), RealmController.getInstance().getFavourites(), null);
		mRecyclerFavourites.setAdapter(mFavouritesAdapter);
		mRecyclerFavourites.setHasFixedSize(true);
		mRecyclerFavourites.setLayoutManager(new LinearLayoutManager(getActivity()));
		DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
		divider.setDrawable(getResources().getDrawable(R.drawable.list_divider_favourite));
		mRecyclerFavourites.addItemDecoration(divider);
		ItemTouchHelper.Callback callback = new FavouriteDismissHelper(mFavouritesAdapter);
		ItemTouchHelper helper = new ItemTouchHelper(callback);
		helper.attachToRecyclerView(mRecyclerFavourites);

		mFavouritesAdapter.registerAdapterDataObserver(mFavouritesDataObserver);
	}

	private void filterFavourites(String search) {
		mFavouritesAdapter = new FavouritesAdapter(getActivity(), RealmController.getInstance().getFavourites(search), null);
		mFavouritesAdapter.registerAdapterDataObserver(mFavouritesDataObserver);
		mRecyclerFavourites.setAdapter(mFavouritesAdapter);
		//TODO:проверить удаление жестом - удаляется не тот элемент
	}

	public static FavouritesFragment getInstance() {
		return new FavouritesFragment();
	}
}
