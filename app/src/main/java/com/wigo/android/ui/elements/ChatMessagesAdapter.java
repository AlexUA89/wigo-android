package com.wigo.android.ui.elements;

import android.content.Context;
import android.database.Cursor;
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
import com.wigo.android.core.database.DBManager;
import com.wigo.android.core.database.Database;
import com.wigo.android.core.database.datas.DBStorable;
import com.wigo.android.core.database.datas.Message;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.MessageDto;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * Created by AlexUA89 on 10/28/2016.
 */
public class ChatMessagesAdapter extends BaseAdapter {

    private List<Message> messagesArray = new ArrayList<>();
    private LayoutInflater lInflater;
    private UUID statusId;

    public ChatMessagesAdapter() {
        lInflater = (LayoutInflater) ContextProvider.getAppContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ChatMessagesAdapter(UUID statusId) {
        this.statusId = statusId;
        Database db = DBManager.getDatabase();
        db.open();
        Cursor c = db.selectMessagesForStatus(statusId);
        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                try {
                    messagesArray.add(new Message(c));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.moveToNext();
            }
        }
        db.close();
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

    public void mergMessageArray(List<MessageDto> messages) {
        List<MessageDto> sortedMessages = new ArrayList<>(messages);
        Calendar date = getLastMessageDate();
        List<Message> newMessages = new ArrayList<>();
        Collections.sort(sortedMessages, new Comparator<MessageDto>() {
            @Override
            public int compare(MessageDto lhs, MessageDto rhs) {
                if (lhs.getCreated().after(rhs.getCreated())) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        for (MessageDto m : sortedMessages) {
            if (m.getCreated().after(date.getTime())) {
                newMessages.add(new Message(m.getId(), m.getUserId(), m.getText(), m.getCreated(), m.getNickname(), statusId));
            }
        }
        Database db = DBManager.getDatabase();
        db.open();
        db.insertNewDBStorables(new ArrayList<DBStorable>(newMessages));
        db.close();
        this.messagesArray.addAll(newMessages);
        this.notifyDataSetChanged();
    }

    public List<Message> getMessagesArray() {
        return messagesArray;
    }

    public void setMessagesArray(List<Message> messagesArray) {
        this.messagesArray = messagesArray;
    }

    private boolean isMessageFromMe(Message message) {
        String userId = SharedPrefHelper.getUserId(null);
        if (userId == null) return false;
        return message.getUserId().equals(UUID.fromString(userId));
    }

    public Calendar getLastMessageDate() {
        Calendar calendar = Calendar.getInstance();
        if (!messagesArray.isEmpty()) {
            calendar.setTime(messagesArray.get(messagesArray.size() - 1).getCreated());
            calendar.add(Calendar.MILLISECOND, 1);
        } else {
            calendar.roll(Calendar.MONTH, 1);
        }
        return calendar;
    }

}
