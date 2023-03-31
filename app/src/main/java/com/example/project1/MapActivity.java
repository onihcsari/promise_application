package com.example.project1;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MapActivity extends Activity implements MapView.MapViewEventListener{

    public MapView mapView;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED || hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
        }

        mapView = new MapView(this);
        RelativeLayout container = findViewById(R.id.map_view);
        container.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.5665, 126.9780), true);
        mapView.setZoomLevel(7, true);

    }

    private void requestLocationPermissions() {
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE && grantResults.length == REQUIRED_PERMISSIONS.length) {
            boolean granted = true;

            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }

            if (!granted) {
                // 위치 권한 거부에 대한 처리
                Toast.makeText(this, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show();

                // 앱 종료
                finish();
            }
        }
    }
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
        double latitude = mapPoint.getMapPointGeoCoord().latitude;
        double longitude = mapPoint.getMapPointGeoCoord().longitude;

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("원하는 장소");
        marker.setTag(0);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.addPOIItem(marker);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);
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
}


