package com.wigo.android.core.server.requestapi;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.FaceBookUserInfoDto;
import com.wigo.android.core.server.dto.MessageDto;
import com.wigo.android.core.server.dto.StatusDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.config.RequestConfig;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.message.AbstractHttpMessage;
import cz.msebera.android.httpclient.message.BasicHeader;
import cz.msebera.android.httpclient.protocol.HTTP;

/**
 * Created by AlexUA on 9/10/2016.
 */
public class WigoRestClient {

    private HttpClient client;
    public static final int TIMEOUT = 10000;

    public WigoRestClient() {
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(TIMEOUT);
        requestBuilder = requestBuilder.setConnectionRequestTimeout(TIMEOUT);

        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(requestBuilder.build());
        client = builder.build();
    }

    public FaceBookUserInfoDto getUserInfoFromFacebook(String faceBookToken) throws IOException {
        HttpGet request = new HttpGet("https://graph.facebook.com/v2.7/me?access_token=" + faceBookToken
                + "&fields=id%2Cname%2Cfirst_name%2Cmiddle_name%2Clast_name%2Cemail%2Clink&format=json&sdk=android");
        setHeader(request);
        HttpResponse response = client.execute(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        String json = reader.readLine();
        FaceBookUserInfoDto userInfoDto = ContextProvider.getObjectMapper().readValue(json, FaceBookUserInfoDto.class);
        return userInfoDto;
    }

    public List<StatusDto> getStatusesListFromServer(double startLatitude, double endLatitude, double startLongitude, double endLongitude) throws IOException {
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        HttpGet request = new HttpGet(serverUrl
                + "/api/status?startLatitude=" + Math.min(startLatitude, endLatitude)
                + "&endLatitude=" + Math.max(startLatitude, endLatitude)
                + "&startLongitude=" + Math.min(startLongitude, endLongitude)
                + "&endLongitude=" + Math.max(startLongitude, endLongitude));
        setHeader(request);
        HttpResponse response = client.execute(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        String json = reader.readLine();
        StatusDto[] statuses = ContextProvider.getObjectMapper().readValue(json, StatusDto[].class);
        return Arrays.asList(statuses);
    }

    public List<MessageDto> getListOfMessagesForStatus(StatusDto statusDto) throws IOException {
        Objects.requireNonNull(statusDto);
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        HttpGet request = new HttpGet(serverUrl + "/api/status/" + statusDto.getId() + "/messages");
        setHeader(request);
        HttpResponse response = client.execute(request);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
        String json = reader.readLine();
        MessageDto[] messageDtos = ContextProvider.getObjectMapper().readValue(json, MessageDto[].class);
        return new ArrayList<>(Arrays.asList(messageDtos));
    }

    public boolean sendMessage(StatusDto statusDto, MessageDto messageDto) throws IOException {
        Objects.requireNonNull(messageDto);
        Objects.requireNonNull(statusDto);
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        HttpPost request = new HttpPost(serverUrl + "/api/status/" + statusDto.getId() + "/messages");
        StringEntity se = new StringEntity(ContextProvider.getObjectMapper().writeValueAsString(messageDto), "UTF8");
        request.setHeader(HTTP.CONTENT_TYPE, "application/json");
        request.setEntity(se);
        HttpResponse response = client.execute(request);
        return response.getStatusLine().getStatusCode() == 200;
    }

    private void setHeader(AbstractHttpMessage request) {
        String token = SharedPrefHelper.getToken(null);
        if (token != null) {
            request.addHeader("token", token);
        }
    }

}
