package com.bacecek.translate.ui.translations;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.bacecek.translate.mvp.translations.TranslationsPresenter;
import com.bacecek.translate.mvp.translations.TranslationsView;
import com.bacecek.translate.ui.base.BaseFragment;
import com.bacecek.translate.util.Consts;
import com.bacecek.translate.util.adapter.TranslateAdapter;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

/**
 * Created by Denis Buzmakov on 05.07.2017.
 * <buzmakov.da@gmail.com>
 */

public class TranslationsFragment extends BaseFragment implements TranslationsView {

    @IntDef
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int HISTORY = 0;
        int FAVOURITES = 1;
    }

    @InjectPresenter
    TranslationsPresenter mPresenter;

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

    @OnTextChanged(value = R.id.edit_search, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void onSearchTextChanged(Editable s) {
        mPresenter.onInputChanged(s.toString());
    }

    private final ItemTouchHelper.Callback mDismissCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                              RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Translation translation = null;
            OrderedRealmCollection data = mTranslateAdapter.getData();
            if(data != null) {
                translation = (Translation) data.get(position);
            }

            mPresenter.onItemSwiped(translation);
        }
    };

    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            mPresenter.onDataChanged(mTranslateAdapter.getItemCount());
        }
    };

    private final TranslateAdapter.OnItemClickListener mOnFavouritesItemClickListener = new TranslateAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Translation translation) {
            EventBus.getDefault().post(new ClickFavouriteEvent(translation));
        }

        @Override
        public void onClickFavourite(Translation translation) {
            mPresenter.onClickItemFavourite(translation);
        }
    };

    private TranslateAdapter mTranslateAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_translations, container, false);
        ButterKnife.bind(this, parent);

        int type = getArguments().getInt(Consts.Extra.EXTRA_TYPE_TRANSLATIONS);
        if(type == Type.HISTORY) {
            setTitle(parent, getString(R.string.action_history));
        } else if (type == Type.FAVOURITES) {
            setTitle(parent, getString(R.string.action_favourites));
        }
        initUI();
        initClickListeners();

        mPresenter.setType(getArguments().getInt(Consts.Extra.EXTRA_TYPE_TRANSLATIONS));

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
        mTranslateAdapter.registerAdapterDataObserver(mDataObserver);
        mDataObserver.onChanged();
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

    public static TranslationsFragment newInstance(@Type int type) {
        Bundle args = new Bundle();
        args.putInt(Consts.Extra.EXTRA_TYPE_TRANSLATIONS, type);
        TranslationsFragment fragment = new TranslationsFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
