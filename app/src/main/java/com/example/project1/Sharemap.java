package com.example.project1;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Sharemap extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private DatabaseReference databaseReference;
    public FirebaseAuth mAuth;
    private List<String> uidList;
    private String destination;
    private boolean isFirstLocationUpdate = true;

    private KakaoMapApi kakaoMapApi;
    private static final String BASE_URL = "https://dapi.kakao.com";
    private static final String KAKAO_API_KEY = "0b289b1ef91f12a6ae8a369ddd779e6a";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        uidList = new ArrayList<String>();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        kakaoMapApi = retrofit.create(KakaoMapApi.class);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        DBfile dbfile = (DBfile) intent.getSerializableExtra("dbfile");
        uidList = dbfile.getUid();
        destination = dbfile.getLocation();

        mapView = new MapView(this);
        mapView.setMapViewEventListener(new MapView.MapViewEventListener() {
            @Override
            public void onMapViewInitialized(MapView mapView) {
                if (isFirstLocationUpdate) {
                    moveMapToCurrentLocation();
                    isFirstLocationUpdate = false;
                }
            }
            @Override
            public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {
            }
            @Override
            public void onMapViewZoomLevelChanged(MapView mapView, int i) {
            }
            @Override
            public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
            }
            @Override
            public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {
            }
            @Override
            public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {
            }
            @Override
            public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {
            }
            @Override
            public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {
            }
            @Override
            public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {
            }
        });
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);

        ViewGroup mapViewContainer = findViewById(R.id.map_view_container);
        mapViewContainer.addView(mapView);

        // Firebase Realtime Database 설정
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user_locations");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        // 위치 업데이트 콜백 설정
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    saveLocation(location);
                }
            }
        };

        // 위치 업데이트 시작
        startLocationUpdates();
        loadUserLocations(uidList);

        // 위치 권한 확인 및 요청
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 사용자의 위치 정보를 가져옵니다.
            Location userLocation = getUserLocation();

            // 사용자의 위치를 Firebase Realtime Database에 저장합니다.
            saveLocation(userLocation);

            // Firebase Realtime Database에서 다른 사용자의 위치 정보를 가져옵니다.
            loadUserLocations(uidList);
        }

        searchPlace(destination);
    }

    // 사용자의 현재 위치를 Location 객체로 반환하는 메소드입니다.
    private Location getUserLocation() {
        // TODO: 사용자의 현재 위치를 가져오는 코드를 작성하세요.
        // 예를 들어, LocationManager 및 LocationListener를 사용하여 위치 정보를 가져올 수 있습니다.
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // 위치 업데이트 시 호출되는 메소드
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            });

            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            return lastKnownLocation;
        } else {
            // 위치 권한이 없는 경우 처리
            return null;
        }
    }

    // 사용자의 위치를 Firebase Realtime Database에 저장하는 메소드입니다.
    private void saveLocation(Location userLocation) {
        // TODO: 사용자의 위치를 저장하는 코드를 작성하세요.
        if (userLocation != null) {
            double latitude = userLocation.getLatitude();
            double longitude = userLocation.getLongitude();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // 사용자 고유 ID로 변경해야 합니다.

            SerializableLocation locationData = new SerializableLocation(latitude, longitude);

            databaseReference.child(userId).setValue(locationData);
        }
    }

    // Firebase Realtime Database에서 다른 사용자의 위치 정보를 가져오는 메소드입니다.
    private void loadUserLocations(List<String> uidList) {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                String userId = dataSnapshot.getKey();
                if (uidList.contains(userId)) { // 현재 사용자의 UID와 다른 사용자의 UID 비교
                    SerializableLocation location = dataSnapshot.getValue(SerializableLocation.class);
                    if (location != null) {
                        mapView.removeAllPOIItems(); // 기존 마커 제거
                        updateMarker(userId, location.getLatitude(), location.getLongitude());
                        searchPlace(destination);
                    }
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                String userId = dataSnapshot.getKey();
                if (uidList.contains(userId)) {
                    SerializableLocation location = dataSnapshot.getValue(SerializableLocation.class);
                    if (location != null) {
                        mapView.removeAllPOIItems(); // 기존 마커 제거
                        updateMarker(userId, location.getLatitude(), location.getLongitude());
                        searchPlace(destination);
                    }
                }
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String userId = childSnapshot.getKey();
                    double latitude = childSnapshot.child("latitude").getValue(Double.class);
                    double longitude = childSnapshot.child("longitude").getValue(Double.class);

                    // 다른 사용자의 위치를 지도에 표시하는 코드를 작성하세요.
                    // 예를 들어, MapPoint 및 MapPOIItem을 사용하여 지도에 마커를 추가할 수 있습니다.
                    updateMarker(userId, latitude, longitude);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("MapActivity", "Failed to read value.", databaseError.toException());
            }
        });

    }
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        } else {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 위치 권한이 허용되었을 때 처리
                Location userLocation = getUserLocation();
                saveLocation(userLocation);
                loadUserLocations(uidList);
            } else {
                // 위치 권한이 거부되었을 때 처리
            }
        }
    }
    private void updateMarker(String userId, double latitude, double longitude) {
        // 마커를 생성하고 지도에 추가
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName(""); // user_name을 마커 이름으로 설정
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        if (userId.equals(mAuth.getCurrentUser().getUid())) {
            marker.setItemName("나의 위치");
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 내 마커인 경우 파란색
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin);
        } else {
            marker.setItemName("상대의 위치");
            marker.setMarkerType(MapPOIItem.MarkerType.RedPin); // 다른 사용자의 마커인 경우 빨간색
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        }

        mapView.addPOIItem(marker);
    }
    private void moveMapToCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(location.getLatitude(), location.getLongitude()), true);
                    }
                }
            });
        }
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

        // 마커 추가
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("목적지");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.YellowPin); // 기본적인 BluePin 마커 사용
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.YellowPin); // 마커 선택 시 RedPin 마커 사용

        mapView.addPOIItem(marker);
    }
}
