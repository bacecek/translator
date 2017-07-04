package com.bacecek.translate.util.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import com.bacecek.translate.ui.favourites.FavouriteFragment;
import com.bacecek.translate.ui.history.HistoryFragment;
import com.bacecek.translate.ui.settings.SettingsFragment;
import com.bacecek.translate.ui.translate.TranslateFragment;
import com.bacecek.translate.util.Consts;

/**
 * Created by Denis Buzmakov on 22.06.2017.
 * <buzmakov.da@gmail.com>
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case Consts.Pager.PAGE_TRANSLATE:
                return TranslateFragment.newInstance();
            case Consts.Pager.PAGE_HISTORY:
                return HistoryFragment.newInstance();
            case Consts.Pager.PAGE_FAVOURITES:
                return FavouriteFragment.newInstance();
            case Consts.Pager.PAGE_SETTINGS:
                return SettingsFragment.newInstance();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return Consts.Pager.MAIN_PAGES_COUNT;
    }
}
