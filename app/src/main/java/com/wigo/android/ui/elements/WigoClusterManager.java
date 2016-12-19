package com.wigo.android.ui.elements;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.ui.MainActivity;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by AlexUA89 on 12/18/2016.
 */

public class WigoClusterManager extends ClusterManager<StatusDto> implements ClusterManager.OnClusterClickListener<StatusDto>, ClusterManager.OnClusterItemClickListener<StatusDto>, ClusterManager.OnClusterInfoWindowClickListener<StatusDto>, ClusterManager.OnClusterItemInfoWindowClickListener<StatusDto> {

    private HashMap<UUID, StatusDto> statuses = new HashMap<>();
    private Context context;

    public WigoClusterManager(Context context, GoogleMap map) {
        super(context, map, new MarkerManager(map));
        this.context = context;
        this.setRenderer(new StatusIconRenderer(context, map, this));
        this.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<StatusDto>() {
            @Override
            public boolean onClusterClick(Cluster<StatusDto> cluster) {
                return false;
            }
        });
        map.setOnInfoWindowClickListener(this);
        map.setOnMarkerClickListener(this);
        this.setOnClusterClickListener(this);
        this.setOnClusterItemClickListener(this);
        this.setOnClusterInfoWindowClickListener(this);
        this.setOnClusterItemInfoWindowClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return super.onMarkerClick(marker);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        super.onInfoWindowClick(marker);

    }

    public void addStatuses(List<StatusDto> statuses){
        for (StatusDto status : statuses) {
            if (!this.statuses.keySet().contains(status.getId())) {
                StatusDto posTemp = this.statuses.put(status.getId(), status);
                this.addItem(status);
            }
        }
        this.cluster();
    }

    @Override
    public boolean onClusterClick(Cluster<StatusDto> cluster) {
        return false;
    }

    @Override
    public boolean onClusterItemClick(StatusDto statusDto) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<StatusDto> cluster) {

    }

    @Override
    public void onClusterItemInfoWindowClick(StatusDto statusDto) {
        StatusDto status = statuses.get(statusDto.getId());
        ((MainActivity) context).openChatFragment(status);
        Toast.makeText(ContextProvider.getAppContext(), statusDto.getName(), Toast.LENGTH_SHORT).show();// display toast
    }

    public void removeAll(){
        this.clearItems();
        statuses.clear();
        this.cluster();
    }
}
