package com.wigo.android.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.core.utils.BitmapUtils;
import com.wigo.android.ui.MainActivity;
import com.wigo.android.ui.elements.LoadMapStatusesTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by olkh on 11/13/2015.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnCameraChangeListener, LoadMapStatusesTask.LoadMapStatusesTaskListener {

    public static final String FRAGMENT_TAG = "FRAGMENT_MAP";
    private static final LatLng KIEV = new LatLng(50.449362, 30.479365);
    private static final float DEFAULT_ZOOM = 14;
    private static final float SCALE_FOR_MAP_ITEMS = 2f;

    private View view;
    private GoogleMap mMap;

    private HashMap<LatLng, StatusDto> statuses = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            view = inflater.inflate(R.layout.map_fragment, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) {
            onFragmentCreated(googleMap);
        } else {
            onFragmentOpened(googleMap);
        }
    }

    @Override
    public void onMapClick(LatLng point) {
    }

    @Override
    public void onMapLongClick(LatLng point) {
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        //request for all markers
        LatLngBounds curScreen = mMap.getProjection()
                .getVisibleRegion().latLngBounds;
        LoadMapStatusesTask.loadData(this, curScreen);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        StatusDto status = statuses.get(marker.getPosition());
        ((MainActivity) getActivity()).openChatFragment(status);
        Toast.makeText(ContextProvider.getAppContext(), marker.getTitle() + " " + status.getId(), Toast.LENGTH_SHORT).show();// display toast
    }

    private void onFragmentCreated(GoogleMap googleMap) {
        //seting listeners
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnCameraChangeListener(this);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //define my last location
        LocationManager mLocationManager = (LocationManager) ContextProvider.getAppContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = mLocationManager.getBestProvider(criteria, true);
        LatLng location;
        if (ActivityCompat.checkSelfPermission(ContextProvider.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(ContextProvider.getAppContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            location = KIEV;
        } else {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                location = KIEV;
            } else {
                location = new LatLng(l.getLatitude(), l.getLongitude());
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(DEFAULT_ZOOM);
        googleMap.moveCamera(zoom);

        mMap.setMyLocationEnabled(true);
    }

    private void onFragmentOpened(GoogleMap googleMap) {

    }

    @Override
    public void loadMapStatusesDone(List<StatusDto> statuses) {
        List<StatusDto> newStatuses = new ArrayList<>();
        List<MarkerOptions> newMarkers = new ArrayList<>();
        for (StatusDto status : statuses) {
            if (!alreadyHaveThisStatus(status)) newStatuses.add(status);
        }
        for (StatusDto status : newStatuses) {
            LatLng pos = new LatLng(status.getLatitude(), status.getLongitude());
            newMarkers.add(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.event, SCALE_FOR_MAP_ITEMS))).title(status.getName()));
            this.statuses.put(pos, status);
        }
        for (MarkerOptions marker : newMarkers) {
            mMap.addMarker(marker);
        }
    }

    @Override
    public void loadMapStateseTimeoutError(LatLngBounds curScreen) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ContextProvider.getAppContext(), "Connection timeout. Try one more time", Toast.LENGTH_SHORT).show();// display toast
            }
        });
    }

    @Override
    public void loadMapStateseConnectionError(LatLngBounds curScreen) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ContextProvider.getAppContext(), "Connection error. Try one more time", Toast.LENGTH_SHORT).show();// display toast
            }
        });
    }

    private boolean alreadyHaveThisStatus(StatusDto newStatus) {
        for (StatusDto status : statuses.values()) {
            if (status.getId().equals(newStatus.getId())) {
                return true;
            }
        }
        return false;
    }

    private void refreshMap() {
        mMap.clear();
        this.statuses.clear();
        onCameraChange(null);
    }
}
