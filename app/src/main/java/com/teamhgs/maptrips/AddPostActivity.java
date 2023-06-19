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
import androidx.exifinterface.media.ExifInterface;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddPostActivity extends AppCompatActivity {

    ImageButton buttonAddImage;
    String filename;
    String imgPath;
    ArrayList<String> url = new ArrayList<>();
    static FirebaseStorage storage = FirebaseStorage.getInstance();
    static StorageReference storageRef = storage.getReference();
    String date;
    Button calendarButton; // 메타데이터에서 날짜 불러올 때 쓸 것 같은 느낌
    Button placeButton;
    Calendar calendar = Calendar.getInstance();
    ExifInterface exifInterface;
    String latitude;
    String longitude;

    private PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        overridePendingTransition(R.anim.none, R.anim.none);

        User currentUser = (User) getIntent().getSerializableExtra(User.CURRENT_USER);

        Post post = new Post();

        buttonAddImage = (ImageButton) findViewById(R.id.Button_addimage);

        Button buttonUpload = (Button) findViewById(R.id.button_upload);

        EditText etTitle = (EditText) findViewById(R.id.et_title);
        EditText etText = (EditText) findViewById(R.id.et_text);

        calendarButton = (Button) findViewById(R.id.button_calander);
        placeButton = (Button) findViewById(R.id.button_place);

        date = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
        String temp = calendar.get(Calendar.YEAR) + getString(R.string.activity_add_post_date_year)
                    + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.activity_add_post_date_month)
                    + calendar.get(Calendar.DATE) + getString(R.string.activity_add_post_date_day);
        calendarButton.setText(temp);

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
                String temp = year + getString(R.string.activity_add_post_date_year)
                            + (month + 1) + getString(R.string.activity_add_post_date_month)
                            + dayOfMonth + getString(R.string.activity_add_post_date_day);
                calendarButton.setText(temp);
                calendarButton.setTextColor(Color.BLACK);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));

        // Calender Dialog.
        calendarButton.setOnClickListener(new View.OnClickListener() {
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


                    // 위치 정보 미입력시 예외처리, 추후 구현 필요.
                    if (latitude == null || longitude == null) {
                        latitude = "0";
                        longitude = "0";
                    }

                    post.setDate(date);
                    post.setLatitude(latitude);
                    post.setLongitude(longitude);
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

                                    RequestQueue queue = Volley.newRequestQueue(AddPostActivity.this);

                                    Response.Listener<String> insertPostResponseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                boolean result = jsonResponse.getBoolean("responseResult");

                                                if (result) {
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_add_post_upload_complete), Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    deleteCacheImg();
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_add_post_upload_err), Toast.LENGTH_LONG).show();
                                                    finish();
                                                }
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    };

                                    Response.Listener<String> insertPostUrlResponseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                boolean result = jsonResponse.getBoolean("responseResult");

                                                if (result) {
                                                    // URL 업로드 완료 시 게시글 업로드
                                                    Post.insertPostRequest request = new Post.insertPostRequest(currentUser.getUsercode(), post, insertPostResponseListener);
                                                    queue.add(request);
                                                } else {
                                                    deleteCacheImg();
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_add_post_upload_err), Toast.LENGTH_LONG).show();
                                                    finish();
                                                }
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    };
                                    Post.insertPostUrlRequest insertPostUrlRequest = new Post.insertPostUrlRequest(post, currentUser.getUsercode(), insertPostUrlResponseListener);
                                    queue.add(insertPostUrlRequest);
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

                            String temp = calendar.get(Calendar.YEAR) + getString(R.string.activity_add_post_date_year)
                                        + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.activity_add_post_date_month)
                                        + calendar.get(Calendar.DATE) + getString(R.string.activity_add_post_date_day);
                            calendarButton.setText(temp);
                            calendarButton.setTextColor(getColor(R.color.app_main_color));

                            TextView calenderText = (TextView) findViewById(R.id.text_calendar);
                            calenderText.setText(getString(R.string.activity_add_post_date_metadata));
                        }

                        // 이미지 메타데이터에서 위치 정보에 대한 값을 가져옵니다.
                        if (exifInterface != null && exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE) != null) {
                            String imgAttrLat = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                            String imgAttrLatRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);

                            String imgAttrLong = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                            String imgAttrLongRef = exifInterface.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

                            // DMS 형태의 값을 Degree로 변환 후 계산합니다.
                            if (imgAttrLatRef.equals("N")) {
                                latitude = String.valueOf(convDMStoDegree(imgAttrLat));
                            }
                            else {
                                latitude = String.valueOf(0 - convDMStoDegree(imgAttrLat));
                            }
                            if (imgAttrLongRef.equals("E")) {
                                longitude = String.valueOf(convDMStoDegree(imgAttrLong));
                            }
                            else {
                                longitude = String.valueOf(convDMStoDegree(imgAttrLong));
                            }
                            // 좌표를 기반으로 장소 찾기
                            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
                            placesClient = Places.createClient(this);

                            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

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

    // DMS를 Degree 형태로 변환합니다.
    public float convDMStoDegree(String stringDMS) {
        float result = 0;
        String[] DMS = stringDMS.split(",", 3);

        String[] stringD = DMS[0].split("/", 2);
        double D0 = Double.parseDouble(stringD[0]);
        double D1 = Double.parseDouble(stringD[1]);
        double doubleD = D0 / D1;

        String[] stringM = DMS[1].split("/", 2);
        double M0 = Double.parseDouble(stringM[0]);
        double M1 = Double.parseDouble(stringM[1]);
        double doubleM = M0 / M1;

        String[] stringS = DMS[2].split("/", 2);
        double S0 = Double.parseDouble(stringS[0]);
        double S1 = Double.parseDouble(stringS[1]);
        double doubleS = S0 / S1;

        result = (float) (doubleD + (doubleM / 60) + (doubleS / 3600));

        return result;

    };
}