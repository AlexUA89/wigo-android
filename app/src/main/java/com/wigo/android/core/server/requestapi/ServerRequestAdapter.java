package com.wigo.android.core.server.requestapi;

import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.GetPrivateMessagesResponceDto;
import com.wigo.android.core.server.dto.LoginRequestDto;
import com.wigo.android.core.server.dto.LoginResponseDto;
import com.wigo.android.core.server.dto.MessageDto;
import com.wigo.android.core.server.dto.SendingMessageResponseDto;
import com.android.volley.Response;

import java.util.HashMap;

public class ServerRequestAdapter {

    private static final String serverUrl = ContextProvider.getAppContext().getResources().getString(R.string.server_url);

    public static void singinRequest(String email, String password, Response.Listener<LoginResponseDto> listener, Response.ErrorListener errorListener) {
        LoginRequestDto body = new LoginRequestDto();
        body.setEmail(email);
        body.setPassword(password);
        new JsonRequest(JsonRequest.POST, serverUrl + "/auth/signin", new HashMap<>(), body, LoginResponseDto.class, listener, errorListener).execute();
    }

    public static void sendMessage(MessageDto message, Response.Listener<SendingMessageResponseDto> listener, Response.ErrorListener errorListener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("token", SharedPrefHelper.getToken(""));
        new JsonRequest(JsonRequest.POST, serverUrl + "/messages/send", params, message, SendingMessageResponseDto.class, listener, errorListener).execute();
    }

    public static void getPrivateMessages(long time, Response.Listener<GetPrivateMessagesResponceDto> listener, Response.ErrorListener errorListener) {
        HashMap<String, String> params = new HashMap<>();
        params.put("token", SharedPrefHelper.getToken(""));
        new JsonRequest(JsonRequest.GET, serverUrl + "/messages/getPrivate", params, null, GetPrivateMessagesResponceDto.class, listener, errorListener).execute();
    }

}
