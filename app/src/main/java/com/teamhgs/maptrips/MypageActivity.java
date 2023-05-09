package com.teamhgs.maptrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

public class MypageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_mypage);
        overridePendingTransition(R.anim.none, R.anim.none);

        User defaultUser = (User) getIntent().getSerializableExtra("defaultUser");

        TextView header_title = (TextView) findViewById(R.id.text_header_title);

        header_title.setText("@" + defaultUser.getUsername());


        //For Debug
        String temp = defaultUser.getUsercode() + " " + defaultUser.getUsername() + " " + defaultUser.getName() + " " + defaultUser.getEmail();
        Toast.makeText(getApplicationContext(), "Userinfo = " + temp, Toast.LENGTH_LONG).show();

        Button buttonFeedTab = (Button) findViewById(R.id.button_feed);
        Button buttonSearchTab = (Button) findViewById(R.id.button_search);
        Button buttonWriteTab = (Button) findViewById(R.id.button_write);
        Button buttonFolderTab = (Button) findViewById(R.id.button_folder);
        Button buttonMypageTab = (Button) findViewById(R.id.button_mypage);

        buttonFeedTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, FeedActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
                finish();
            }
        });

        buttonSearchTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, SearchActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
                finish();
            }
        });

        buttonWriteTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, WriteActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
//                finish();
            }
        });

        buttonFolderTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MypageActivity.this, FolderActivity.class);
                intent.putExtra("defaultUser", defaultUser);
                startActivity(intent);
                finish();
            }
        });

//        btm_mypage_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MypageActivity.this, MypageActivity.class);
//                intent.putExtra("defaultUser", defaultUser);
//                startActivity(intent);
//                finish();
//            }
//        });

        ImageButton buttonSetting = (ImageButton) findViewById(R.id.button_setting);

        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = getSharedPreferences("com.teamhgs.maptrips.user", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("UserCode", "");
                editor.commit();

                startActivity(new Intent(MypageActivity.this, LoginActivity.class));
                finish();

            }
        });

        Button buttonAllPosts = (Button) findViewById(R.id.button_allpost);
        Button buttonFollowing = (Button) findViewById(R.id.button_following);
        Button buttonFollower = (Button) findViewById(R.id.button_follower);

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
            User.getPostsCountRequest request = new User.getPostsCountRequest(defaultUser.getUsercode(), responseListener);
            RequestQueue queue = Volley.newRequestQueue(MypageActivity.this);
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
            User.getFollowerCountRequest request = new User.getFollowerCountRequest(defaultUser.getUsercode(), responseListener);
            RequestQueue queue = Volley.newRequestQueue(MypageActivity.this);
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
            User.getFollowingCountRequest request = new User.getFollowingCountRequest(defaultUser.getUsercode(), responseListener);
            RequestQueue queue = Volley.newRequestQueue(MypageActivity.this);
            queue.add(request);
        }

        MypageViewPager2Adapter mypageViewPager2Adapter = new MypageViewPager2Adapter(getSupportFragmentManager(), getLifecycle());
        ViewPager2 viewPager2 = (ViewPager2) findViewById(R.id.viewPager2);
        viewPager2.setAdapter(mypageViewPager2Adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                if (position == 0) {
                    tab.setText(getResources().getString(R.string.activity_mypage_tab_posts));
                }
                else {
                    tab.setText(getResources().getString(R.string.activity_mypage_tab_favorites));
                }
            }
        }).attach();
    }
}