package com.wigo.android.ui.elements;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.MessageDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        if (convertView == null) {
            view = lInflater.inflate(R.layout.chat_list_item, parent, false);
        }
        if (isMessageFromMe(messagesArray.get(position))) {
            ((LinearLayout) view.findViewById(R.id.chat_list_item_message_container)).setGravity(Gravity.END);
            view.findViewById(R.id.chat_list_item_message_text_container).setBackground(ContextCompat.getDrawable(ContextProvider.getAppContext(), R.drawable.chat_item_from_me_bg));
        } else {
            ((LinearLayout) view.findViewById(R.id.chat_list_item_message_container)).setGravity(Gravity.START);
            view.findViewById(R.id.chat_list_item_message_text_container).setBackground(ContextCompat.getDrawable(ContextProvider.getAppContext(), R.drawable.chat_item_not_from_me_bg));
        }

        ((TextView) view.findViewById(R.id.chat_list_item_message_text)).setText(messagesArray.get(position).getText());
        ((TextView) view.findViewById(R.id.chat_list_item_name_date)).setText(messagesArray.get(position).getNickname() + " " + messagesArray.get(position).getCreated());
        convertView = view;
        return convertView;
    }

    public void mergMessageArray(List<MessageDto> messagesArray) {
        for (MessageDto newMessage : messagesArray) {
            boolean alreadyExist = false;
            for (MessageDto oldMessage : this.messagesArray) {
                if (oldMessage.getId().equals(newMessage.getId())) {
                    alreadyExist = true;
                    break;
                }
            }
            if (!alreadyExist) {
                this.messagesArray.add(newMessage);
            }
        }
        this.notifyDataSetChanged();
    }

    public List<MessageDto> getMessagesArray() {
        return messagesArray;
    }

    public void setMessagesArray(List<MessageDto> messagesArray) {
        this.messagesArray = messagesArray;
    }

    private boolean isMessageFromMe(MessageDto message) {
        String userId = SharedPrefHelper.getUserId(null);
        if (userId == null) return false;
        return message.getUserId().equals(UUID.fromString(userId));
    }

}
