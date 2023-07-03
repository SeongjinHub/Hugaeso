package com.teamhgs.maptrips;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceTypes;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
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
    Button calendarButton;
    Button placeButton;
    Button folderButton;
    Calendar calendar = Calendar.getInstance();
    ExifInterface exifInterface;
    String latitude;
    String longitude;
    String placeID;
    String predictedPlaceID;
    Folder folder = null;
    boolean firstFolderBtnClicked = true;
    ArrayList<String> placeIDArrayList = new ArrayList<>();
    ArrayList<Folder> folderArrayList = new ArrayList<>();

    private PlacesClient placesClient;

    public final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Place place = Autocomplete.getPlaceFromIntent(intent);

                        placeID = place.getId();

                        String latLng = String.valueOf(place.getLatLng());
                        latLng = latLng.replaceAll("[lat/lng: ()]", "");
                        String latLngSplit[] = latLng.split(",", 2);

                        latitude = latLngSplit[0];
                        longitude = latLngSplit[1];

                        placeButton.setText(place.getName());
                        placeButton.setTextColor(Color.BLACK);

                        Log.i("TAG", "Place: ${place.getName()}, ${place.getId()}");
                    }
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i("TAG", "User canceled autocomplete");
                }
            });

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

        TextView calenderText = (TextView) findViewById(R.id.text_calendar);
        calendarButton = (Button) findViewById(R.id.button_calander);
        placeButton = (Button) findViewById(R.id.button_place);
        folderButton = (Button) findViewById(R.id.button_folder);

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
                calenderText.setText(getString(R.string.activity_add_post_date_sub));
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

                    // 이미지 미 선택 시 예외 처리
                    if (filename == null) {
                        buttonAddImage.setBackground(getDrawable(R.drawable.btn_add_post_add_img_err));
                    }
                    // 위치 정보 미 입력 시 예외 처리
                    else if (placeID == null) {
                        placeButton.setBackground(getDrawable(R.drawable.btn_add_post_calender_err));
                    }

                    post.setDate(date);
                    post.setPlaceID(placeID);
                    post.setLatitude(latitude);
                    post.setLongitude(longitude);
                } catch (Exception e) {

                }

                if (filename != null && placeID != null) {
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
                            finish();
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

                                    Response.Listener<String> insertFolderPostsResponseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                boolean result = jsonResponse.getBoolean("responseResult");

                                                if (result) {
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_add_post_upload_complete), Toast.LENGTH_LONG).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_add_post_upload_err), Toast.LENGTH_LONG).show();
                                                    finish();
                                                }
                                            } catch (JSONException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    };

                                    Response.Listener<String> insertPostResponseListener = new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonResponse = new JSONObject(response);
                                                boolean result = jsonResponse.getBoolean("responseResult");

                                                if (result) { // 게시글 정보 업로드 성공 시
                                                    if (folder != null) { // 폴더를 선택하였을 경우 폴더에 게시글 추가.
                                                        Folder.insertFolderPostsRequest insertFolderPostsRequest = new Folder.insertFolderPostsRequest(folder, post.getPostcode(), date, insertFolderPostsResponseListener);
                                                        queue.add(insertFolderPostsRequest);
                                                    }
                                                    else {
                                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.activity_add_post_upload_complete), Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }
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

        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
        placesClient = Places.createClient(this);

        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ID, Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.LAT_LNG);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, placeFields)
                .setTypesFilter(Arrays.asList(PlaceTypes.ESTABLISHMENT)) // 비즈니스 정보가 등록된 장소로 검색범위를 제한
                .build(this);

        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeButton.setBackground(getDrawable(R.drawable.btn_add_post_calender));
                startAutocomplete.launch(intent);
            }
        });

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View folderDialogView = layoutInflater.inflate(R.layout.activity_add_post_folder, null, false);
        Dialog folderDialog = new Dialog(this, R.style.FullScreenDialogTheme);
        folderDialog.setContentView(folderDialogView);

        ListView listViewFolder = (ListView) folderDialogView.findViewById(R.id.listview_folder);
        ListViewAdapter listViewAdapter = new ListViewAdapter();
        listViewFolder.setAdapter(listViewAdapter);

        listViewFolder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                folder = item.getFolder();
                folderButton.setText(item.getFolder().getTitle());
                folderButton.setTextColor(getColor(R.color.black));

                folderDialog.dismiss();
            }
        });

        Response.Listener<String> getFolderResponseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonResponse = new JSONArray(response);
                    JSONObject jsonObject;

                    if (jsonResponse.length() > 0) {
                        for (int i = 0; i < jsonResponse.length(); i++) {

                            jsonObject = jsonResponse.getJSONObject(i);
                            String foldercode = jsonObject.getString("foldercode");

                            Folder tempFolder = new Folder(foldercode);

                            tempFolder.setTitle(jsonObject.getString("title"));
                            tempFolder.setText(jsonObject.getString("text"));
                            tempFolder.setStartDate(jsonObject.getString("startdate"));
                            tempFolder.setEndDate(jsonObject.getString("enddate"));
                            tempFolder.setUpdateDate(jsonObject.getString("updatedate"));
                            tempFolder.setPrivateStatus(jsonObject.getInt("private"));

                            listViewAdapter.addItem(tempFolder);

                            folderArrayList.add(tempFolder);
                        }
                        folderDialog.show();
                    } else {
                        folderDialog.show();
                        // 생성한 폴더가 없음을 알리는 코드
                    }
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
            }
        };

        folderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstFolderBtnClicked) {
                    Folder.getFolderRequest request = new Folder.getFolderRequest(currentUser.getUsercode(), getFolderResponseListener);
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(request);
                    firstFolderBtnClicked = false;
                }
                else {
                    folderDialog.show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 갤러리
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 201) {
            if (resultCode == RESULT_OK) {

                { // 새 이미지를 불러올 시 기존에 입력한 날짜 및 장소를 초기화
                    placeID = null;

                    date = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
                    String temp = calendar.get(Calendar.YEAR) + getString(R.string.activity_add_post_date_year)
                            + (calendar.get(Calendar.MONTH) + 1) + getString(R.string.activity_add_post_date_month)
                            + calendar.get(Calendar.DATE) + getString(R.string.activity_add_post_date_day);
                    calendarButton.setText(temp);
                    calendarButton.setTextColor(getColor(R.color.default_text_color));
                    calendarButton.setBackground(getDrawable(R.drawable.btn_add_post_calender));

                    TextView calenderText = (TextView) findViewById(R.id.text_calendar);
                    calenderText.setText(getString(R.string.activity_add_post_date_sub));

                    placeButton.setText(getString(R.string.activity_add_post_place_btn));
                    placeButton.setTextColor(getColor(R.color.default_text_color));

                    TextView placeText = (TextView) findViewById(R.id.text_place);
                    placeText.setText(getString(R.string.activity_add_post_place_sub));
                }

                Uri fileUri = data.getData();

                // 미디어 액세스 권한이 허용된 경우에만 동작하도록 하였습니다.
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    try { // 이미지 메타데이터를 가져옵니다.
                        exifInterface = new ExifInterface(uriToPath(fileUri));

                        // Image 에 메타데이터가 존재하고 날짜 및 시간 값이 존재합니다.
                        if (exifInterface != null && exifInterface.getAttribute(ExifInterface.TAG_DATETIME) != null) {
                            String exifDate = exifInterface.getAttribute(ExifInterface.TAG_DATETIME).replace(":", "-");

                            date = exifDate.substring(0, 10); // 2023-05-15 22-13-34 to 2023-05-15

                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                            Date formattedDate = null;

                            try {
                                formattedDate = simpleDateFormat.parse(date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            Calendar exifCalendar = Calendar.getInstance();

                            exifCalendar.setTime(formattedDate);

                            String temp = exifCalendar.get(Calendar.YEAR) + getString(R.string.activity_add_post_date_year)
                                    + (exifCalendar.get(Calendar.MONTH) + 1) + getString(R.string.activity_add_post_date_month)
                                    + exifCalendar.get(Calendar.DATE) + getString(R.string.activity_add_post_date_day);
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
                            } else {
                                latitude = String.valueOf(0 - convDMStoDegree(imgAttrLat));
                            }
                            if (imgAttrLongRef.equals("E")) {
                                longitude = String.valueOf(convDMStoDegree(imgAttrLong));
                            } else {
                                longitude = String.valueOf(convDMStoDegree(imgAttrLong));
                            }

                            // 이미지 메타데이터 내 좌표를 기반으로 추천 장소 찾기
                            Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);
                            placesClient = Places.createClient(this);

                            // 추천 장소 버튼을 포함할 레이아웃 및 레이아웃 초기화
                            LinearLayout placePredictionLayout = findViewById(R.id.layout_place_prediction);
                            placePredictionLayout.removeAllViews();

                            Display display = getWindowManager().getDefaultDisplay();
                            DisplayMetrics outMetrics = new DisplayMetrics();
                            display.getMetrics(outMetrics);

                            float density = getResources().getDisplayMetrics().density;

                            // Geocoding API를 통해 Latlng을 주소로 변환합니다.
                            final Geocoder geocoder = new Geocoder(this);

                            placeIDArrayList.clear();
                            List<Address> list = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble(longitude), 5);

                            if (list != null) {
                                if (list.size() > 0) {
                                    for (int i = 0; i < list.size(); i++) { // 검색된 주소 수 만큼 장소 검색을 반복합니다.
                                        String query = list.get(i).getAddressLine(0);

                                        // Use the builder to create a FindAutocompletePredictionsRequest.
                                        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                                                // Call either setLocationBias() OR setLocationRestriction().
                                                // .setLocationBias(bounds)
                                                // .setLocationRestriction(bounds)
                                                .setOrigin(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)))
                                                // .setTypesFilter(Arrays.asList(PlaceTypes.ADDRESS))
                                                .setTypesFilter(Arrays.asList(PlaceTypes.ESTABLISHMENT)) // 비즈니스 정보가 등록된 장소로 검색 범위를 제한
                                                // .setSessionToken(token)
                                                .setQuery(query)
                                                .build();

                                        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                                            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                                                predictedPlaceID = null;

                                                // 주소를 기반으로 검색한 장소와 이미지의 좌표사이의 거리를 계산합니다.
                                                int distance = prediction.getDistanceMeters();

                                                if (distance < 2500) { // 거리가 2500M 이내 인 장소만 사용합니다.
                                                    predictedPlaceID = prediction.getPlaceId();

                                                    boolean placeIDdup = false;

                                                    // 주소를 기반으로 장소를 반복하여 검색할 때 이미 검색한 장소가 있으면 건너뛰도록 하였습니다.
                                                    for (int j = 0; j < placeIDArrayList.size(); j++) {
                                                        if (placeIDArrayList.get(j).equals(predictedPlaceID)) {
                                                            placeIDdup = true;
                                                            break;
                                                        } else {
                                                            placeIDdup = false;
                                                        }
                                                    }
                                                    if (placeIDdup) {
                                                        predictedPlaceID = null;
                                                    } else {
                                                        placeIDArrayList.add(predictedPlaceID);
                                                    }
                                                }

                                                List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ID, Place.Field.LAT_LNG);

                                                if (predictedPlaceID != null) {
                                                    final FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.newInstance(predictedPlaceID, placeFields);

                                                    placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener((getPlaceNameResponse) -> {
                                                        Place place = getPlaceNameResponse.getPlace();

                                                        // 검색한 장소를 화면에 출력할 버튼을 생성합니다.
                                                        Button predictedPlaceButton = new Button(getApplicationContext());

                                                        // 생성한 버튼의 UI를 설정합니다.
                                                        LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                                        buttonLayoutParams.setMargins(0, (int) (5 * density + 0.5), (int) (5 * density + 0.5), (int) (5 * density + 0.5));
                                                        predictedPlaceButton.setLayoutParams(buttonLayoutParams);
                                                        predictedPlaceButton.setPadding((int) (5 * density + 0.5), 0, (int) (5 * density + 0.5), 0);
                                                        predictedPlaceButton.setBackground(getDrawable(R.drawable.btn_add_post_predicted_place));

                                                        predictedPlaceButton.setText(place.getName());

                                                        // 버튼에 Tag를 설정하여 필요한 정보를 전달합니다.
                                                        String tag = place.getLatLng() + "%" + place.getName() + "%" + place.getId();

                                                        predictedPlaceButton.setTag(tag);

                                                        predictedPlaceButton.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {

                                                                // Tag에서 정보를 불러온 후 적절히 분할하여 사용합니다.
                                                                String tag = String.valueOf(v.getTag());
                                                                String tagSplit[] = tag.split("%");

                                                                placeID = tagSplit[2];

                                                                String latLng = tagSplit[0];
                                                                latLng = latLng.replaceAll("[lat/lng: ()]", "");
                                                                String latLngSplit[] = latLng.split(",", 2);

                                                                latitude = latLngSplit[0];
                                                                longitude = latLngSplit[1];

                                                                placeButton.setText(tagSplit[1]);
                                                                placeButton.setTextColor(getColor(R.color.app_main_color));

//                                                                TextView placeText = (TextView) findViewById(R.id.text_place);
//                                                                placeText.setText(getString(R.string.activity_add_post_place_metadata));
                                                            }
                                                        });

                                                        placePredictionLayout.addView(predictedPlaceButton);

                                                    }).addOnFailureListener((exception) -> {
                                                        if (exception instanceof ApiException) {
                                                            final ApiException apiException = (ApiException) exception;
                                                            final int statusCode = apiException.getStatusCode();
                                                            // TODO: Handle error with given status code.
                                                        }
                                                    });
                                                }

                                                Log.i("TAG", prediction.getPlaceId());
                                                Log.i("TAG", prediction.getPrimaryText(null).toString());
                                            }
                                        }).addOnFailureListener((exception) -> {
                                            if (exception instanceof ApiException) {
                                                ApiException apiException = (ApiException) exception;
                                                Log.e("TAG", "Place not found: " + apiException.getStatusCode());
                                            }
                                        });
                                    }
                                } else {
                                    Log.i("TAG", "No result of reverse-geocoding.");
                                }
                            }
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
    public double convDMStoDegree(String stringDMS) {
        double result = 0;
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

        result = (doubleD + (doubleM / 60) + (doubleS / 3600));

        return result;

    }

    // 리스트 뷰에 표시 할 객체
    public class ListViewItem {
        private ImageView imageView;
        private Folder folder;

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(String url) {
//            Glide로 이미지 삽입하는 코드
        }

        public Folder getFolder() {
            return folder;
        }

        public void setFolder(Folder folder) {
            this.folder = folder;
        }
    }

    // 리스트 뷰에 객체를 추가하여 화면에 출력
    public class ListViewAdapter extends BaseAdapter {

        private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

        @Override
        public int getCount() {
            return listViewItemList.size();
        }

        @Override
        public Object getItem(int position) {
            return listViewItemList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            // activity_add_post_folder_listitem을 inflate하여 레이아웃을 View로 만들고 convertView 참조 획득
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.activity_add_post_folder_listitem, parent, false);
            }

            // 화면에 표시될 View 객체 불러오기
            ImageView ImageView = (ImageView) convertView.findViewById(R.id.imageView_listitem);
            TextView titleTextView = (TextView) convertView.findViewById(R.id.text_listitem_title);
            TextView subTextView = (TextView) convertView.findViewById(R.id.text_listitem_sub);

            // Data Set (listViewItemList) 에서 position에 위치한 데이터참조 획득
            ListViewItem listViewItem = listViewItemList.get(position);

            // 각 객체에 값 입력
//            iconImageView 에 Glide() 로 사진 입력 코드
            titleTextView.setText(listViewItem.getFolder().getTitle());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // 폴더의 시작 날짜와 끝 날짜를 가져옵니다.
            Date startDate = null;
            Date endDate = null;

            try {
                startDate = simpleDateFormat.parse(listViewItem.getFolder().getStartDate());
                endDate = simpleDateFormat.parse(listViewItem.getFolder().getEndDate());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            Calendar calendarStart = Calendar.getInstance();
            Calendar calendarEnd = Calendar.getInstance();

            calendarStart.setTime(startDate);
            calendarEnd.setTime(endDate);

            String startDateText = calendarStart.get(Calendar.YEAR) + getString(R.string.activity_add_post_date_year)
                    + (calendarStart.get(Calendar.MONTH) + 1) + getString(R.string.activity_add_post_date_month)
                    + calendarStart.get(Calendar.DATE) + getString(R.string.activity_add_post_date_day);

            String endDateText = calendarEnd.get(Calendar.YEAR) + getString(R.string.activity_add_post_date_year)
                    + (calendarEnd.get(Calendar.MONTH) + 1) + getString(R.string.activity_add_post_date_month)
                    + calendarEnd.get(Calendar.DATE) + getString(R.string.activity_add_post_date_day);

            // 가져온 결과를 화면에 출력합니다.
            String textSub = startDateText + "  ~  " + endDateText + " ";

            subTextView.setText(textSub);

            return convertView;
        }

        // item에 데이터 추가 - 추후 이미지 표시 기능 사용할 때 사용
        public void addItem(String url, Folder folder) {
            ListViewItem item = new ListViewItem();

            item.setImageView(url);
            item.setFolder(folder);

            listViewItemList.add(item);
        }

        public void addItem(Folder folder) {
            ListViewItem item = new ListViewItem();

            item.setFolder(folder);

            listViewItemList.add(item);
        }
    }
}
