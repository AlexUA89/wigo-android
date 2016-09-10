package com.wigo.android.core.server.requestapi;

import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by AlexUA on 9/10/2016.
 */
public class WigoRestClient {

    private AsyncHttpClient client = new AsyncHttpClient();

    public void setToken(String token) {
        client.addHeader("token", token);
    }

    public void getUserInfoFromFacebook(String faceBookToken, AbstractWigoResponseHandler responseHandler) {
        client.get("https://graph.facebook.com/v2.7/me?access_token=" + faceBookToken
                + "&fields=id%2Cname%2Cfirst_name%2Cmiddle_name%2Clast_name%2Cemail%2Clink&format=json&sdk=android", responseHandler);
    }

}
