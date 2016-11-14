package com.wigo.android.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.core.server.dto.StatusKind;
import com.wigo.android.core.utils.BitmapUtils;
import com.wigo.android.ui.MainActivity;
import com.wigo.android.ui.elements.LoadMapStatusesTask;

import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
    private MultiAutoCompleteTextView autoCompleteTextView;
    private Button filterButton;
    private List<String> tags;
    private HashMap<UUID, StatusDto> statuses = new HashMap<>();
    private HashMap<String, UUID> markers = new HashMap<>();
    private BitmapDescriptor eventBitmap;
    private BitmapDescriptor chatBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        try {
            view = inflater.inflate(R.layout.map_fragment, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }
        if (view != null) {
            this.view = view;
        }
        initView(this.view);
        return this.view;
    }

    private void initView(View fragmentView) {
        eventBitmap = BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.event, SCALE_FOR_MAP_ITEMS));
        chatBitmap = BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.chat, SCALE_FOR_MAP_ITEMS));
        filterButton = (Button) fragmentView.findViewById(R.id.map_hashtags_filter_button);
        autoCompleteTextView = (MultiAutoCompleteTextView) fragmentView.findViewById(R.id.map_hashtags_text_view);
        autoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        autoCompleteTextView.setThreshold(0);
        autoCompleteTextView.setText(SharedPrefHelper.getTagSearch(""));
        new LoadTags().execute();
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.print("Selectet " + position + " id " + id);
            }
        });
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.showDropDown();
            }
        });
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshMap();
                SharedPrefHelper.setTagSearch(autoCompleteTextView.getText().toString());
            }
        });
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
            onMapCreated(googleMap);
        } else {
            onMapOpened(googleMap);
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
        LoadMapStatusesTask.loadData(this, curScreen, getTagsFromText(autoCompleteTextView.getText().toString()));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        StatusDto status = statuses.get(markers.get(marker.getId()));
        ((MainActivity) getActivity()).openChatFragment(status);
        Toast.makeText(ContextProvider.getAppContext(), marker.getTitle(), Toast.LENGTH_SHORT).show();// display toast
    }

    private void onMapCreated(GoogleMap googleMap) {
        //seting listeners
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnCameraChangeListener(this);

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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
            mMap.setMyLocationEnabled(true);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        CameraUpdate zoom = CameraUpdateFactory.zoomTo(DEFAULT_ZOOM);
        googleMap.moveCamera(zoom);

    }

    private void onMapOpened(GoogleMap googleMap) {

    }

    @Override
    public void loadMapStatusesDone(List<StatusDto> statuses) {
        for (StatusDto status : statuses) {
            if (!this.statuses.keySet().contains(status.getId())) {
                LatLng pos = new LatLng(status.getLatitude(), status.getLongitude());
                MarkerOptions marker = new MarkerOptions().position(pos).title(status.getName());
                if (StatusKind.event.toString().equals(status.getKind())) {
                    marker.icon(eventBitmap);
                } else {
                    marker.icon(chatBitmap);
                }
                StatusDto posTemp = this.statuses.put(status.getId(), status);
                UUID postMarker = this.markers.put(mMap.addMarker(marker).getId(), status.getId());
                if (posTemp != null || postMarker != null) {
                    System.out.print("asd");
                }

            }
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

    private void refreshMap() {
        mMap.clear();
        this.statuses.clear();
        onCameraChange(null);
    }

    public Comparator<StatusDto> comp = new Comparator<StatusDto>() {
        @Override
        public int compare(StatusDto lhs, StatusDto rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    };

    private class LoadTags extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                tags = ContextProvider.getWigoRestClient().getAllHashTags();
            } catch (HttpClientErrorException e) {
                Toast.makeText(ContextProvider.getAppContext(), "Connection error. Try one more time", Toast.LENGTH_SHORT).show();// display toast
                this.cancel(true);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.map_tags_list_item, tags);
            autoCompleteTextView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }

    private List<String> getTagsFromText(String text) {
        List<String> res = new ArrayList<>();
        for (String s : text.split(", ")) {
            if (!s.isEmpty()) {
                res.add(s);
            }
        }
        return res;
    }
}
