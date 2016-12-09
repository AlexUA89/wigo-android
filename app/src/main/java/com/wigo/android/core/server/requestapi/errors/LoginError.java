package com.wigo.android.core.server.requestapi.errors;

/**
 * Created by AlexUA89 on 12/9/2016.
 */

public class LoginError extends WigoException {

    private static final String NEED_LOGIN = "You didn't login";

    public LoginError() {
        super(NEED_LOGIN);
    }

}
