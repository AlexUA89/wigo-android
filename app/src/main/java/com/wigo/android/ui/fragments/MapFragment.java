package com.wigo.android.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.ui.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by olkh on 11/13/2015.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnInfoWindowClickListener {

    private View view;
    private Circle circle;

    public static final String FRAGMENT_TAG = "FRAGMENT_MAP";
    private GoogleMap mMap;

    private HashMap<MarkerOptions, UUID> markers = new HashMap<>();
    private HashMap<LatLng, UUID> positions = new HashMap<>();

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
        if(mMap == null) {
            onFragmentCreated(googleMap);
        } else {
            onFragmentOpened(googleMap);
        }
    }

    @Override
    public void onMapClick(LatLng point) {

//        mMap.animateCamera(CameraUpdateFactory.newLatLng(point));

    }

    @Override
    public void onMapLongClick(LatLng point) {

//        if (circle != null) {
//            circle.remove();
//        }
//
//        circle = mMap.addCircle(new CircleOptions().center(point).radius(10000).strokeColor(Color.RED));

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        UUID id = positions.get(marker.getPosition());
        ((MainActivity) getActivity()).openChatFragment(id);
        Toast.makeText(ContextProvider.getAppContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();// display toast
    }

    private void getAllMarkers() {
        for (int i = 0; i < 100; i++) {
            LatLng pos = new LatLng((Math.random() - 0.5) * 90, (Math.random() - 0.5) * 180);
            MarkerOptions marker = new MarkerOptions().position(pos).title("Position - " + i).snippet("Sniped text");
            UUID id = UUID.randomUUID();
            markers.put(marker, id);
            positions.put(pos, id);
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void onFragmentCreated(GoogleMap googleMap){
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        LatLng sydney = new LatLng(50.449362, 30.479365);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setMyLocationEnabled(true);

        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        // Add a marker in Sydney and move the camera
        getAllMarkers();
        for (MarkerOptions marker : markers.keySet()) {
            mMap.addMarker(marker);
        }
    }

    private void onFragmentOpened(GoogleMap googleMap) {

    }

}
