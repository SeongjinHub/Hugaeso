package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
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
        Post.getPostRequest request = new Post.getPostRequest(currentPost.getPostcode(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View postMenuDialogView = layoutInflater.inflate(R.layout.activity_post_menu, null, false);
        BottomSheetDialog postMenuDialog = new BottomSheetDialog(this);
        postMenuDialog.setContentView(postMenuDialogView);

        Button modifyPost = (Button) postMenuDialogView.findViewById(R.id.button_modify);
        Button deletePost = (Button) postMenuDialogView.findViewById(R.id.button_delete);

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
            }
        });


        postMenuDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
            }
        });

        buttonMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postMenuDialog.show();
            }
        });

//        SupportMapFragment supportMapFragment = SupportMapFragment.newInstance();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.mapView2, supportMapFragment)
//                .commitAllowingStateLoss();

    }
}