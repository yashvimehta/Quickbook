package com.example.quickbook;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.quickbook.FragmentSetAdmin.AdminIssuedBooksFragment;
import com.example.quickbook.FragmentSetAdmin.AdminCreateProfileFragment;
import com.example.quickbook.FragmentSetAdmin.SearchPageFragment;
import com.example.quickbook.FragmentSetAdmin.AdminSettingsFragment;
import com.example.quickbook.FragmentSetAdmin.AdminUploadPageFragment;

public class AdminFragmentAdapter extends FragmentPagerAdapter {

    public AdminFragmentAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        if (position==0){
            return new AdminSettingsFragment();
        }
        else if (position == 1) {
            return new AdminCreateProfileFragment();

        }
        else if (position == 2) {
            return new AdminUploadPageFragment();
        }
        else if (position ==3){
            return new SearchPageFragment();
        }
        else {
            return new AdminIssuedBooksFragment();
        }
    }

    @Override
    public int getCount()
    {
        return 5;
    }
}
