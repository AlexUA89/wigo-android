package com.wigo.android.ui.elements;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.wigo.android.R;
import com.wigo.android.core.utils.BitmapUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by AlexUA89 on 12/2/2016.
 */

public class CategoriesProvider {

    private static final float SCALE_FOR_MAP_ITEMS = 2f;

    public static List<String> getListOfCategories(){
        return Collections.emptyList();
    }

    public static HashMap<String, BitmapDescriptor> getMapOfCategoriesAndImages(){
        HashMap<String, BitmapDescriptor> imagesBitmaps = new HashMap<>();
        imagesBitmaps.put("DINING_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.dinning_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("IT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.it, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("FESTIVAL_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.festival_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("COMEDY_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.comedy_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("FITNESS", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.fitness, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("SHOPPING", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.shopping, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("RELIGIOUS_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.religious_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("FOOD_TASTING", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.food_tasting, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("VOLUNTEERING", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.volunteering, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("BOOK_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.book_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("MOVIE_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.movie_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("DANCE_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.dance_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("NIGHTLIFE", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.nightlife, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("ART_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.art_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("SPORTS_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.sports_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("THEATER_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.theater_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("CONFERENCE_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.conference_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("FAMILY_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.family_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("OTHER", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.other, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("MEETUP", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.meetup, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("LECTURE", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.lecture, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("MUSIC_EVENT", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.music_event, SCALE_FOR_MAP_ITEMS)));
        imagesBitmaps.put("WORKSHOP", BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.workshop, SCALE_FOR_MAP_ITEMS)));
        return imagesBitmaps;
    }

    public static BitmapDescriptor getDefaultChatImage(){
        return BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.chat, SCALE_FOR_MAP_ITEMS));
    }


    public static BitmapDescriptor getDefaultEventImage(){
       return BitmapDescriptorFactory.fromBitmap(BitmapUtils.getScaledBitmap(R.mipmap.other, SCALE_FOR_MAP_ITEMS));
    }
}
