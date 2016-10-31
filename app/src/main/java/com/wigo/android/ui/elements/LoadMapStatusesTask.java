package com.wigo.android.ui.elements;

import android.os.AsyncTask;
import android.os.Handler;

import com.google.android.gms.maps.model.LatLngBounds;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.server.dto.StatusDto;

import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.List;

/**
 * Created by AlexUA89 on 10/12/2016.
 */
public class LoadMapStatusesTask extends AsyncTask<Void, Void, Void> {

    private LoadMapStatusesTaskListener listener;
    private LatLngBounds curScreen;
    private List<StatusDto> statuses;
    private static LoadMapStatusesTask task;
    private static Handler handler;
    private static final long TIME_DELAY = 1000;

    public static void loadData(final LoadMapStatusesTaskListener listener, final LatLngBounds curScreen) {
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
                task = new LoadMapStatusesTask(listener, curScreen);
                task.execute();
            }
        }, TIME_DELAY);
    }

    private LoadMapStatusesTask(LoadMapStatusesTaskListener listener, LatLngBounds curScreen) {
        this.listener = listener;
        this.curScreen = curScreen;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            statuses = ContextProvider.getWigoRestClient()
                    .getStatusesListFromServer(curScreen.northeast.latitude, curScreen.southwest.latitude, curScreen.northeast.longitude, curScreen.southwest.longitude);
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