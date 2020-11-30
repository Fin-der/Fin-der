package com.example.finder.match;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.finder.R;
import com.example.finder.controller.ImageLoaderHelper;
import com.example.finder.models.UserAccount;
import com.example.finder.views.HomeView;

import org.json.JSONObject;

/**
 * Fragment holding the profile picture, biography, and name of the potential match for
 * the user
 */
public class MatchViewFragment extends Fragment {
    private UserAccount match;
    private String userId;
    private final String HOST_URL = HomeView.HOST_URL + "/match/";

    /**
     * Creates the fragment instance and bundle containing relevant information
     *
     * @param match The potential match for the user
     * @param userId The user's ID
     * @return MathViewFragment to be loaded onto MatchView
     */
    public static MatchViewFragment createInstance(UserAccount match, String userId) {
        MatchViewFragment inst = new MatchViewFragment();
        Bundle args = new Bundle();
        args.putSerializable("match", match);
        args.putString("userId", userId);
        inst.setArguments(args);
        return inst;
    }

    /**
     * Retrieves relevant information and creates instance
     *
     * @param savedInstance Contains information about userId and the potential match
     */
    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        match = (UserAccount) getArguments().getSerializable("match");
        userId = getArguments().getString("userId");
    }

    /**
     * Loads fragment onto screen
     * Attach approve() and deny() functions for swiping and buttons
     *
     * @param inflater Inflator
     * @param container Container
     * @param savedInstanceState savedInstanceState
     * @return the created MathViewFragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_match_fragment,
                                                            container, false);
        TextView name = rootView.findViewById(R.id.match_name);

        name.setText(match.getFirstName() + " " + match.getLastName());

        TextView bio = rootView.findViewById(R.id.match_bio);

        bio.setText(match.getBiography());

        ImageView profilePic = rootView.findViewById(R.id.match_profilePic);

        ImageLoaderHelper.loadProfilePic(getActivity(), profilePic, match.getpfpUrl(),
                                        profilePic.getWidth(), profilePic.getHeight());

        rootView.findViewById(R.id.match_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               approve(rootView);
            }
        });

        rootView.findViewById(R.id.match_deny).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deny(rootView);
            }
        });

        rootView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight() {
                approve(rootView);
            }
            public void onSwipeLeft() {
                deny(rootView);
            }
        });

        return rootView;
    }

    /**
     * Function handling user approval of potential match
     *
     * @param view MatchViewFragment to attach function onto
     */
    private void approve(final View view) {
        RequestQueue que = Volley.newRequestQueue(getActivity());
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.PUT, HOST_URL + "approve/" + match.getMatchId() + "/" + userId,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(getActivity(), "Approval Sent!", Toast.LENGTH_SHORT).show();
                view.setBackgroundResource(R.color.green);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.match_approve_err, Toast.LENGTH_SHORT).show();
            }
        });
        que.add(req);
    }

    /**
     * Function handling user disapproval/decline of potential match
     *
     * @param view MatchViewFragment to attach function onto
     */
    private void deny(final View view) {
        RequestQueue que = Volley.newRequestQueue(getActivity());
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,
                HOST_URL + "decline/" + match.getMatchId() + "/" + userId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getActivity(), "Goodbye " + match.getFirstName(), Toast.LENGTH_SHORT).show();
                        view.setBackgroundResource(R.color.red);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.match_deny_err, Toast.LENGTH_SHORT).show();
            }
        });
        que.add(req);
    }

}
