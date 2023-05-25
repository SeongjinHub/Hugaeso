package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class PostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Post currentPost = (Post) getIntent().getSerializableExtra("Post");
        User currentUser = (User) getIntent().getSerializableExtra(User.CURRENT_USER);
        User postWriter = (User) getIntent().getSerializableExtra(User.POST_WRITER);

        ImageButton buttonBack = (ImageButton) findViewById(R.id.imageButton_header_back);
        ImageButton buttonMenu = (ImageButton) findViewById(R.id.imageButton_header_menu);
        TextView headerTitle = (TextView) findViewById(R.id.text_header_title);

        TextView postTitle = (TextView) findViewById(R.id.text_post_title);
        TextView postDate = (TextView) findViewById(R.id.text_post_date);
        TextView postText = (TextView) findViewById(R.id.text_post_text);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density; // get DPI.
//        float dpHeight = outMetrics.heightPixels / density; // get Height DP Val.
        float dpWidth = outMetrics.widthPixels / density; // get Width DP Val.

        // 3분할 이미지 뷰를 위한 Width, Height 값.
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpWidth, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) (dpWidth / 1.5), getResources().getDisplayMetrics());

        LinearLayout containerImg = findViewById(R.id.container_img);

        ImageView imageView = new ImageView(this);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));

        Glide.with(this)
                .load(currentPost.getUrl().get(0))
                .downsample(DownsampleStrategy.AT_LEAST)
//                .override(width-1, width-1)
                .transition(DrawableTransitionOptions.withCrossFade())
                .fitCenter()
                .into(imageView);
        imageView.setBackground(getResources().getDrawable(R.drawable.imgview_post_img));
        containerImg.addView(imageView);

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean result = jsonResponse.getBoolean("responseResult");
                    if(result) {
                        currentPost.setTitle(jsonResponse.getString("title"));
                        currentPost.setText(jsonResponse.getString("text"));
                        currentPost.setDate(jsonResponse.getString("date"));
                        currentPost.setCountry(jsonResponse.getString("country"));
                        currentPost.setCity(jsonResponse.getString("city"));
                        currentPost.setArea(jsonResponse.getString("area"));
                        currentPost.setPrivateStatus(Integer.parseInt(jsonResponse.getString("private")));

                        headerTitle.setText(currentPost.getTitle());
                        postTitle.setText(currentPost.getTitle());
                        postDate.setText(currentPost.getDate());
                        postText.setText(currentPost.getText());
                    }
                    else {

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Post.getPostRequest request = new Post.getPostRequest(currentPost.getPostcode(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);


        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.mapView2, supportMapFragment)
                .commitAllowingStateLoss();

    }
}