package com.example.project1;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener{
    private static final String BASE_URL = "https://dapi.kakao.com";
    private static final String KAKAO_API_KEY = "0b289b1ef91f12a6ae8a369ddd779e6a";
    private boolean hasMovedToInitialLocation = false;

    private MapView mapView;
    private EditText searchEditText;
    private KakaoMapApi kakaoMapApi;
    private Button searchButton;
    private RecyclerView recyclerView;
    private PlaceAdapter placeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapView = new MapView(this);
        // mapView.setCurrentLocationEventListener(this);
        mapView.setShowCurrentLocationMarker(true);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        mapView.setMapViewEventListener(this);

        ViewGroup mapViewContainer = findViewById(R.id.map_view_container);
        mapViewContainer.addView(mapView);

        searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // searchPlace(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        kakaoMapApi = retrofit.create(KakaoMapApi.class);

        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPlace(searchEditText.getText().toString());
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        placeAdapter = new PlaceAdapter(new ArrayList<>());
        recyclerView.setAdapter(placeAdapter);

        placeAdapter.setOnItemClickListener(new PlaceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Place place) {
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(place.getLatitude(), place.getLongitude());
                mapView.setMapCenterPoint(mapPoint, true);
            }
        });

    }

    private void searchPlace(String query) {
        Call<SearchResult> call = kakaoMapApi.searchPlace("KakaoAK " + KAKAO_API_KEY, query);
        call.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Place> places = new ArrayList<>();
                    for (SearchResult.Document document : response.body().documents) {
                        if (places.size() >= 4) {
                            break;
                        }
                        places.add(new Place(document.place_name, document.latitude, document.longitude));
                    }
                    placeAdapter.places = places;
                    placeAdapter.notifyDataSetChanged();
                    findViewById(R.id.recycler_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.map_overlay).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                // 실패 처리
            }
        });
    }
    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float v) {
        if (!hasMovedToInitialLocation) {
            mapView.setMapCenterPoint(currentLocation, true);
            hasMovedToInitialLocation = true;
        }    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }
    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }
    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

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
        hideSearchResultList();
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

    private void hideSearchResultList() {
        findViewById(R.id.recycler_view).setVisibility(View.GONE);
        findViewById(R.id.map_overlay).setVisibility(View.GONE);
    }
}


