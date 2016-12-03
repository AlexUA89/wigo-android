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

    private static final float SCALE_FOR_MAP_ITEMS = 2f;
    private static HashMap<String, Bitmap> bitmapHashMap;

    public static Set<String> getListOfCategories() {
        return new HashSet<>(getMapOfCategoriesAndImages().keySet());
    }

    public static HashMap<String, BitmapDescriptor> getMapOfCategoriesAndImagesForMap() {
        HashMap<String, BitmapDescriptor> imagesBitmaps = new HashMap<>();
        for (Map.Entry<String, Bitmap> entry : getMapOfCategoriesAndImages().entrySet()) {
            imagesBitmaps.put(entry.getKey(), BitmapDescriptorFactory.fromBitmap(entry.getValue()));
        }
        return imagesBitmaps;
    }

    public static HashMap<String, Bitmap> getMapOfCategoriesAndImages() {
        if (bitmapHashMap != null) return bitmapHashMap;
        HashMap<String, Bitmap> imagesBitmaps = new HashMap<>();
        imagesBitmaps.put("DINING_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.dinning_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("IT", BitmapUtils.getScaledBitmap(R.mipmap.it, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("FESTIVAL_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.festival_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("COMEDY_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.comedy_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("FITNESS", BitmapUtils.getScaledBitmap(R.mipmap.fitness, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("SHOPPING", BitmapUtils.getScaledBitmap(R.mipmap.shopping, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("RELIGIOUS_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.religious_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("FOOD_TASTING", BitmapUtils.getScaledBitmap(R.mipmap.food_tasting, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("VOLUNTEERING", BitmapUtils.getScaledBitmap(R.mipmap.volunteering, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("BOOK_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.book_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("MOVIE_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.movie_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("DANCE_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.dance_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("NIGHTLIFE", BitmapUtils.getScaledBitmap(R.mipmap.nightlife, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("ART_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.art_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("SPORTS_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.sports_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("THEATER_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.theater_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("CONFERENCE_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.conference_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("FAMILY_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.family_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("OTHER", BitmapUtils.getScaledBitmap(R.mipmap.other, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("MEETUP", BitmapUtils.getScaledBitmap(R.mipmap.meetup, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("LECTURE", BitmapUtils.getScaledBitmap(R.mipmap.lecture, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("MUSIC_EVENT", BitmapUtils.getScaledBitmap(R.mipmap.music_event, SCALE_FOR_MAP_ITEMS));
        imagesBitmaps.put("WORKSHOP", BitmapUtils.getScaledBitmap(R.mipmap.workshop, SCALE_FOR_MAP_ITEMS));
        bitmapHashMap = imagesBitmaps;
        return imagesBitmaps;
    }

    public static BitmapDescriptor getDefaultChatImage() {
        return BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.chat, SCALE_FOR_MAP_ITEMS));
    }


    public static BitmapDescriptor getDefaultEventImage() {
        return BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.other, SCALE_FOR_MAP_ITEMS));
    }

    public static Set<String> getChoosenCategories() {
        Set<String> choosedCategpries = SharedPrefHelper.getCategoriesSearch(null);
        if (choosedCategpries == null) return getListOfCategories();
        Set<String> latestCat = getListOfCategories();
        Set<String> merged = new HashSet<>();
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
