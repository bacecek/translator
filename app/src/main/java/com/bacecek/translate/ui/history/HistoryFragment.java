package com.bacecek.translate.ui.history;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
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
import com.bacecek.translate.mvp.history.HistoryPresenter;
import com.bacecek.translate.mvp.history.HistoryView;
import com.bacecek.translate.ui.base.BaseFragment;
import com.bacecek.translate.util.adapter.TranslateAdapter;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import io.realm.OrderedRealmCollection;
import io.realm.RealmResults;

/**
 * Created by Denis Buzmakov on 18.06.2017.
 * <buzmakov.da@gmail.com>
 */

public class HistoryFragment extends BaseFragment implements HistoryView {

    @InjectPresenter
    HistoryPresenter mPresenter;

    @BindView(R.id.list_history)
    RecyclerView mRecyclerHistory;
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

    private final RecyclerView.AdapterDataObserver mHistoryDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            mPresenter.onDataChanged(mTranslateAdapter.getItemCount());
        }
    };

    private final TranslateAdapter.OnItemClickListener mOnHistoryItemClickListener = translation -> EventBus
            .getDefault().post(new ClickFavouriteEvent(translation));

    private TranslateAdapter mTranslateAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, parent);

        setTitle(parent, getString(R.string.action_history));
        initUI();
        initClickListeners();

        return parent;
    }

    private void initUI() {
        Drawable leftDrawable = AppCompatResources.getDrawable(getActivity().getApplicationContext(), R.drawable.ic_search);
        mEditSearch.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        mRecyclerHistory.setHasFixedSize(true);
        mRecyclerHistory.setNestedScrollingEnabled(false);
        mRecyclerHistory.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration divider = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        divider.setDrawable(getResources().getDrawable(R.drawable.list_divider));
        mRecyclerHistory.addItemDecoration(divider);
        ItemTouchHelper helper = new ItemTouchHelper(mDismissCallback);
        helper.attachToRecyclerView(mRecyclerHistory);
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
        mRecyclerHistory.setVisibility(visible ? View.VISIBLE : View.GONE);
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
        mTranslateAdapter = new TranslateAdapter(getActivity(), favourites, mOnHistoryItemClickListener);
        mTranslateAdapter.registerAdapterDataObserver(mHistoryDataObserver);
        mHistoryDataObserver.onChanged();
        mRecyclerHistory.setAdapter(mTranslateAdapter);
    }

    @Override
    public void updateData(RealmResults<Translation> favourites) {
        if(mTranslateAdapter == null) {
            setData(favourites);
        } else {
            mTranslateAdapter.updateData(favourites);
        }
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }
}
