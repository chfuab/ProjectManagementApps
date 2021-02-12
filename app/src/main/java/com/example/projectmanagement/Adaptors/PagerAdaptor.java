package com.example.projectmanagement.Adaptors;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.projectmanagement.TaskDetails;
import com.example.projectmanagement.TaskDetailsAll;

import java.util.HashMap;

public class PagerAdaptor extends FragmentStateAdapter {
    private int pageIndexForTabs;
    private Bundle pageInitData = new Bundle();
    private HashMap<Integer, Integer> pageIndexForTabsAll;
    private HashMap<Integer, Bundle> pageInitDataAll;
    public PagerAdaptor(@NonNull Fragment fragment, HashMap<Integer, Integer> pageIndexForTabsAll,
                        HashMap<Integer, Bundle> pageInitDataAll) {
        super(fragment);
        this.pageIndexForTabsAll = pageIndexForTabsAll;
        this.pageInitDataAll = pageInitDataAll;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = new TaskDetails();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        pageIndexForTabs = pageIndexForTabsAll.get(position);
        pageInitData = pageInitDataAll.get(position);
        args.putInt("pageIndexForTabs", pageIndexForTabs);
        args.putBundle("pageInitData", pageInitData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}