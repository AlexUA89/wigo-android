package com.wigo.android.ui.elements;

import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.maps.model.LatLngBounds;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.server.dto.StatusDto;

import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by AlexUA89 on 10/12/2016.
 */
public class LoadMapStatusesTask extends AsyncTask<Void, Void, Void> {

    private LoadMapStatusesTaskListener listener;
    private LatLngBounds curScreen;
    private List<StatusDto> statuses;
    private List<String> tags = new ArrayList<>();
    private Calendar fromDate;
    private Calendar toDate;
    private String searchString;
    private static LoadMapStatusesTask task;
    private static Handler handler;
    private static final long TIME_DELAY = 1000;

    public static void loadData(final LoadMapStatusesTaskListener listener, final LatLngBounds curScreen, final List<String> tags, final Calendar fromDate, final Calendar toDate, final String searchString) {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        } else {
            handler = new Handler();
        }
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                task = new LoadMapStatusesTask(listener, curScreen, tags, fromDate, toDate, searchString);
                task.execute();
            }
        }, TIME_DELAY);
    }

    private LoadMapStatusesTask(LoadMapStatusesTaskListener listener, LatLngBounds curScreen, List<String> tags, Calendar fromDate, Calendar toDate, String searchString) {
        this.listener = listener;
        this.curScreen = curScreen;
        this.tags = tags;
        this.toDate = toDate;
        this.fromDate = fromDate;
        this.searchString = searchString;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            statuses = ContextProvider.getWigoRestClient()
                    .getStatusesListFromServer(curScreen.northeast.latitude, curScreen.southwest.latitude, curScreen.northeast.longitude, curScreen.southwest.longitude, tags, fromDate, toDate, searchString);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            listener.loadMapStateseConnectionError(curScreen);
            this.cancel(true);
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        listener.loadMapStatusesDone(statuses);
        listener = null;
        task = null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        listener = null;
    }

    public interface LoadMapStatusesTaskListener {
        void loadMapStatusesDone(List<StatusDto> statuses);

        void loadMapStateseTimeoutError(LatLngBounds curScreen);

        void loadMapStateseConnectionError(LatLngBounds curScreen);
    }
}