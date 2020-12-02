package com.example.finder.views;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.R;
import com.example.finder.match.MatchViewFragment;
import com.example.finder.models.UserAccount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MatchView extends AppCompatActivity {

    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private int NUM_PAGES;

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager2 mPager;

    private final String err = "Huh... Doesn't look like there are any more available matches right now... Please Come Back Later";
    private UserAccount user;
    private RequestQueue que;
    private int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_view);

        Intent intent = getIntent();
        this.user = (UserAccount) intent.getSerializableExtra("profile");
        final ArrayList<UserAccount> matches = user.getMatches();
        NUM_PAGES = matches.size();

        if (NUM_PAGES == 0) {
            Toast.makeText(this, err, Toast.LENGTH_LONG).show();
            findViewById(R.id.match_err).setVisibility(View.VISIBLE);
        }

        this.que = Volley.newRequestQueue(this);
        this.page = 1;
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.match_pager);
        /*
         * The pager adapter, which provides the pages to the view pager widget.
         */
        final MatchViewFragmentAdapter pagerAdapter = new MatchViewFragmentAdapter(MatchView.this,
                matches, user.getId());
        mPager.setAdapter(pagerAdapter);
        /*
         * This makes the backend call to get more potential matches if available
         *
         */
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(final int position) {
                super.onPageSelected(position);
                if (position == NUM_PAGES - 1) {
                    final String URI = HomeView.HOST_URL + "/match/" + user.getId() + "/?page=" + page + HomeView.MATCH_LIMIT;
                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, URI, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ArrayList<UserAccount> newMatches = new ArrayList<>();
                            try {
                                parseEdges(response.getJSONArray("matches"), newMatches);
                                if (newMatches.isEmpty()) {
                                    Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
                                } else {
                                    page++;
                                    Log.d("MatchView", "Page: " + page);
                                    matches.addAll(newMatches);
                                    NUM_PAGES = matches.size();
                                    pagerAdapter.notifyItemRangeInserted(position + 1, NUM_PAGES - 1);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    que.add(req);
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: // Sends user back to HomeView
                Intent intent = new Intent(MatchView.this, HomeView.class);
                intent.putExtra("profile", user);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            Intent intent = new Intent(this, HomeView.class);
            intent.putExtra("profile", this.user);
            startActivity(intent);
            finish();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * Parses a JSONObject response retrieved from the backend and places them in the given
     * ArrayList
     *
     * @param matches JSONObject containg the server response
     * @param toBeMatched Arraylist to store the matches in
     * @throws JSONException if error parsing through server response
     */
    public static void parseEdges(JSONArray matches, ArrayList<UserAccount> toBeMatched) throws JSONException {
        for (int i = 0; i < matches.length(); i++) {
            JSONObject acc = matches.getJSONObject(i).getJSONObject("to");
            String firstName = acc.getString("firstName");
            String lastName = acc.getString("lastName");
            String email = acc.getString("email");
            String id = acc.getString("_id");
            String biography = acc.getString("description");
            String matchId = matches.getJSONObject(i).getString("_id");
            String profileURI = acc.getString("profileURL");
            int age = acc.getInt("age");
            UserAccount match = new UserAccount(id, firstName, lastName, email);
            match.setBiography(biography);
            match.setAge(age);
            match.setMatchId(matchId);
            match.setpfpUrl(Uri.parse(profileURI));
            toBeMatched.add(match);
        }
        Log.d("Matches", "Done finding matches");
        Log.d("Matches", "Matches" + matches.toString());
    }

    /**
     * Adapter to hold and show MatchViewFragments
     *
     */
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