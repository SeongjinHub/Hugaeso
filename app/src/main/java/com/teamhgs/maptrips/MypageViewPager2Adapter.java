package com.teamhgs.maptrips;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MypageViewPager2Adapter extends FragmentStateAdapter {

    public MypageViewPager2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MypagePostsFragment();
            case 1:
                return new MypageFavoritesFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 페이지 수
    }
}
