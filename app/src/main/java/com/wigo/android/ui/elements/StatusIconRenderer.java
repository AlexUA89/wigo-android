package com.wigo.android.ui.elements;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.wigo.android.core.server.dto.StatusSmallDto;

import java.util.HashMap;

/**
 * Created by AlexUA89 on 12/18/2016.
 */

public class StatusIconRenderer extends DefaultClusterRenderer<StatusSmallDto> {

    private BitmapDescriptor eventBitmap;
    private HashMap<String, BitmapDescriptor> imagesBitmaps = new HashMap<>();

    public StatusIconRenderer(Context context, GoogleMap map, ClusterManager<StatusSmallDto> clusterManager) {
        super(context, map, clusterManager);
        imagesBitmaps = CategoriesProvider.getMapOfCategoriesAndImagesForMap();
        eventBitmap = CategoriesProvider.getDefaultEventImage();
    }

    @Override
    protected void onBeforeClusterItemRendered(StatusSmallDto item, MarkerOptions markerOptions) {
        markerOptions.title(item.getName());
        BitmapDescriptor bitmap = eventBitmap;
        if (item.getCategory() != null && imagesBitmaps.get(item.getCategory()) != null) {
            bitmap = imagesBitmaps.get(item.getCategory());
        }
        markerOptions.icon(bitmap);
    }
}
