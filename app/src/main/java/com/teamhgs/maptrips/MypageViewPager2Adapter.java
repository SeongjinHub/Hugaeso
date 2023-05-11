package com.teamhgs.maptrips;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MypageViewPager2Adapter extends FragmentStateAdapter {

    User currentUser;
    MypagePostsFragment mypagePostsFragment;
    MypageFavoritesFragment mypageFavoritesFragment;

    public MypageViewPager2Adapter(User user, @NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        currentUser = user;

        mypagePostsFragment = new MypagePostsFragment().newInstance(currentUser);
        mypageFavoritesFragment = new MypageFavoritesFragment().newInstance(currentUser);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return mypagePostsFragment;
            case 1:
                return mypageFavoritesFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 페이지 수
    }
}
