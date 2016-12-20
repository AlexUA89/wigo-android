package com.wigo.android.ui.elements;

import android.os.AsyncTask;

import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.server.dto.MessageDto;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.core.server.requestapi.errors.WigoException;

/**
 * Created by AlexUA89 on 10/25/2016.
 */

public class SendMessageTask extends AsyncTask<Void, Void, Void> {

    private MessageDto messageDto;
    private StatusDto statusDto;
    private SendMessageListener listener;

    public SendMessageTask(MessageDto messageDto, StatusDto statusDto, SendMessageListener listener) {
        this.messageDto = messageDto;
        this.statusDto = statusDto;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            messageDto = ContextProvider.getWigoRestClient().sendMessage(statusDto, messageDto);
            if (messageDto.getId() == null) {
                this.cancel(true);
            }
        } catch (WigoException e) {
            e.printStackTrace();
            listener.sendMessageConnectionError(messageDto, statusDto, e);
            this.cancel(true);
        }
        return null;
    }

    @Override
    protected void onCancelled(Void aVoid) {
        super.onCancelled(aVoid);
        listener = null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        listener.sendMessageDone(messageDto, statusDto);
        super.onPostExecute(aVoid);
    }

    public interface SendMessageListener {
        void sendMessageDone(MessageDto messages, StatusDto statusDto);

        void sendMessageTimeoutError(MessageDto message, StatusDto statusDto);

        void sendMessageConnectionError(MessageDto message, StatusDto statusDto, WigoException e);
    }
}
