package com.wigo.android.ui.elements;

import android.os.AsyncTask;

import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.server.dto.MessageDto;
import com.wigo.android.core.server.dto.StatusDto;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by AlexUA89 on 10/25/2016.
 */

public class LoadMessageFroStatusTask  extends AsyncTask<Void, Void, Void> {

    private static LoadMessageFroStatusTask task;

    private LoadMessagesForStatusTaskListener listener;
    private StatusDto statusDto;
    private List<MessageDto> messages;


    public static void loadData(final LoadMessageFroStatusTask.LoadMessagesForStatusTaskListener listener, final StatusDto statusDto) {
        Objects.requireNonNull(statusDto);
        Objects.requireNonNull(listener);
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        task = new LoadMessageFroStatusTask(listener, statusDto);
        task.execute();
    }

    private LoadMessageFroStatusTask(LoadMessagesForStatusTaskListener listener, StatusDto statusDto) {
        this.listener = listener;
        this.statusDto = statusDto;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            messages = ContextProvider.getWigoRestClient().getListOfMessagesForStatus(statusDto);
        } catch (IOException e) {
            e.printStackTrace();
            listener.loadMessagesConnectionError(statusDto);
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

        void loadMessagesConnectionError(StatusDto statusDto);
    }

}
