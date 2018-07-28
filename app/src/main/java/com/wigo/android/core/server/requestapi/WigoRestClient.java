package com.wigo.android.core.server.requestapi;


import android.content.Intent;
import android.text.TextUtils;

import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.FaceBookUserInfoDto;
import com.wigo.android.core.server.dto.MessageDto;
import com.wigo.android.core.server.dto.StatusDto;
import com.wigo.android.core.server.dto.StatusSmallDto;
import com.wigo.android.core.server.dto.WigoUserInfoResponseDto;
import com.wigo.android.core.server.requestapi.errors.LoginError;
import com.wigo.android.core.server.requestapi.errors.RequestError;
import com.wigo.android.core.server.requestapi.errors.WigoException;
import com.wigo.android.core.utils.DateUtils;
import com.wigo.android.ui.activities.LoginActivity;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


/**
 * Created by AlexUA on 9/10/2016.
 */
public class WigoRestClient {

    public static final String CAN_NOT_CONNECT_TO_SERVER_TEXT = "Can not connect to server";

    private RestTemplate client;

    public WigoRestClient() {
        client = new RestTemplate();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(ContextProvider.getObjectMapper());
        client.getMessageConverters().add(0, converter);
    }

    public FaceBookUserInfoDto getUserInfoFromFacebook(String faceBookToken) throws WigoException {
        String requestUrl = "https://graph.facebook.com/v2.7/me?access_token=" + faceBookToken
                + "&fields=id,name,first_name,middle_name,last_name,email,link&format=json&sdk=android";
        HttpEntity request = new HttpEntity(getHeaders());
        try {
            ResponseEntity<FaceBookUserInfoDto> response = client.exchange(requestUrl, HttpMethod.GET, request, FaceBookUserInfoDto.class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                startLogicActvity();
                throw new LoginError();
            } else {
                throw new RequestError(e.getMessage());
            }
        } catch (ResourceAccessException e) {
            throw new RequestError(CAN_NOT_CONNECT_TO_SERVER_TEXT);
        }
    }

    public WigoUserInfoResponseDto getUserInfoFromWigo(String faceBookToken) throws WigoException {
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl + "/api/login";
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("fbToken", faceBookToken);
        HttpEntity<HashMap<String, String>> request = new HttpEntity(requestBody, getHeaders());
        try {
            ResponseEntity<WigoUserInfoResponseDto> response = client.exchange(requestUrl, HttpMethod.POST, request, WigoUserInfoResponseDto.class, request);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                startLogicActvity();
                throw new LoginError();
            } else {
                throw new RequestError(e.getMessage());
            }
        } catch (ResourceAccessException e) {
            throw new RequestError(CAN_NOT_CONNECT_TO_SERVER_TEXT);
        }
    }

    public List<StatusSmallDto> getStatusesListFromServer(double startLatitude, double endLatitude, double startLongitude, double endLongitude, List<String> tags, Set<String> categories, Calendar fromDate, Calendar toDate, String searchText) throws WigoException {
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl
                + "/api/status?startLatitude=" + Math.min(startLatitude, endLatitude)
                + "&endLatitude=" + Math.max(startLatitude, endLatitude)
                + "&startLongitude=" + Math.min(startLongitude, endLongitude)
                + "&endLongitude=" + Math.max(startLongitude, endLongitude)
                + "&startDate=" + DateUtils.calendarToString(fromDate)
                + "&endDate=" + DateUtils.calendarToString(toDate);
        if (searchText != null && !searchText.isEmpty()) {
            requestUrl += "&search=" + searchText;
        }
        if (!tags.isEmpty()) {
            requestUrl = requestUrl + "&hashtags=" + TextUtils.join(",", tags);
        }
        if (categories != null && !categories.isEmpty()) {
            requestUrl = requestUrl + "&categories=" + TextUtils.join(",", categories);
        }
        HttpEntity request = new HttpEntity(getHeaders());
        try {
            ResponseEntity<StatusSmallDto[]> response = client.exchange(requestUrl, HttpMethod.GET, request, StatusSmallDto[].class);
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                startLogicActvity();
                throw new LoginError();
            } else {
                throw new RequestError(e.getMessage());
            }
        } catch (ResourceAccessException e) {
            if(!(e.getCause() instanceof InterruptedIOException)){
                throw new RequestError(CAN_NOT_CONNECT_TO_SERVER_TEXT);
            }
        }
        return Collections.emptyList();
    }

    public StatusDto getStatusById(UUID statusId) throws WigoException {
        StatusDto result = null;
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl + "/api/status/" + statusId + "/";
        HttpEntity request = new HttpEntity(getHeaders());
        try {
            ResponseEntity<StatusDto> response = client.exchange(requestUrl, HttpMethod.GET, request, StatusDto.class);
            result = response.getBody();
            return result;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                startLogicActvity();
                throw new LoginError();
            } else {
                throw new RequestError(e.getMessage());
            }
        } catch (ResourceAccessException e) {
            throw new RequestError(CAN_NOT_CONNECT_TO_SERVER_TEXT);
        }

    }

    public List<MessageDto> getListOfMessagesForStatus(StatusDto statusDto, Calendar fromDate) throws WigoException {
        Objects.requireNonNull(statusDto);
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl + "/api/status/" + statusDto.getId() + "/messages"
                + "?from=" + DateUtils.calendarToString(fromDate);
        HttpEntity request = new HttpEntity(getHeaders());
        try {
            ResponseEntity<MessageDto[]> response = client.exchange(requestUrl, HttpMethod.GET, request, MessageDto[].class);
            return new ArrayList<>(Arrays.asList(response.getBody()));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                startLogicActvity();
                throw new LoginError();
            } else {
                throw new RequestError(e.getMessage());
            }
        } catch (ResourceAccessException e) {
            throw new RequestError(CAN_NOT_CONNECT_TO_SERVER_TEXT);
        }
    }

    public List<String> getAllHashTags() throws WigoException {
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl + "/api/hashtags?prefix=&limit=1000";
        HttpEntity request = new HttpEntity(getHeaders());
        try {
            ResponseEntity<String[]> response = client.exchange(requestUrl, HttpMethod.GET, request, String[].class);
            return new ArrayList<>(Arrays.asList(response.getBody()));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                startLogicActvity();
                throw new LoginError();
            } else {
                throw new RequestError(e.getMessage());
            }
        } catch (ResourceAccessException e) {
            throw new RequestError(CAN_NOT_CONNECT_TO_SERVER_TEXT);
        }
    }

    public MessageDto sendMessage(StatusDto statusDto, MessageDto messageDto) throws WigoException {
        Objects.requireNonNull(messageDto);
        Objects.requireNonNull(statusDto);
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl + "/api/status/" + statusDto.getId() + "/messages";
        HttpEntity<MessageDto> request = new HttpEntity(messageDto, getHeaders());
        ResponseEntity<MessageDto> response = null;
        try {
            response = client.exchange(requestUrl, HttpMethod.POST, request, MessageDto.class, messageDto);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                startLogicActvity();
                throw new LoginError();
            } else {
                throw new RequestError(e.getMessage());
            }
        } catch (ResourceAccessException e) {
            throw new RequestError(CAN_NOT_CONNECT_TO_SERVER_TEXT);
        }
    }

    public StatusDto createNewChat(StatusDto statusDto) throws WigoException {
        Objects.requireNonNull(statusDto);
//        statusDto.setCategory(StatusCategory.CHAT);
        String serverUrl = ContextProvider.getAppContext().getString(R.string.server_url);
        String requestUrl = serverUrl + "/api/status/";
        HttpEntity<StatusDto> request = new HttpEntity(statusDto, getHeaders());
        ResponseEntity<UUID> response = null;
        try {
            response = client.exchange(requestUrl, HttpMethod.POST, request, UUID.class, statusDto);
            statusDto.setId(response.getBody());
            return statusDto;
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                startLogicActvity();
                throw new LoginError();
            } else {
                throw new RequestError(e.getMessage());
            }
        } catch (ResourceAccessException e) {
            throw new RequestError(CAN_NOT_CONNECT_TO_SERVER_TEXT);
        }
    }

    private HttpHeaders getHeaders() {
        String token = SharedPrefHelper.getToken(null);
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", "bearer " + token);
        }
        return headers;
    }

    private void startLogicActvity() {
        Intent login = new Intent(ContextProvider.getAppContext(), LoginActivity.class);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextProvider.getAppContext().startActivity(login);
    }

}
