package com.teamhgs.maptrips;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewPostActivity extends AppCompatActivity {

    ImageButton buttonAddImage;
    String filename;
    String imgPath;
    ArrayList<String> url = new ArrayList<>();
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    static StorageReference storageRef = storage.getReference();
    String date;
    Button calenderButton; // 메타데이터에서 날짜 불러올 때 쓸 것 같은 느낌
    Calendar calendar = Calendar.getInstance();
    ExifInterface exifInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        overridePendingTransition(R.anim.none, R.anim.none);

        User currentUser = (User) getIntent().getSerializableExtra(User.CURRENT_USER);

        Post post = new Post();

        buttonAddImage = (ImageButton) findViewById(R.id.Button_addimage);

        Button buttonUpload = (Button) findViewById(R.id.button_upload);

        EditText etTitle = (EditText) findViewById(R.id.et_title);
        EditText etText = (EditText) findViewById(R.id.et_text);

        calenderButton = (Button) findViewById(R.id.button_calander);

        date = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
        String temp = calendar.get(Calendar.YEAR) + getString(R.string.activity_new_post_date_year)
                    + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.activity_new_post_date_month)
                    + calendar.get(Calendar.DATE) + getString(R.string.activity_new_post_date_day);
        calenderButton.setText(temp);

        // 날짜 선택을 위한 달력 UI Dailog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date = year + "-" + (month + 1) + "-" + dayOfMonth;
                try { // 2023-5-1 to 2023-05-01
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    date = format.format(format.parse(date));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                String temp = year + getString(R.string.activity_new_post_date_year)
                            + (month + 1) + getString(R.string.activity_new_post_date_month)
                            + dayOfMonth + getString(R.string.activity_new_post_date_day);
                calenderButton.setText(temp);
                calenderButton.setTextColor(Color.BLACK);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        // Calender Dialog.
        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // Add Image.
        buttonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 201);
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

                    post.setDate(date);
                } catch (Exception e) {

                }

                if (filename != null) {
                    try {
                        imgPath = getCacheDir() + "/" + filename;   // 내부 저장소에 저장되어 있는 이미지 경로
                    } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), "파일 로드 실패", Toast.LENGTH_SHORT).show();
                    }

                    InputStream stream = null;

                    try {
                        stream = new FileInputStream(new File(imgPath));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    StorageReference postsImageRef = storageRef.child("Posts/" + post.getPostcode() + "/" + filename);

                    UploadTask uploadTask = postsImageRef.putStream(stream);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            url.add("");
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            postsImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    url.add(uri.toString());
                                    post.setUrl(url);

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
                                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_new_post_upload_complete), Toast.LENGTH_LONG).show();
                                                                    finish();
                                                                } else {
                                                                    deleteCacheImg();
                                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_new_post_upload_err), Toast.LENGTH_LONG).show();
                                                                    finish();
                                                                }
                                                            } catch (JSONException e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                        }
                                                    };
                                                    Post.insertPostRequest request = new Post.insertPostRequest(currentUser.getUsercode(), post, responseListener);
                                                    RequestQueue queue = Volley.newRequestQueue(NewPostActivity.this);
                                                    queue.add(request);
                                                } else {
                                                    deleteCacheImg();
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_new_post_upload_err), Toast.LENGTH_LONG).show();
                                                    finish();
                                                }
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    };
                                    Post.insertPostUrlRequest request = new Post.insertPostUrlRequest(post, currentUser.getUsercode(), responseListener);
                                    RequestQueue queue = Volley.newRequestQueue(NewPostActivity.this);
                                    queue.add(request);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "ERROR CODE - GET URL Failure", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                } else {

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 갤러리
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 201) {
            if (resultCode == RESULT_OK) {

                Uri fileUri = data.getData();

                // 미디어 액세스 권한이 허용된 경우에만 동작하도록 하였습니다.
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    try { // 이미지 메타데이터를 가져옵니다.
                        exifInterface = new ExifInterface(uriToPath(fileUri));

                        // Image 에 메타데이터가 존재하고 날짜 및 시간 값이 존재합니다.
                        if (exifInterface != null && exifInterface.getAttribute(ExifInterface.TAG_DATETIME) != null) {
                            String exifDate = exifInterface.getAttribute(ExifInterface.TAG_DATETIME).replace(":", "-");
                            date = exifDate.substring(0, 10); // 2023-05-15 22-13-34 to 2023-05-15

                            calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
                            calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(5, 7)) - 1);
                            calendar.set(Calendar.DATE, Integer.parseInt(date.substring(8, 10)));

                            String temp = calendar.get(Calendar.YEAR) + getString(R.string.activity_new_post_date_year)
                                        + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.activity_new_post_date_month)
                                        + calendar.get(Calendar.DATE) + getString(R.string.activity_new_post_date_day);
                            calenderButton.setText(temp);
                            calenderButton.setTextColor(getColor(R.color.app_main_color));

                            TextView calenderText = (TextView) findViewById(R.id.text_calander);
                            calenderText.setText(getString(R.string.activity_new_post_date_metadata));
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                ContentResolver resolver = getContentResolver();

                try {
                    InputStream instream = resolver.openInputStream(fileUri);
                    Bitmap imgBitmap = BitmapFactory.decodeStream(instream);
                    saveBitmapToPNG(imgBitmap);    // 내부 저장소에 저장
                    buttonAddImage.setImageBitmap(imgBitmap);    // 선택한 이미지를 이미지 버튼에 표시합니다.
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

    public String uriToPath(Uri contentUri) {

        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));

        cursor.close();
        return path;
    }
}