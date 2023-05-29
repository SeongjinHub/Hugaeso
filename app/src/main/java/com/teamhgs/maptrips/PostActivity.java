package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class PostActivity extends AppCompatActivity {

    boolean likeStatus;
    String likeDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        Post currentPost = (Post) getIntent().getSerializableExtra(Post.POST);
        User currentUser = (User) getIntent().getSerializableExtra(User.CURRENT_USER);
        User postWriter = (User) getIntent().getSerializableExtra(User.POST_WRITER);

        ImageButton buttonBack = (ImageButton) findViewById(R.id.imageButton_header_back);
        ImageButton buttonMenu = (ImageButton) findViewById(R.id.imageButton_header_menu);
        TextView headerTitle = (TextView) findViewById(R.id.text_header_title);

        TextView postDate = (TextView) findViewById(R.id.text_post_date);
        TextView postText = (TextView) findViewById(R.id.text_post_text);
        TextView postUser = (TextView) findViewById(R.id.text_post_user);

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
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

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

        Response.Listener<String> postResponseListener = new Response.Listener<String>() {
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

                        Calendar calendar = Calendar.getInstance();

                        calendar.set(Calendar.YEAR, Integer.parseInt(currentPost.getDate().substring(0, 4)));
                        calendar.set(Calendar.MONTH, Integer.parseInt(currentPost.getDate().substring(5, 7)) - 1);
                        calendar.set(Calendar.DATE, Integer.parseInt(currentPost.getDate().substring(8, 10)));

                        String temp = calendar.get(Calendar.YEAR) + getString(R.string.activity_add_post_date_year)
                                + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.activity_add_post_date_month)
                                + calendar.get(Calendar.DATE) + getString(R.string.activity_add_post_date_day);

                        postDate.setText(temp);
                        postText.setText(currentPost.getText());
                        postUser.setText(postWriter.getUsername());
                    }
                    else {

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Post.getPostRequest getPostRequest = new Post.getPostRequest(currentPost.getPostcode(), postResponseListener);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getPostRequest);

        ImageButton buttonLike = (ImageButton) findViewById(R.id.imageButton_like);
        ImageButton buttonComments = (ImageButton) findViewById(R.id.imageButton_comments);
        ImageButton buttonBookmark = (ImageButton) findViewById(R.id.imageButton_bookmark);

        Response.Listener<String> getLikeResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean result = jsonResponse.getBoolean("responseResult");
                    if(result) {
                        likeStatus = true;
                        likeDate = jsonResponse.getString("datetime");
                        buttonLike.setImageResource(R.drawable.btn_post_like_selected);
                    }
                    else {
                        likeStatus = false;
                        buttonLike.setImageResource(R.drawable.btn_post_like_default);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        Post.getPostLikeRequest getPostLikeRequest = new Post.getPostLikeRequest(currentPost.getPostcode(), currentUser.getUsercode(), getLikeResponseListener);
        queue.add(getPostLikeRequest);

        Response.Listener<String> insertLikeResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean result = jsonResponse.getBoolean("responseResult");
                    if(result) {
                        queue.add(getPostLikeRequest);
                    }
                    else {

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Response.Listener<String> deleteLikeResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean result = jsonResponse.getBoolean("responseResult");
                    if(!result) {
                        queue.add(getPostLikeRequest);
                    }
                    else {

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Response.Listener<String> deletePostResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonResponse = null;
                try {
                    jsonResponse = new JSONObject(response);
                    boolean result = jsonResponse.getBoolean("responseResult");
                    if(!result) {
                        finish();
                    }
                    else {

                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                if (likeStatus) {
                    buttonLike.setImageResource(R.drawable.btn_post_like_default);
                    Post.deletePostLikeRequest deletePostLikeRequest = new Post.deletePostLikeRequest(currentPost.getPostcode(), currentUser.getUsercode(), likeDate, deleteLikeResponseListener);
                    queue.add(deletePostLikeRequest);
                }
                else {
                    buttonLike.setImageResource(R.drawable.btn_post_like_selected);
                    Post.insertPostLikeRequest insertPostLikeRequest = new Post.insertPostLikeRequest(currentPost.getPostcode(), currentUser.getUsercode(), insertLikeResponseListener);
                    queue.add(insertPostLikeRequest);
                }
            }
        });

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View postMenuDialogView = layoutInflater.inflate(R.layout.activity_post_menu, null, false);
        BottomSheetDialog postMenuDialog = new BottomSheetDialog(this);
        postMenuDialog.setContentView(postMenuDialogView);

        Button modifyPost = (Button) postMenuDialogView.findViewById(R.id.button_modify);
        Button deletePost = (Button) postMenuDialogView.findViewById(R.id.button_delete);

        View postMenuAlertDialogView = layoutInflater.inflate(R.layout.activity_post_menu_alert, null, false);
        BottomSheetDialog postMenuAlertDialog = new BottomSheetDialog(this);
        postMenuAlertDialog.setContentView(postMenuAlertDialogView);

        Button confirmDelete = (Button) postMenuAlertDialogView.findViewById(R.id.button_confirm);
        Button cancelDelete = (Button) postMenuAlertDialogView.findViewById(R.id.button_cancel);

        if (currentUser.getUsercode().equals(postWriter.getUsercode())) {
            modifyPost.setVisibility(View.VISIBLE);
            modifyPost.setVisibility(View.VISIBLE);
        }
        else {
            modifyPost.setVisibility(View.INVISIBLE);
            modifyPost.setVisibility(View.INVISIBLE);
        }
        modifyPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMenuDialog.dismiss();

            }
        });

        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMenuDialog.dismiss();
                postMenuAlertDialog.show();
            }
        });


        postMenuDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });

        // 상단 좌측 메뉴 버튼
        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMenuDialog.show();
            }
        });

        // 삭제 경고 다이얼로그
        confirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post.deletePostRequest deletePostRequest = new Post.deletePostRequest(currentUser.getUsercode(), currentPost, deletePostResponseListener);
                queue.add(deletePostRequest);
                postMenuAlertDialog.dismiss();
            }
        });

        // 삭제 경고 다이얼로그
        cancelDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMenuAlertDialog.dismiss();
                postMenuDialog.show();
            }
        });

//        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.mapView2, supportMapFragment)
//                .commitAllowingStateLoss();

    }
}