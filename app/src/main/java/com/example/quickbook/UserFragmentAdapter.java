package com.example.quickbook;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.quickbook.FragmentSetAdmin.SearchPageFragment;
import com.example.quickbook.FragmentSetUser.UserIssuedBooksFragment;
import com.example.quickbook.FragmentSetUser.UserSettingsFragment;

public class
UserFragmentAdapter extends FragmentPagerAdapter {

    public UserFragmentAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        if (position==0){
            return new UserSettingsFragment();
        }
        else if (position ==1){
            return new SearchPageFragment();
        }
        else {
            return new UserIssuedBooksFragment();
        }
    }

    @Override
    public int getCount()
    {
        return 3;
    }
}
