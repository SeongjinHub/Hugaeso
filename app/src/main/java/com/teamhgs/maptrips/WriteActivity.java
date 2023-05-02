package com.teamhgs.maptrips;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WriteActivity extends AppCompatActivity {

    ImageButton buttonAddImage;
    String filename;
    String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        overridePendingTransition(R.anim.none, R.anim.none);

        User defaultUser = (User) getIntent().getSerializableExtra("defaultUser");

        Post post = new Post();

        buttonAddImage = (ImageButton) findViewById(R.id.Button_addimage);

        Button buttonUpload = (Button) findViewById(R.id.button_upload);

        EditText etTitle = (EditText) findViewById(R.id.et_title);
        EditText etText = (EditText) findViewById(R.id.et_text);

        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 101);
            }
        });

        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                post.setTitle(etTitle.getText().toString());
            }
        });

        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                post.setText(etText.getText().toString());
            }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    post.setTitle(etTitle.getText().toString());

                    if (post.getTitle().length() < 1) {
                        post.setTitle("No Tilte");
                    }

                    post.setText(etText.getText().toString());

                    if (post.getText().length() < 1) {
                        post.setText("\n");
                    }
                } catch (Exception e) {

                }

                if (filename != null) {
                    try {
                        imgPath = getCacheDir() + "/" + filename;   // 내부 저장소에 저장되어 있는 이미지 경로

                        post.uploadStorageStream(imgPath, filename);
                        post.setUrl("Posts/" + post.getPostcode() + "/" + filename);

                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonResponse = new JSONObject(response);
                                    boolean result = jsonResponse.getBoolean("responseResult");

                                    if (result) {
                                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonResponse = new JSONObject(response);
                                                    boolean result = jsonResponse.getBoolean("responseResult");

                                                    if (result) {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_write_upload_complete), Toast.LENGTH_LONG).show();
                                                        finish();
                                                    } else {
                                                        deleteCacheImg();
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_write_upload_err), Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }
                                                } catch (JSONException e) {
                                                    throw new RuntimeException(e);
                                                }
                                            }
                                        };
                                        Post.insertPostRequest request = new Post.insertPostRequest(defaultUser.getUsercode(), post, responseListener);
                                        RequestQueue queue = Volley.newRequestQueue(WriteActivity.this);
                                        queue.add(request);
                                    } else {
                                        deleteCacheImg();
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_write_upload_err), Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        };
                        Post.insertPostUrlRequest request = new Post.insertPostUrlRequest(post, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(WriteActivity.this);
                        queue.add(request);

                    } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "파일 로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }
                else {

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 갤러리
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                ContentResolver resolver = getContentResolver();
                try {
                    InputStream instream = resolver.openInputStream(fileUri);
                    Bitmap imgBitmap = BitmapFactory.decodeStream(instream);
                    saveBitmapToPNG(imgBitmap);    // 내부 저장소에 저장
                    buttonAddImage.setImageBitmap(imgBitmap);    // 선택한 이미지 이미지뷰에 셋
                    instream.close();   // 스트림 닫아주기
//                    Toast.makeText(getApplicationContext(), "파일 불러오기 성공", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "파일 불러오기 실패", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void saveBitmapToPNG(Bitmap bitmap) {   // 선택한 이미지 내부 저장소에 저장
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        filename = "Image" + format.format(date) + Locale.getDefault().getCountry() + ".png";

        File tempFile = new File(getCacheDir(), filename);    // 파일 경로와 이름 넣기
        try {
            tempFile.createNewFile();   // 자동으로 빈 파일을 생성하기
            FileOutputStream out = new FileOutputStream(tempFile);  // 파일을 쓸 수 있는 스트림을 준비하기
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);   // compress 함수를 사용해 스트림에 비트맵을 저장하기
            out.close();    // 스트림 닫기
//            Toast.makeText(getApplicationContext(), "파일 저장 성공", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteCacheImg() {    // 이미지 삭제
        try {
            File file = getCacheDir();  // 내부저장소 캐시 경로를 받아오기
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {    // 배열의 크기만큼 반복
//                if (fileList[i].getName().equals(filename)) {   // 삭제하고자 하는 이름과 같은 파일명이 있으면 실행
                    fileList[i].delete();  // 파일 삭제
//                    Toast.makeText(getApplicationContext(), "파일 삭제 성공", Toast.LENGTH_SHORT).show();
//                }
            }
        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "파일 삭제 실패", Toast.LENGTH_SHORT).show();
        }
    }
}