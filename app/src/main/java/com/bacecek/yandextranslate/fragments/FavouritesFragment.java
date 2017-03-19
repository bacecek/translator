package com.bacecek.yandextranslate.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.bacecek.yandextranslate.R;
import com.bacecek.yandextranslate.adapters.FavouritesAdapter;
import com.bacecek.yandextranslate.entities.Translation;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Created by Denis Buzmakov on 17/03/2017.
 * <buzmakov.da@gmail.com>
 */

public class FavouritesFragment extends Fragment {
	@BindView(R.id.list_favourites)
	RecyclerView mRecyclerFavourites;
	@BindView(R.id.empty_view)
	View mViewEmpty;

	private FavouritesAdapter mFavouritesAdapter;
	private Realm mRealm;
	RealmResults<Translation> mData;

	private RealmChangeListener<RealmResults<Translation>> mChangeListener = new RealmChangeListener<RealmResults<Translation>>() {
		@Override
		public void onChange(RealmResults<Translation> results) {
			if(mFavouritesAdapter != null) {
				if(mFavouritesAdapter.getItemCount() == 0) {
					mViewEmpty.setVisibility(View.VISIBLE);
					mRecyclerFavourites.setVisibility(View.GONE);
				} else {
					mViewEmpty.setVisibility(View.GONE);
					mRecyclerFavourites.setVisibility(View.VISIBLE);
				}
			}
		}
	};

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
		View parent = inflater.inflate(R.layout.fragment_favourites, container, false);
		ButterKnife.bind(this, parent);

		mRealm = Realm.getDefaultInstance();
		mData = mRealm.where(Translation.class).equalTo("isFavourite", true).findAll();
		mData.addChangeListener(mChangeListener);

		initUI();

		return parent;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		mData.removeChangeListener(mChangeListener);
	}

	private void initUI() {
		mFavouritesAdapter = new FavouritesAdapter(getActivity(), mData, null);
		mRecyclerFavourites.setHasFixedSize(true);
		mRecyclerFavourites.setLayoutManager(new LinearLayoutManager(getActivity()));
		mRecyclerFavourites.setAdapter(mFavouritesAdapter);
	}

	public static FavouritesFragment getInstance() {
		return new FavouritesFragment();
	}
}
