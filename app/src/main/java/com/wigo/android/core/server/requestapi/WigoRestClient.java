package com.wigo.android.core.server.requestapi;


import android.text.TextUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.FaceBookUserInfoDto;
import com.wigo.android.core.server.dto.MessageDto;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.core.server.dto.WigoUserInfoResponseDto;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


/**
 * Created by AlexUA on 9/10/2016.
 */
public class WigoRestClient {

    private RestTemplate client;
    private ObjectMapper mapper;

    public WigoRestClient() {
        client = new RestTemplate();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        mapper = converter.getObjectMapper();
        client.getMessageConverters().add(converter);
    }

    public FaceBookUserInfoDto getUserInfoFromFacebook(String faceBookToken) {
        String requestUrl = "https://graph.facebook.com/v2.7/me?access_token=" + faceBookToken
                + "&fields=id,name,first_name,middle_name,last_name,email,link&format=json&sdk=android";
        HttpEntity request = new HttpEntity(getHeaders());
        ResponseEntity<FaceBookUserInfoDto> response = client.exchange(requestUrl, HttpMethod.GET, request, FaceBookUserInfoDto.class);
        return response.getBody();
    }

    public WigoUserInfoResponseDto getUserInfoFromWigo(String faceBookToken) {
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl + "/api/login";
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("fbToken", faceBookToken);
        HttpEntity<HashMap<String, String>> request = new HttpEntity(requestBody, getHeaders());
        ResponseEntity<WigoUserInfoResponseDto> response = client.exchange(requestUrl, HttpMethod.POST, request, WigoUserInfoResponseDto.class, request);
        return response.getBody();
    }

    public List<StatusDto> getStatusesListFromServer(double startLatitude, double endLatitude, double startLongitude, double endLongitude, List<String> tags) {
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl
                + "/api/status?startLatitude=" + Math.min(startLatitude, endLatitude)
                + "&endLatitude=" + Math.max(startLatitude, endLatitude)
                + "&startLongitude=" + Math.min(startLongitude, endLongitude)
                + "&endLongitude=" + Math.max(startLongitude, endLongitude);
        if(!tags.isEmpty()){
            requestUrl = requestUrl + "&hashtags=" + TextUtils.join(",", tags);
        }
        HttpEntity request = new HttpEntity(getHeaders());
        ResponseEntity<StatusDto[]> response = client.exchange(requestUrl, HttpMethod.GET, request, StatusDto[].class);
        return Arrays.asList(response.getBody());
    }

    public StatusDto getStatusById(UUID statusId) {
        StatusDto result = null;
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl + "/api/status/" + statusId + "/";
        HttpEntity request = new HttpEntity(getHeaders());
        try {
            ResponseEntity<StatusDto> response = client.exchange(requestUrl, HttpMethod.GET, request, StatusDto.class);
            result = response.getBody();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<MessageDto> getListOfMessagesForStatus(StatusDto statusDto) {
        Objects.requireNonNull(statusDto);
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl + "/api/status/" + statusDto.getId() + "/messages";
        HttpEntity request = new HttpEntity(getHeaders());
        ResponseEntity<MessageDto[]> response = client.exchange(requestUrl, HttpMethod.GET, request, MessageDto[].class);
        return new ArrayList<>(Arrays.asList(response.getBody()));
    }

    public List<String> getAllHashTags() {
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl + "/api/hashtags?prefix=&limit=1000";
        HttpEntity request = new HttpEntity(getHeaders());
        ResponseEntity<String[]> response = client.exchange(requestUrl, HttpMethod.GET, request, String[].class);
        return new ArrayList<>(Arrays.asList(response.getBody()));
    }

    public boolean sendMessage(StatusDto statusDto, MessageDto messageDto) {
        Objects.requireNonNull(messageDto);
        Objects.requireNonNull(statusDto);
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl + "/api/status/" + statusDto.getId() + "/messages";
        HttpEntity<MessageDto> request = new HttpEntity(messageDto, getHeaders());
        ResponseEntity response = client.exchange(requestUrl, HttpMethod.POST, request, Object.class, messageDto);
        return HttpStatus.OK.equals(response.getStatusCode());
    }

    private HttpHeaders getHeaders() {
        String token = SharedPrefHelper.getToken(null);
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", "bearer " + token);
        }
        return headers;
    }

    public ObjectMapper getObjectMapper() {
        return mapper;
    }

}
