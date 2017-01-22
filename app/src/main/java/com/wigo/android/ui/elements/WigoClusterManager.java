package com.wigo.android.ui.elements;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.MarkerManager;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.server.dto.StatusSmallDto;
import com.wigo.android.ui.MainActivity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by AlexUA89 on 12/18/2016.
 */

public class WigoClusterManager extends ClusterManager<StatusSmallDto> implements ClusterManager.OnClusterItemClickListener<StatusSmallDto>, ClusterManager.OnClusterInfoWindowClickListener<StatusSmallDto>, ClusterManager.OnClusterItemInfoWindowClickListener<StatusSmallDto> {

    private HashMap<UUID, StatusSmallDto> statuses = new HashMap<>();
    private Context context;

    public WigoClusterManager(Context context, GoogleMap map) {
        super(context, map, new MarkerManager(map));
        this.context = context;
        this.setRenderer(new StatusIconRenderer(context, map, this));
        this.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<StatusSmallDto>() {
            @Override
            public boolean onClusterClick(Cluster<StatusSmallDto> cluster) {
                return false;
            }
        });
        map.setOnInfoWindowClickListener(this);
        map.setOnMarkerClickListener(this);
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

    public void addStatuses(List<StatusSmallDto> statuses){
        for (StatusSmallDto status : statuses) {
            if (!this.statuses.keySet().contains(status.getId())) {
                StatusSmallDto posTemp = this.statuses.put(status.getId(), status);
                this.addItem(status);
            }
        }
        this.cluster();
    }

    public Collection<StatusSmallDto> getStatuses() {
        return statuses.values();
    }

    @Override
    public boolean onClusterItemClick(StatusSmallDto statusDto) {
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<StatusSmallDto> cluster) {

    }

    @Override
    public void onClusterItemInfoWindowClick(StatusSmallDto statusDto) {
        StatusSmallDto status = statuses.get(statusDto.getId());
        ((MainActivity) context).openChatFragment(status.getId());
        Toast.makeText(ContextProvider.getAppContext(), statusDto.getName(), Toast.LENGTH_SHORT).show();// display toast
    }

    public void removeAll(){
        this.clearItems();
        statuses.clear();
        this.cluster();
    }
}
