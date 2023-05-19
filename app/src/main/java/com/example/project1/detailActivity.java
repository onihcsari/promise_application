package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class detailActivity extends AppCompatActivity {
    private TextView textViewDateTime;
    private TextView textViewTitle;
    private TextView textViewLocation;
    private TextView textViewCategory;
    private TextView textViewNumber;
    private FrameLayout mapViewContainer;
    private MapView mapView;
    String location;
    private Button shareButton, deleteButton;
    public FirebaseAuth mAuth;

    private KakaoMapApi kakaoMapApi;
    private static final String BASE_URL = "https://dapi.kakao.com";
    private static final String KAKAO_API_KEY = "0b289b1ef91f12a6ae8a369ddd779e6a";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mAuth = FirebaseAuth.getInstance();
        String myUID = mAuth.getCurrentUser().getUid();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        kakaoMapApi = retrofit.create(KakaoMapApi.class);

        textViewTitle = findViewById(R.id.textView_title);
        textViewDateTime = findViewById(R.id.textView_datetime);
        textViewLocation = findViewById(R.id.textView_location);
        textViewCategory = findViewById(R.id.textView_category);
        textViewNumber = findViewById(R.id.textView_number);
        shareButton = findViewById(R.id.share_button);
        deleteButton = findViewById(R.id.delete_button);

        // 전달된 약속 데이터 받기
        Intent intent = getIntent();
        DBfile dbfile = (DBfile) intent.getSerializableExtra("DBfile");
        String title = dbfile.getTitle();
        String date = dbfile.getDate();
        String time = dbfile.getTime();
        String dateTime = date + " " + time;
        String relocation = dbfile.getLocation();
        String category = dbfile.getCategory();
        String number = dbfile.getNumber();

        textViewTitle.setText(title);
        textViewDateTime.setText(dateTime);
        textViewLocation.setText(relocation);
        textViewCategory.setText("카테고리: " + category);
        textViewNumber.setText("약속 인원: " + number);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(detailActivity.this, Sharemap.class);
                mapView.setVisibility(View.GONE);
                shareIntent.putExtra("dbfile", dbfile);
                startActivity(shareIntent);
            }
        });

        location = textViewLocation.getText().toString();
        mapViewContainer = findViewById(R.id.map_view_container);
        mapView = new MapView(this);
        mapViewContainer.addView(mapView);

        searchPlace(location);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAppointment(myUID, dbfile.getDate(), dbfile.getTime(), dbfile.getTitle());
                onBackPressed();
            }
        });
    }

    private void searchPlace(String location) {
        // Kakao Maps API를 사용하여 위치 검색
        kakaoMapApi.searchPlace("KakaoAK " + KAKAO_API_KEY, location).enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SearchResult.Document> documents = response.body().documents;
                    if (!documents.isEmpty()) {

                        SearchResult.Document document = documents.get(0);
                        if (document.place_name.equals(location)) {
                            double latitude = document.latitude;
                            double longitude = document.longitude;
                            showLocationOnMap(latitude, longitude);
                        } else {
                            SearchResult.Document document2 = documents.get(1);
                            double latitude = document2.latitude;
                            double longitude = document2.longitude;
                            showLocationOnMap(latitude, longitude);
                        }
                        // 좌표를 이용하여 지도에 마커 표시 등의 작업 수행
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                // 실패 처리
            }
        });
    }

    private void showLocationOnMap(double latitude, double longitude) {
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);

        // 마커 추가
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("검색 결과");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본적인 BluePin 마커 사용
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커 선택 시 RedPin 마커 사용

        mapView.addPOIItem(marker);
    }

    private void deleteAppointment(String uid, String date, String time, String title) {
        DatabaseReference appointmentsRef = FirebaseDatabase.getInstance().getReference("DB").child(uid);

        appointmentsRef.orderByChild("date").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot appointmentSnapshot : dataSnapshot.getChildren()) {
                    String appointmentTime = appointmentSnapshot.child("time").getValue(String.class);
                    String appointmentTitle = appointmentSnapshot.child("title").getValue(String.class);

                    if (appointmentTime != null && appointmentTitle != null &&
                            appointmentTime.equals(time) && appointmentTitle.equals(title)) {
                        appointmentSnapshot.getRef().removeValue();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 삭제 작업이 실패한 경우의 처리를 수행합니다.
                Log.e("DeleteAppointment", "Failed to delete appointment: " + databaseError.getMessage());
            }
        });
    }
}
