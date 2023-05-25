package com.teamhgs.maptrips;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FolderViewPager2Adapter extends FragmentStateAdapter {

    User currentUser;
    FolderMapFragment folderMapFragment;
    FolderListFragment folderListFragment;

    public FolderViewPager2Adapter(User user, @NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        currentUser = user;

        folderMapFragment = new FolderMapFragment().newInstance(currentUser);
        folderListFragment = new FolderListFragment().newInstance(currentUser);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return folderMapFragment;
            case 1:
                return folderListFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2; // 페이지 수
    }
}
