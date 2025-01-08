package com.alifia.mymaxxioadmin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MemberAdapter extends FragmentStateAdapter {

    public MemberAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new MemberPerluDitinjauFragment();
        } else if (position == 2) {
            return new MemberDitolakFragment();
        }
        return new MemberSemuaFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
