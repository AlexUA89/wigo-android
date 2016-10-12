package com.wigo.android.ui.asynctasks;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by AlexUA89 on 10/12/2016.
 */
public class LoadMapStatusesTask extends AsyncTask<Void, Void, Void> {

    private LoadMapStatusesTaskListener listener;
    private HashMap<MarkerOptions, UUID> markers = new HashMap<>();
    private HashMap<LatLng, UUID> positions = new HashMap<>();

    public LoadMapStatusesTask(LoadMapStatusesTaskListener listener){
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        getAllMarkers();
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        listener.loadMapStatusesDone(markers, positions);
        listener = null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        listener = null;
    }

    private void getAllMarkers() {
//        for (int i = 0; i < 100; i++) {
//            LatLng pos = new LatLng((Math.random() - 0.5) * 90, (Math.random() - 0.5) * 180);
//            MarkerOptions marker = new MarkerOptions().position(pos).title("Position - " + i).snippet("Sniped text");
//            UUID id = UUID.randomUUID();
//            markers.put(marker, id);
//            positions.put(pos, id);
//        }

        UUID id = UUID.randomUUID();
        LatLng pos =  new LatLng(50.449362, 30.479365);
        MarkerOptions marker = new MarkerOptions().position(pos).title("Position - " + 1).snippet("Sniped text");
        markers.put(marker, id);
        positions.put(pos, id);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public interface LoadMapStatusesTaskListener {
        void loadMapStatusesDone(HashMap<MarkerOptions, UUID> markers, HashMap<LatLng, UUID> positions);
    }
}