package com.wigo.android.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.wigo.android.R;
import com.wigo.android.core.server.dto.MessageDto;
import com.wigo.android.core.server.socketapi.SocketHelper;
import com.wigo.android.ui.base.BaseTextWatcher;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private static final String TAG = ChatFragment.class.getCanonicalName();
    public static final String FRAGMENT_TAG = "CHAT_FRAGMENT";

    public static final String TO_USER_ID = "toUserId";
    public static final String CHAT_GROUP_ID = "chatGroupId";
    private static final String LIST_KEY = "LIST_KEY";

    Button send = null;
    ArrayList<String> messagesArray = new ArrayList<>();
    ListView messagesList;
    EditText msg = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.chat_fragment, container, false);
        if (savedInstanceState != null && savedInstanceState.getStringArrayList(LIST_KEY) != null) {
            messagesArray = savedInstanceState.getStringArrayList(LIST_KEY);
        }
        initView(fragmentView);
        initParameters(getArguments());
        return fragmentView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putStringArrayList(LIST_KEY, messagesArray);
        super.onSaveInstanceState(outState);
    }

    private void initParameters(Bundle args) {

    }


    private void initView(View fragmentView) {
        msg = (EditText) fragmentView.findViewById(R.id.chat_fragment_msg);
        messagesList = (ListView) fragmentView.findViewById(R.id.listView);
        messagesList.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, messagesArray));
        final Fragment fr = this;

        send = (Button) fragmentView.findViewById(R.id.chat_fragment_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketHelper.sendMessage(new MessageDto(), new SocketHelper.MessageHandler() {
                    @Override
                    public void receiveMessage(final String message) {
                        fr.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                messagesArray.add(message);
                                messagesList.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, messagesArray));
                            }
                        });
                    }
                });
            }
        });

        msg.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                send.setEnabled(!msg.getText().toString().isEmpty());
            }
        });
    }

}
