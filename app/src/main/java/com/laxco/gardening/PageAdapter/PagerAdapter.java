package com.laxco.gardening.PageAdapter;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import com.laxco.gardening.Fragments.Gardening;
import com.laxco.gardening.Fragments.Indoor;
import com.laxco.gardening.Fragments.Others;
import com.laxco.gardening.Fragments.Outdoor;
import com.laxco.gardening.Fragments.Pots;


public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                Indoor indoor = new Indoor();
                return indoor;
            case 1:
                Outdoor outdoor = new Outdoor();
                return outdoor;
            case 2:
                Gardening gardening = new Gardening();
                return gardening;
            case 3:
                Pots pots = new Pots();
                return pots;
            default:
                Others others = new Others();
                return others;
        }
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

