package com.wigo.android.ui.elements;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.wigo.android.R;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.utils.BitmapUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by AlexUA89 on 12/2/2016.
 */

public class CategoriesProvider {

    private static final float SCALE_FOR_MAP_ITEMS = 2.5f;
    private static HashMap<String, Integer> resourcesMap;
    private static HashMap<String, Bitmap> bitmapMap;
    public static final String OTHER = "OTHER";

    public static HashSet<String> getListOfCategories() {
        return new HashSet<>(getMapOfCategoriesAndResources().keySet());
    }

    public static HashMap<String, BitmapDescriptor> getMapOfCategoriesAndImagesForMap() {
        HashMap<String, BitmapDescriptor> imagesBitmaps = new HashMap<>();
        for (Map.Entry<String, Integer> entry : getMapOfCategoriesAndResources().entrySet()) {
            imagesBitmaps.put(entry.getKey(), BitmapDescriptorFactory.fromBitmap(getScaledBitmap(entry.getValue())));
        }
        return imagesBitmaps;
    }

    public static HashMap<String, Bitmap> getMapOfCategoiesAndBitmaps() {
        if (bitmapMap != null) return bitmapMap;
        bitmapMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : getMapOfCategoriesAndResources().entrySet()) {
            bitmapMap.put(entry.getKey(), getScaledBitmap(entry.getValue()));
        }
        return bitmapMap;
    }

    public static HashMap<String, Integer> getMapOfCategoriesAndResources() {
        if (resourcesMap != null) return resourcesMap;
        HashMap<String, Integer> imagesBitmaps = new HashMap<>();
        imagesBitmaps.put("DINING_EVENT", R.mipmap.dinning_event);
        imagesBitmaps.put("IT", R.mipmap.it);
        imagesBitmaps.put("FESTIVAL_EVENT", R.mipmap.festival_event);
        imagesBitmaps.put("COMEDY_EVENT", R.mipmap.comedy_event);
        imagesBitmaps.put("FITNESS", R.mipmap.fitness);
        imagesBitmaps.put("SHOPPING", R.mipmap.shopping);
        imagesBitmaps.put("RELIGIOUS_EVENT", R.mipmap.religious_event);
        imagesBitmaps.put("FOOD_TASTING", R.mipmap.food_tasting);
        imagesBitmaps.put("VOLUNTEERING", R.mipmap.volunteering);
        imagesBitmaps.put("BOOK_EVENT", R.mipmap.book_event);
        imagesBitmaps.put("MOVIE_EVENT", R.mipmap.movie_event);
        imagesBitmaps.put("DANCE_EVENT", R.mipmap.dance_event);
        imagesBitmaps.put("NIGHTLIFE", R.mipmap.nightlife);
        imagesBitmaps.put("ART_EVENT", R.mipmap.art_event);
        imagesBitmaps.put("SPORTS_EVENT", R.mipmap.sports_event);
        imagesBitmaps.put("THEATER_EVENT", R.mipmap.theater_event);
        imagesBitmaps.put("CONFERENCE_EVENT", R.mipmap.conference_event);
        imagesBitmaps.put("FAMILY_EVENT", R.mipmap.family_event);
        imagesBitmaps.put("MEETUP", R.mipmap.meetup);
        imagesBitmaps.put("LECTURE", R.mipmap.lecture);
        imagesBitmaps.put("MUSIC_EVENT", R.mipmap.music_event);
        imagesBitmaps.put("WORKSHOP", R.mipmap.workshop);
        imagesBitmaps.put("CHAT", R.mipmap.chat);
        imagesBitmaps.put("CELEBRATION", R.mipmap.chat);
        imagesBitmaps.put("ACTIVITY", R.mipmap.chat);
        imagesBitmaps.put(OTHER, R.mipmap.other);
        resourcesMap = imagesBitmaps;
        return imagesBitmaps;
    }

    public static Bitmap getScaledBitmap(int resId) {
        return BitmapUtils.getScaledBitmap(resId, SCALE_FOR_MAP_ITEMS);
    }

    public static BitmapDescriptor getDefaultEventImage() {
        return BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.other, SCALE_FOR_MAP_ITEMS));
    }

    public static HashSet<String> getChoosenCategories() {
        Set<String> choosedCategpries = SharedPrefHelper.getCategoriesSearch(null);
        if (choosedCategpries == null) return getListOfCategories();
        Set<String> latestCat = getListOfCategories();
        HashSet<String> merged = new HashSet<>();
        for (String category : choosedCategpries) {
            if (latestCat.contains(category)) {
                merged.add(category);
            }
        }
        return merged;
    }

    public static void setChoosenCategories(Set<String> choosedCategpries) {
        if(choosedCategpries==null) return;
        Set<String> latestCat = getListOfCategories();
        Set<String> merged = new HashSet<>();
        for (String category : choosedCategpries) {
            if (latestCat.contains(category)) {
                merged.add(category);
            }
        }
        SharedPrefHelper.setCategoriesSearch(merged);
    }
}
