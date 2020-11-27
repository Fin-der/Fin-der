package com.example.finder.views;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
    private ViewPager2 mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);

        Intent intent = getIntent();
        UserAccount user = (UserAccount) intent.getSerializableExtra("profile");
        ArrayList<UserAccount> matches = user.getMatches();
        NUM_PAGES = matches.size();
        final String err = "Huh... Doesn't look like there are any available matches right now... Please Come Back Later";
        if (NUM_PAGES == 0) {
            Toast.makeText(this, err, Toast.LENGTH_LONG).show();
            findViewById(R.id.match_err).setVisibility(View.VISIBLE);
        }
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.match_pager);
        /**
         * The pager adapter, which provides the pages to the view pager widget.
         */
        MatchViewFragmentAdapter pagerAdapter = new MatchViewFragmentAdapter(MatchView.this,
                matches, user.getId());
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


    private class MatchViewFragmentAdapter extends FragmentStateAdapter {
        private ArrayList<UserAccount> matches;
        private String userId;
        public MatchViewFragmentAdapter(FragmentActivity fm, ArrayList<UserAccount> matches, String userId) {
            super(fm);
            this.matches = matches;
            this.userId = userId;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return MatchViewFragment.createInstance(matches.get(position), userId);
        }

        @Override
        public int getItemCount() {
            return NUM_PAGES;
        }
    }

}