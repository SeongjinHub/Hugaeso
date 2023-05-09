package com.teamhgs.maptrips;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

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
    int i;


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

        TextView textView = (TextView) view.findViewById(R.id.text1);
        textView.setText(currentUser.getUsername() + currentUser.getEmail());

        // Usercode 와 일치하는 모든 Postcode를 검색하여 ArrayList에 추가.
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    JSONObject jsonObject;

                    if (jsonResponse.length() > 0) {
                        for (i = 0; i < jsonResponse.length(); i++) {

                            jsonObject = jsonResponse.getJSONObject(i);
                            String postcode = jsonObject.getString("postcode");

                            postArrayList.add(new Post(postcode));
                        }
                    } else {
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                i = 0;

                if (postArrayList.size() > 0) { // ArrayList에 Post 객체가 존재할 경우
                    for (int j = 0; j < postArrayList.size(); j++) { // 각 Postcode와 일치하는 이미지 Url을 반복 검색
                        Response.Listener<String> responseListener_url = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONArray jsonResponse_url = new JSONArray(response);
                                    JSONObject jsonObject_url;

                                    if (jsonResponse_url.length() > 0) {
                                        ArrayList<String> url = new ArrayList<>();
                                        for (int k = 0; k < jsonResponse_url.length(); k++) {

                                            jsonObject_url = jsonResponse_url.getJSONObject(k);
                                            String temp = jsonObject_url.getString("url");
                                            url.add(temp);
                                        }
                                        postArrayList.get(i).setUrl(url);
                                        i++;

                                        if (i == postArrayList.size()){ // 추후 작업영역.
                                            String temp = "";

                                            for (int i = 0; i < postArrayList.size(); i++) {
                                                temp = temp + postArrayList.get(i).getPostcode() + " " + postArrayList.get(i).getUrl().get(0) + "\n";
                                            }

                                            textView.setText(temp);
                                        }
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        };
                        RequestQueue queue_url = Volley.newRequestQueue(getActivity().getApplicationContext());
                        Post.getPostsUrlListRequest request_url = new Post.getPostsUrlListRequest(postArrayList.get(i).getPostcode(), responseListener_url);
                        queue_url.add(request_url);
                    }
                }
            }
        };
        Post.getPostsListRequest request = new Post.getPostsListRequest(currentUser.getUsercode(), responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(request);
        queue.start();
    }
}