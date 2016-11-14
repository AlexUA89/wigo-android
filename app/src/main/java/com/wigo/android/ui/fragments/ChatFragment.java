package com.wigo.android.ui.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.database.DBManager;
import com.wigo.android.core.database.Database;
import com.wigo.android.core.database.datas.DBStorable;
import com.wigo.android.core.database.datas.Status;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.MessageDto;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.core.server.dto.StatusKind;
import com.wigo.android.ui.MainActivity;
import com.wigo.android.ui.elements.ChatMessagesAdapter;
import com.wigo.android.ui.elements.LoadMessageFroStatusTask;
import com.wigo.android.ui.elements.SendMessageTask;
import com.wigo.android.ui.base.BaseTextWatcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class ChatFragment extends Fragment implements LoadMessageFroStatusTask.LoadMessagesForStatusTaskListener, SendMessageTask.SendMessageListener {

    private static final String TAG = ChatFragment.class.getCanonicalName();
    public static final String FRAGMENT_TAG = "CHAT_FRAGMENT";

    public static final String STATUS_DTO = "statusId";

    private Button send = null;
    private StatusDto status;
    private Status statusDb;
    private EditText msg = null;
    private ChatMessagesAdapter adapter = null;
    private ListView messagesList = null;
    private ScrollView scrollView = null;
    private Timer timer = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            String statusJson = (String) this.getArguments().get(STATUS_DTO);
            if (statusJson != null) {
                status = ContextProvider.getObjectMapper().readValue(statusJson, StatusDto.class);
            }
            if (status == null || (savedInstanceState != null && savedInstanceState.getStringArrayList(STATUS_DTO) != null)) {
                statusJson = savedInstanceState.getString(STATUS_DTO);
                status = ContextProvider.getObjectMapper().readValue(statusJson, StatusDto.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        View fragmentView = inflater.inflate(R.layout.chat_fragment, container, false);
        initParameters(getArguments());
        initView(fragmentView);
        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            outState.putString(ChatFragment.STATUS_DTO, ContextProvider.getObjectMapper().writeValueAsString(status));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    private void initParameters(Bundle args) {

    }

    private void initView(View fragmentView) {
        Database db = DBManager.getDatabase();
        db.open();
        statusDb = db.selectStatusServerById(status.getId());
        db.close();
        if (statusDb == null) {
            statusDb = new Status(DBStorable.DEFAULT_ROW_ID, status.getId(), status.getUserId(), status.getLatitude(), status.getLongitude(), status.getName(), status.getText(),
                    status.getStartDate(), status.getEndDate(), status.getKind(), new Date());
        }
        scrollView = (ScrollView) fragmentView.findViewById(R.id.chat_fragment_scroll_view);
        msg = (EditText) fragmentView.findViewById(R.id.chat_fragment_msg);
        messagesList = (ListView) fragmentView.findViewById(R.id.listView);
        adapter = new ChatMessagesAdapter();
        messagesList.setAdapter(adapter);
        TextView statusName = (TextView) fragmentView.findViewById(R.id.status_name);
        TextView statusText = (TextView) fragmentView.findViewById(R.id.status_desc);
        TextView statusHashtags = (TextView) fragmentView.findViewById(R.id.status_hashtags);
        statusName.setVisibility(View.VISIBLE);
        statusName.setText(status.getName());
        if (StatusKind.event.toString().equals(status.getKind())) {
            statusText.setVisibility(View.VISIBLE);
            statusText.setText(status.getText());
        }
        if (status.getHashtags() != null && !status.getHashtags().isEmpty()) {
            statusHashtags.setVisibility(View.VISIBLE);
            statusHashtags.setText(status.getHashtags().toString());
        }
        send = (Button) fragmentView.findViewById(R.id.chat_fragment_send);
        final ChatFragment that = this;
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = SharedPrefHelper.getUserId(null);
                if (userId == null) {
                    //TODO throw correct exception
                }
                MessageDto messageDto = new MessageDto(null, UUID.fromString(userId), msg.getText().toString(), null, SharedPrefHelper.getUserNickName(""));
                new SendMessageTask(messageDto, status, that).execute();
            }
        });

        msg.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                send.setEnabled(!msg.getText().toString().isEmpty());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        final ChatFragment that = this;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LoadMessageFroStatusTask.loadData(that, status);
            }

        }, 0, 5000);
    }

    @Override
    public void onPause() {
        timer.cancel();
        timer = null;
        super.onPause();
    }

    @Override
    public void loadMessagesDone(List<MessageDto> messages) {
        adapter.mergMessageArray(messages);
        messagesList.setSelection(adapter.getCount() - 1);
    }

    @Override
    public void sendMessageDone(MessageDto message, StatusDto statusDto) {
        adapter.mergMessageArray(Collections.singletonList(message));
        message.setCreated(new Date().toString());
        msg.setText("");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ContextProvider.getAppContext(), "Status have sent", Toast.LENGTH_SHORT).show();// display toast
            }
        });
        messagesList.setSelection(adapter.getCount() - 1);
        statusDb.setLastOpenDate(new Date());
        Database db = DBManager.getDatabase();
        db.open();
        if (statusDb.getLocalId() != DBStorable.DEFAULT_ROW_ID) {
            db.updateDBStorable(statusDb);
        } else {
            statusDb.setLocalId(db.insertNewDBStorable(statusDb));
        }
        db.close();
        ((MainActivity) getActivity()).updateMenuList();
    }

    @Override
    public void onDestroy() {
        LoadMessageFroStatusTask.cancel();
        super.onDestroy();
    }

    @Override
    public void loadMessagesTimeoutError(StatusDto statusDto) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ContextProvider.getAppContext(), "Timeout error. Try one more time", Toast.LENGTH_SHORT).show();// display toast
            }
        });
    }

    @Override
    public void loadMessagesConnectionError(StatusDto statusDto) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ContextProvider.getAppContext(), "Connection error. Try one more time", Toast.LENGTH_SHORT).show();// display toast
            }
        });
    }

    @Override
    public void sendMessageTimeoutError(MessageDto messages, StatusDto statusDto) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ContextProvider.getAppContext(), "Timeout error. Try one more time", Toast.LENGTH_SHORT).show();// display toast
            }
        });
    }

    @Override
    public void sendMessageConnectionError(MessageDto messages, StatusDto statusDto) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ContextProvider.getAppContext(), "Connection error. Try one more time", Toast.LENGTH_SHORT).show();// display toast
            }
        });
    }

    public StatusDto getStatus() {
        return status;
    }
}
