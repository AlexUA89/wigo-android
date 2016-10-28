package com.wigo.android.ui.elements;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.server.dto.MessageDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AlexUA89 on 10/28/2016.
 */
public class ChatMessagesAdapter extends BaseAdapter {

    private List<MessageDto> messagesArray = new ArrayList<>();
    private LayoutInflater lInflater;

    public ChatMessagesAdapter() {
        lInflater = (LayoutInflater) ContextProvider.getAppContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ChatMessagesAdapter(List<MessageDto> messagesArray) {
        this.messagesArray = messagesArray;
        lInflater = (LayoutInflater) ContextProvider.getAppContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return messagesArray.size();
    }

    @Override
    public Object getItem(int position) {
        return messagesArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.chat_list_item, parent, false);
        }
        ((TextView) view.findViewById(R.id.chat_list_item_message_text)).setText(messagesArray.get(position).getText());

        ((LinearLayout) view.findViewById(R.id.chat_list_item_message_text_container)).setGravity(Gravity.START);
        if (isMessageFromMe(messagesArray.get(position))) {
            ((LinearLayout) view.findViewById(R.id.chat_list_item_message_text_container)).setGravity(Gravity.END);
        }
        return view;
    }

    public void mergMessageArray(List<MessageDto> messagesArray) {
        this.messagesArray.addAll(messagesArray);
        this.notifyDataSetChanged();
    }

    public List<MessageDto> getMessagesArray() {
        return messagesArray;
    }

    public void setMessagesArray(List<MessageDto> messagesArray) {
        this.messagesArray = messagesArray;
    }

    private boolean isMessageFromMe(MessageDto message) {
        return Math.round(Math.random()) == 1l;
    }


}
