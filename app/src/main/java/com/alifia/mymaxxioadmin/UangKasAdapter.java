package com.alifia.mymaxxioadmin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class UangKasAdapter extends FragmentStateAdapter {
    public UangKasAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new UangKasPribadiFragment();
        } else if (position == 2) {
            return new UangKasPerluDitinjauFragment();
        }
        return new UangKasChapterFragment();
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
