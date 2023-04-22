package com.example.project1;

import android.content.Context;
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

import java.util.HashMap;
import java.util.Map;

public class Sharemap extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private DatabaseReference databaseReference;
    public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getCurrentUser().getUid();

        mapView = new MapView(this);
        mapView.setMapViewEventListener(new MapView.MapViewEventListener() {
            @Override
            public void onMapViewInitialized(MapView mapView) {
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
        loadOtherUserLocations();

        // 위치 권한 확인 및 요청
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 사용자의 위치 정보를 가져옵니다.
            Location userLocation = getUserLocation();

            // 사용자의 위치를 Firebase Realtime Database에 저장합니다.
            saveLocation(userLocation);

            // Firebase Realtime Database에서 다른 사용자의 위치 정보를 가져옵니다.
            loadOtherUserLocations();
        }
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

//            Map<String, Object> locationData = new HashMap<>();
//            locationData.put("latitude", latitude);
//            locationData.put("longitude", longitude);
            SerializableLocation locationData = new SerializableLocation(latitude, longitude);

            databaseReference.child(userId).setValue(locationData);
        }
    }

    // Firebase Realtime Database에서 다른 사용자의 위치 정보를 가져오는 메소드입니다.
    private void loadOtherUserLocations() {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                SerializableLocation location = dataSnapshot.getValue(SerializableLocation.class);
                if (location != null) {
                    mapView.removeAllPOIItems(); // 기존 마커 제거
                    updateMarker(dataSnapshot.getKey(), location.getLatitude(), location.getLongitude());
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                SerializableLocation location = dataSnapshot.getValue(SerializableLocation.class);
                if (location != null) {
                    mapView.removeAllPOIItems(); // 기존 마커 제거
                    updateMarker(dataSnapshot.getKey(), location.getLatitude(), location.getLongitude());
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
                loadOtherUserLocations();
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
            marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 내 마커인 경우 파란색
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.BluePin);
        } else {
            marker.setMarkerType(MapPOIItem.MarkerType.RedPin); // 다른 사용자의 마커인 경우 빨간색
            marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        }

        mapView.addPOIItem(marker);
    }

//    private void loadOtherUsersLocations() {
//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Location location = dataSnapshot.getValue(Location.class);
//                if (location != null) {
//                    updateMarker(dataSnapshot.getKey(), location.getLatitude(), location.getLongitude());
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                Location location = dataSnapshot.getValue(Location.class);
//                if (location != null) {
//                    updateMarker(dataSnapshot.getKey(), location.getLatitude(), location.getLongitude());
//                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });
//    }

}
