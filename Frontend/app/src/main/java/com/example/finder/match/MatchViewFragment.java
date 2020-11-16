package com.example.finder.match;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.finder.models.UserAccount;
import com.example.finder.views.HomeView;

import org.json.JSONObject;

public class MatchViewFragment extends Fragment {
    private UserAccount match;
    private String userId;

    public static MatchViewFragment createInstance(UserAccount match, String userId) {
        MatchViewFragment inst = new MatchViewFragment();
        Bundle args = new Bundle();
        args.putSerializable("match", match);
        args.putString("userId", userId);
        inst.setArguments(args);
        return inst;
    }

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        match = (UserAccount) getArguments().getSerializable("match");
        userId = (String) getArguments().getString("userId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_match_fragment,
                                                            container, false);
        final String HOST_URL = HomeView.HOST_URL + "/match/";
        TextView name = rootView.findViewById(R.id.match_name);
        name.setText(match.getFirstName() + " " + match.getLastName());
        TextView bio = rootView.findViewById(R.id.match_bio);
        bio.setText(match.getBiography());
        rootView.findViewById(R.id.match_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue que = Volley.newRequestQueue(getActivity());
                JsonObjectRequest req = new JsonObjectRequest(
                        Request.Method.PUT, HOST_URL + "approve/" + match.getMatchId() + "/" + userId,
                        null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(getActivity(), "Approval Sent!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), "Could not approve?", Toast.LENGTH_SHORT).show();
                    }
                });
                que.add(req);
            }
        });

        rootView.findViewById(R.id.match_deny).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue que = Volley.newRequestQueue(getActivity());
                JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT,
                        HOST_URL + "decline/" + match.getMatchId() + "/" + userId, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(getActivity(), "Goodbye " + match.getFirstName(), Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getActivity(), "Could not decline?", Toast.LENGTH_SHORT).show();
                        }
                });
                que.add(req);
            }
        });

        return rootView;
    }
}
