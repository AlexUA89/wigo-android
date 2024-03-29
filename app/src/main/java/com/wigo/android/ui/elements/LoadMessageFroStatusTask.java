package com.wigo.android.ui.elements;

import android.os.AsyncTask;

import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.server.dto.MessageDto;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.core.server.requestapi.errors.WigoException;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * Created by AlexUA89 on 10/25/2016.
 */

public class LoadMessageFroStatusTask extends AsyncTask<Void, Void, Void> {

    private static LoadMessageFroStatusTask task;

    private LoadMessagesForStatusTaskListener listener;
    private StatusDto statusDto;
    private List<MessageDto> messages;
    private Calendar fromDate;


    public static void loadData(final LoadMessageFroStatusTask.LoadMessagesForStatusTaskListener listener, final StatusDto statusDto, final Calendar fromDate) {
        Objects.requireNonNull(statusDto);
        Objects.requireNonNull(listener);
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        task = new LoadMessageFroStatusTask(listener, statusDto, fromDate);
        task.execute();
    }

    public static void cancel() {
        if (task != null) {
            task.listener = null;
            task.cancel();
        }
    }

    private LoadMessageFroStatusTask(LoadMessagesForStatusTaskListener listener, StatusDto statusDto, Calendar fromDate) {
        this.listener = listener;
        this.statusDto = statusDto;
        this.fromDate = fromDate;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            messages = ContextProvider.getWigoRestClient().getListOfMessagesForStatus(statusDto, fromDate);
        } catch (WigoException e) {
            e.printStackTrace();
            listener.loadMessagesConnectionError(statusDto, e);
            this.cancel(true);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        listener.loadMessagesDone(messages);
        listener = null;
        task = null;
        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        listener = null;
    }


    public interface LoadMessagesForStatusTaskListener {
        void loadMessagesDone(List<MessageDto> messages);

        void loadMessagesTimeoutError(StatusDto statusDto);

        void loadMessagesConnectionError(StatusDto statusDto, WigoException e);
    }

}
