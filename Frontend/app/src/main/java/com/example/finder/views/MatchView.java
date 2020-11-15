package com.example.finder.views;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.finder.match.MatchViewFragment;
import com.example.finder.R;
import com.example.finder.models.UserAccount;

import java.util.ArrayList;

public class MatchView extends FragmentActivity {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private int NUM_PAGES;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    private UserAccount user;
    private ArrayList<UserAccount> matches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);

        Intent intent = getIntent();
        user = (UserAccount) intent.getSerializableExtra("user");
        matches = (ArrayList<UserAccount>) intent.getSerializableExtra("matches");
        NUM_PAGES = matches.size();

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.match_pager);
        /**
         * The pager adapter, which provides the pages to the view pager widget.
         */
        PagerAdapter pagerAdapter = new MatchViewFragmentAdapter(getSupportFragmentManager(), matches, user.getId());
        mPager.setAdapter(pagerAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }


    private class MatchViewFragmentAdapter extends FragmentStatePagerAdapter {
        private ArrayList<UserAccount> matches;
        private String userId;
        public MatchViewFragmentAdapter(FragmentManager fm, ArrayList<UserAccount> matches, String userId) {
            super(fm);
            this.matches = matches;
            this.userId = userId;
        }

        @Override
        public Fragment getItem(int position) {
            return MatchViewFragment.createInstance(matches.get(position), userId);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

}