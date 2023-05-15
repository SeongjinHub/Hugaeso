package com.teamhgs.maptrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();
    FeedFragment feedFragment = new FeedFragment();
    SearchFragment searchFragment;
    WriteFragment writeFragment;
    FolderFragment folderFragment;
    MypageFragment mypageFragment;
    BottomNavigationView bottomNavigationView;
    FragmentTransaction transaction;

    public final String TAG_FEED = "Feed";
    public final String TAG_SEARCH = "Search";
    public final String TAG_WRITE = "Write";
    public final String TAG_FOLDER = "Folder";
    public final String TAG_MYPAGE = "Mypage";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.none, R.anim.none);

        User currentUser = (User) getIntent().getSerializableExtra(User.CURRENT_USER);

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean result = jsonResponse.getBoolean("responseResult");

                    if (result) {
                        currentUser.setUsername(jsonResponse.getString("username"));
                        currentUser.setName(jsonResponse.getString("name"));
                        currentUser.setEmail(jsonResponse.getString("email"));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        User.getUserInfoRequest request = new User.getUserInfoRequest(currentUser.getUsercode(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        queue.add(request);

        bottomNavigationView = findViewById(R.id.bottom_navi);
        bottomNavigationView.setSelectedItemId(R.id.folder);

        feedFragment = new FeedFragment().newInstance(currentUser);
        searchFragment = new SearchFragment().newInstance(currentUser);
//        writeFragment = new WriteFragment().newInstance(currentUser);
        folderFragment = new FolderFragment().newInstance(currentUser);
        mypageFragment = new MypageFragment().newInstance(currentUser);

        transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, folderFragment, TAG_FOLDER).commitAllowingStateLoss();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.feed:
                        transaction.replace(R.id.container, feedFragment, TAG_FEED).commitAllowingStateLoss();
                        break;
                    case R.id.search:
                        transaction.replace(R.id.container, searchFragment, TAG_SEARCH).commitAllowingStateLoss();
                        break;
                    case R.id.write:
//                        transaction.replace(R.id.container, writeFragment).addToBackStack(null).commit();
                        Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                        intent.putExtra(User.CURRENT_USER, currentUser);
                        startActivity(intent);
                        break;
                    case R.id.folder:
                        transaction.replace(R.id.container, folderFragment, TAG_FOLDER).commitAllowingStateLoss();
                        break;
                    case R.id.mypage:
                        transaction.replace(R.id.container, mypageFragment, TAG_MYPAGE).commitAllowingStateLoss();
                        break;
                }
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        bottomNavigationView = findViewById(R.id.bottom_navi);

        Fragment feed = fragmentManager.findFragmentByTag(TAG_FEED);
        Fragment search = fragmentManager.findFragmentByTag(TAG_SEARCH);
//        Fragment write = fragmentManager.findFragmentByTag(TAG_WRITE);
        Fragment folder = fragmentManager.findFragmentByTag(TAG_FOLDER);
        Fragment mypage = fragmentManager.findFragmentByTag(TAG_MYPAGE);

        if (feed != null && feed.isVisible()) {
            bottomNavigationView.getMenu().findItem(R.id.feed).setChecked(true);
        }
        else if (search != null && search.isVisible()) {
            bottomNavigationView.getMenu().findItem(R.id.search).setChecked(true);
        }
//        else if (write != null && write.isVisible()) {
//            bottomNavigationView.getMenu().findItem(R.id.write).setChecked(true);
//        }
        else if (folder != null && folder.isVisible()) {
            bottomNavigationView.getMenu().findItem(R.id.folder).setChecked(true);
        }
        else if (mypage != null && mypage.isVisible()) {
            bottomNavigationView.getMenu().findItem(R.id.mypage).setChecked(true);
        }


    }
}