package com.wigo.android.ui.fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.database.DBManager;
import com.wigo.android.core.database.Database;
import com.wigo.android.core.database.datas.Status;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.core.server.dto.StatusSmallDto;
import com.wigo.android.core.server.requestapi.errors.WigoException;
import com.wigo.android.ui.MainActivity;
import com.wigo.android.ui.activities.CategoryActivity;
import com.wigo.android.ui.activities.CreateStatusActivity;
import com.wigo.android.ui.activities.StatusListActivity;
import com.wigo.android.ui.elements.CategoriesProvider;
import com.wigo.android.ui.elements.LoadMapStatusesTask;
import com.wigo.android.ui.elements.WigoClusterManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by olkh on 11/13/2015.
 */
public class MapFragment extends Fragment implements ClusterManager.OnClusterClickListener<StatusSmallDto>, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, LoadMapStatusesTask.LoadMapStatusesTaskListener, GoogleMap.OnCameraIdleListener {

    public static final String FRAGMENT_TAG = "FRAGMENT_MAP";
    private static final LatLng KIEV = new LatLng(50.449362, 30.479365);
    private static final float DEFAULT_ZOOM = 14;
    private static final int PICK_CATEGORIES = 1;
    private static final int CREATE_STATUS = 2;
    private static final int PICK_STATUSES = 3;

    private View view;
    private GoogleMap mMap;
    private EditText textSearch;
    private ImageButton categoryButton, searchButton;
    private Calendar fromDate, toDate;
    private Button fromDateButton;
    private Button toDateButton;
    private WigoClusterManager mClusterManager;

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

        categoryButton = (ImageButton) fragmentView.findViewById(R.id.category_button);
        searchButton = (ImageButton) fragmentView.findViewById(R.id.map_search_button);
        fragmentView.findViewById(R.id.map_plus_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ContextProvider.getAppContext(), "Please, long click on map to create new chat.", Toast.LENGTH_LONG).show();// display toast
            }
        });
        fromDateButton = (Button) fragmentView.findViewById(R.id.from_date_button);
        toDateButton = (Button) fragmentView.findViewById(R.id.to_date_button);
        textSearch = (EditText) fragmentView.findViewById(R.id.text_search_field);
        categoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContactIntent = new Intent(ContextProvider.getAppContext(), CategoryActivity.class);
                startActivityForResult(pickContactIntent, PICK_CATEGORIES);
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshMap();
            }
        });
        fromDate = SharedPrefHelper.getFromDateSearch(null);
        Calendar c = Calendar.getInstance();
        if (fromDate == null) {
            fromDate = Calendar.getInstance();
            fromDate.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        }
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        c.add(Calendar.DATE, 1);
        toDate = SharedPrefHelper.getToDateSearch(c);
        redrawDateButtons();

        fromDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        fromDate.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                        SharedPrefHelper.setFromDateSearch(fromDate);
                        redrawDateButtons();
                        refreshMap();
                    }
                }, fromDate.get(Calendar.YEAR), fromDate.get(Calendar.MONTH), fromDate.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(toDate.getTimeInMillis());
                dialog.show();
            }
        });

        toDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        toDate.set(year, monthOfYear, dayOfMonth, 23, 59, 59);
                        SharedPrefHelper.setToDateSearch(toDate);
                        redrawDateButtons();
                        refreshMap();
                    }
                }, toDate.get(Calendar.YEAR), toDate.get(Calendar.MONTH), toDate.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(fromDate.getTimeInMillis());
                dialog.show();
            }
        });
    }

    private void redrawDateButtons() {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        fromDateButton.setText(format.format(fromDate.getTime()));
        toDateButton.setText(format.format(toDate.getTime()));
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
        Intent createStatus = new Intent(ContextProvider.getAppContext(), CreateStatusActivity.class);
        createStatus.putExtra(CreateStatusActivity.POINT, point);
        startActivityForResult(createStatus, CREATE_STATUS);
    }

    @Override
    public void onCameraIdle() {
        mClusterManager.onCameraIdle();
        //request for all markers
        LatLngBounds curScreen = mMap.getProjection()
                .getVisibleRegion().latLngBounds;
        LoadMapStatusesTask.loadData(this, curScreen, Collections.EMPTY_LIST, CategoriesProvider.getChoosenCategories(), fromDate, toDate, textSearch.getText().toString());
    }

    private void onMapCreated(GoogleMap googleMap) {
        //seting listeners
        mMap = googleMap;
        mClusterManager = new WigoClusterManager(this.getActivity(), mMap);
        mClusterManager.setOnClusterClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnCameraIdleListener(this);
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
    public void loadMapStatusesDone(List<StatusSmallDto> statuses) {
        mClusterManager.addStatuses(statuses);
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
    public void loadMapStateseConnectionError(LatLngBounds curScreen, final WigoException e) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ContextProvider.getAppContext(), "Connection error: " + e.getMessage() + ". Try one more time", Toast.LENGTH_SHORT).show();// display toast
            }
        });
    }

    private void refreshMap() {
        mClusterManager.removeAll();
        onCameraIdle();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CATEGORIES && resultCode == getActivity().RESULT_OK) {
            refreshMap();
        }
        if (requestCode == PICK_STATUSES && resultCode == getActivity().RESULT_OK) {
            StatusSmallDto status = (StatusSmallDto) data.getExtras().get(StatusListActivity.CHOOSED_STATUS);
            ((MainActivity) getActivity()).openChatFragment(status.getId());
        }
        if (requestCode == CREATE_STATUS && resultCode == getActivity().RESULT_OK) {
            try {
                StatusDto status = ContextProvider.getObjectMapper().readValue(data.getStringExtra(CreateStatusActivity.CREATED_STATUS), StatusDto.class);
                Database db = DBManager.getDatabase();
                db.open();
                Status statusDb = new Status(status.getId(), status.getUserId(), status.getLatitude(), status.getLongitude(), status.getName(), status.getText(), status.getUrl(),
                        status.getStartDate(), status.getEndDate(), status.getKind(), status.getCategory(), status.getHashtags(), status.getImages(), new Date());
                statusDb.setLocalId(db.insertNewDBStorable(statusDb));
                db.close();
                ((MainActivity) getActivity()).updateMenuList();
                ArrayList<StatusSmallDto> newStatus = new ArrayList<>();
                newStatus.add(status);
                loadMapStatusesDone(newStatus);
                ((MainActivity) getActivity()).openChatFragment(status.getId());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onClusterClick(Cluster<StatusSmallDto> cluster) {
        Intent pickStatusIntent = new Intent(ContextProvider.getAppContext(), StatusListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(StatusListActivity.STATUSES, new ArrayList<>(cluster.getItems()));
        pickStatusIntent.putExtras(bundle);
        startActivityForResult(pickStatusIntent, PICK_STATUSES);
        return false;
    }
}
