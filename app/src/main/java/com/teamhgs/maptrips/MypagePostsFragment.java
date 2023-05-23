package com.teamhgs.maptrips;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MypagePostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MypagePostsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    User currentUser;
    ArrayList<Post> postArrayList = new ArrayList<>();
    int postIndex;
    int num;


    public MypagePostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MypagePostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public MypagePostsFragment newInstance(User user) {
        MypagePostsFragment fragment = new MypagePostsFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mypage_posts, container, false);
    }

    /**
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout container = view.findViewById(R.id.container);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density; // get DPI.
//        float dpHeight = outMetrics.heightPixels / density; // get Height DP Val.
        float dpWidth = outMetrics.widthPixels / density; // get Width DP Val.

        // 3분할 이미지 뷰를 위한 Width, Height 값.
        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (dpWidth) / 3, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (dpWidth) / 3, getResources().getDisplayMetrics());

        // Usercode 와 일치하는 모든 Postcode를 검색하여 ArrayList에 추가.
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    JSONObject jsonObject;

                    if (jsonResponse.length() > 0) {
                        for (postIndex = 0; postIndex < jsonResponse.length(); postIndex++) {

                            jsonObject = jsonResponse.getJSONObject(postIndex);
                            String postcode = jsonObject.getString("postcode");

                            postArrayList.add(new Post(postcode));
                        }
                        postIndex = 0;
                    } else {
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                if (postArrayList.size() > 0) { // ArrayList에 Post 객체가 존재할 경우.
                    // Image Url 불러오기.
                    Response.Listener<String> responseListener_url = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                ArrayList<String> url = new ArrayList<>();

                                JSONArray jsonResponse_url = new JSONArray(response);
                                JSONObject jsonObject_url;

                                // 한 Post에 여러 이미지가 있을 경우 대비
                                String postcodePrev = null;
                                String postcodeNow;

                                if (jsonResponse_url.length() > 0) {
//                                    ArrayList<String> url = new ArrayList<>();
                                    for (int k = 0; k < jsonResponse_url.length(); k++) {
                                        jsonObject_url = jsonResponse_url.getJSONObject(k);
                                        postcodeNow = jsonObject_url.getString("postcode");

                                        if (!postcodeNow.equals(postcodePrev)) { // Postcode값 중복여부 검사 - 불일치.
                                            postcodePrev = postcodeNow;

                                            url = new ArrayList<>();
                                            url.add(jsonObject_url.getString("url"));

                                            // ArrayList<Post> 내 Postcode가 일치하는 요소 탐색.
                                            for (int l = 0; l < postArrayList.size(); l++) {
                                                if (postArrayList.get(l).getPostcode().equals(postcodeNow)) {
                                                    postArrayList.get(l).setUrl(url);
                                                    break;
                                                }
                                            }
                                        }
                                        else { // 중복될 경우
                                            // 이전에 생성한 ArrayList<String> url 객체에 요소 추가.
                                            url.add(jsonObject_url.getString("url"));
                                            for (int l = 0; l < postArrayList.size(); l++) {
                                                if (postArrayList.get(l).getPostcode().equals(postcodeNow)) {
                                                    postArrayList.get(l).setUrl(url);
                                                }
                                            }
                                        }
                                    }
                                    boolean downloadUrlStat = true;

                                    for (int l = 0; l < postArrayList.size(); l++) {
                                        if (postArrayList.get(l).getUrl().size() == 0)
                                            downloadUrlStat = false;
                                    }

                                    if (downloadUrlStat) { // 모든 URL 검색 및 저장 완료.

                                        int row = postArrayList.size();
                                        int col = postArrayList.size();

                                        for (int i = 0; i <= postArrayList.size() / 3; i++) { // 가로 3개.

                                            LinearLayout linearLayout = new LinearLayout(getActivity().getApplicationContext());
                                            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                                            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                                            row = row - 3;
                                            if (row < 0) {
                                                row = 0;
                                            }

                                            for (num = col - 1; num >= row; num--) { // 내림차순으로 3개씩 배치.
                                                ImageButton imageButton = new ImageButton(getActivity().getApplicationContext());
                                                imageButton.setLayoutParams(new ViewGroup.LayoutParams(width, height));
                                                imageButton.setTag(num);

                                                Glide.with(getActivity().getApplicationContext())
                                                        .load(postArrayList.get(num).getUrl().get(0))
                                                        .downsample(DownsampleStrategy.AT_LEAST)
                                                        .override(width-1, width-1)
                                                        .transition(DrawableTransitionOptions.withCrossFade())
                                                        .centerCrop()
                                                        .into(imageButton);
                                                imageButton.setBackground(getResources().getDrawable(R.drawable.imgview_sqaure_border));
                                                linearLayout.addView(imageButton);

                                                imageButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        int index = (int) v.getTag(); // View 객체의 Tag를 이용, ArrayList 내 객체 접근에 사용.
                                                        Intent intent = new Intent(getActivity(), MypagePostActivity.class);
                                                        intent.putExtra("Post", postArrayList.get(index));
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                            col = col - 3;
                                            container.addView(linearLayout);
                                        }

                                    }
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };

                    RequestQueue queue_url = Volley.newRequestQueue(getActivity().getApplicationContext());
                    Post.getPostsUrlListRequest request_url = new Post.getPostsUrlListRequest(currentUser.getUsercode(), responseListener_url);
                    queue_url.add(request_url);

                }
            }
        };
        Post.getPostsListRequest request = new Post.getPostsListRequest(currentUser.getUsercode(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);
        queue.start();
    }
}