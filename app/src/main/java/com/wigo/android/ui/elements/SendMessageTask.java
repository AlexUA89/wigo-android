package com.wigo.android.ui.elements;

import android.os.AsyncTask;

import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.server.dto.MessageDto;
import com.wigo.android.core.server.dto.StatusDto;

import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.UUID;

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
            UUID newMessageId = ContextProvider.getWigoRestClient().sendMessage(statusDto, messageDto);
            messageDto.setId(newMessageId);
            if (newMessageId == null) {
                this.cancel(true);
            }
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            listener.sendMessageConnectionError(messageDto, statusDto);
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

        void sendMessageConnectionError(MessageDto message, StatusDto statusDto);
    }
}