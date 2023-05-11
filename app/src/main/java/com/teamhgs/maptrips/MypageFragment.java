package com.teamhgs.maptrips;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MypageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MypageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    // TODO: Rename and change types of parameters
    User currentUser;
    ViewGroup viewGroup;
    public MypageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MypageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MypageFragment newInstance(User user) {
        MypageFragment fragment = new MypageFragment();
        Bundle args = new Bundle();
        args.putSerializable(User.CURRENT_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = (User) getArguments().getSerializable(User.CURRENT_USER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_mypage, container, false);

        TextView header_title = (TextView) viewGroup.findViewById(R.id.text_header_title);

        header_title.setText("@" + currentUser.getUsername());

        ImageButton buttonSetting = (ImageButton) viewGroup.findViewById(R.id.button_setting);

        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = getActivity().getSharedPreferences("com.teamhgs.maptrips.user", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("UserCode", "");
                editor.commit();

                startActivity(new Intent(getActivity().getApplicationContext(), LoginActivity.class));
                getActivity().finish();

            }
        });

        Button buttonAllPosts = (Button) viewGroup.findViewById(R.id.button_allpost);
        Button buttonFollowing = (Button) viewGroup.findViewById(R.id.button_following);
        Button buttonFollower = (Button) viewGroup.findViewById(R.id.button_follower);

        { // Get Count of current User's all posts.
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean result = jsonResponse.getBoolean("responseResult");

                        if (result) {
                            String temp = jsonResponse.getString("posts") + "\n" + getResources().getString(R.string.activity_mypage_posts);
                            buttonAllPosts.setText(temp);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            User.getPostsCountRequest request = new User.getPostsCountRequest(currentUser.getUsercode(), responseListener);
            RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
            queue.add(request);
        }

        { // Get Count of current User's followers.
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean result = jsonResponse.getBoolean("responseResult");

                        if (result) {
                            String temp = jsonResponse.getString("follower") + "\n" + getResources().getString(R.string.activity_mypage_follower);
                            buttonFollower.setText(temp);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            User.getFollowerCountRequest request = new User.getFollowerCountRequest(currentUser.getUsercode(), responseListener);
            RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
            queue.add(request);
        }

        { // Get Count of current User's following.
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean result = jsonResponse.getBoolean("responseResult");

                        if (result) {
                            String temp = jsonResponse.getString("following") + "\n" + getResources().getString(R.string.activity_mypage_following);
                            buttonFollowing.setText(temp);
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            User.getFollowingCountRequest request = new User.getFollowingCountRequest(currentUser.getUsercode(), responseListener);
            RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
            queue.add(request);
        }

        // ViewPager2 를 이용해 좌,우 슬라이드 제스쳐 및 탭 기능을 구현
        MypageViewPager2Adapter mypageViewPager2Adapter = new MypageViewPager2Adapter(currentUser, getActivity());
        ViewPager2 viewPager2 = (ViewPager2) viewGroup.findViewById(R.id.viewPager2);
        viewPager2.setAdapter(mypageViewPager2Adapter);

        TabLayout tabLayout = (TabLayout) viewGroup.findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                if (position == 0) {
                    tab.setText(getResources().getString(R.string.activity_mypage_tab_posts));
                } else {
                    tab.setText(getResources().getString(R.string.activity_mypage_tab_favorites));
                }
            }
        }).attach();

        // Inflate the layout for this fragment
        return viewGroup;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}
