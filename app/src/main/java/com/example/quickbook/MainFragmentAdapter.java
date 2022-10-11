package com.example.quickbook;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.example.quickbook.FragmentSetAdmin.IssuedBooksFragment;
import com.example.quickbook.FragmentSetAdmin.ProfileFragment;
import com.example.quickbook.FragmentSetAdmin.SearchPageFragment;
import com.example.quickbook.FragmentSetAdmin.UploadPageFragment;

public class MainFragmentAdapter  extends FragmentPagerAdapter {

    public MainFragmentAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position)
    {
        if (position==0){
            return new IssuedBooksFragment();
        }
        else if (position == 1) {
            return new ProfileFragment();

        }
        else if (position == 2) {
            return new UploadPageFragment();
        }
        else {
            return new SearchPageFragment();
        }
    }

    @Override
    public int getCount()
    {
        return 4;
    }
}
