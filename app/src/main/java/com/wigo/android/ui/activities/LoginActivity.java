package com.wigo.android.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.LoginResponseDto;
import com.wigo.android.core.server.requestapi.ServerRequestAdapter;
import com.wigo.android.ui.MainActivity;
import com.wigo.android.ui.base.BaseTextWatcher;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends Activity {

    TextView email = null;
    TextView pass = null;
    Button login = null;
    View loading = null;
    LoginButton loginButton = null;
    CallbackManager callbackManager = null;
    AccessTokenTracker accessTokenTracker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);


        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        if(!SharedPrefHelper.getToken("").isEmpty() &&
                !SharedPrefHelper.getEmail("").isEmpty() &&
                !SharedPrefHelper.getUserId("").isEmpty() &&
                !SharedPrefHelper.getUserName("").isEmpty()) {
            startMainActivity();
            return;
        }

        setContentView(R.layout.login_activity);
        email = (TextView) findViewById(R.id.login_activity_email);
        pass = (TextView) findViewById(R.id.login_activity_password);
        login = (Button) findViewById(R.id.login_activity_button);
        loading = findViewById(R.id.login_activity_loading_animation);

        login.setEnabled(false);
        email.setText(SharedPrefHelper.getEmail(""));

        email.addTextChangedListener(loginTextWatcher);
        pass.addTextChangedListener(loginTextWatcher);

        callbackManager = CallbackManager.Factory.create();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((TextView) findViewById(R.id.login_activity_email)).getText().toString();
                String password = ((TextView) findViewById(R.id.login_activity_password)).getText().toString();
                SharedPrefHelper.setEmail(email);
                loading.setVisibility(View.VISIBLE);
                setEnableAll(false);
                ServerRequestAdapter.singinRequest(email, password, loginListener, errorLoginListener);
            }
        });

        loginButton = (LoginButton) findViewById(R.id.login_facebook_button);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                SharedPrefHelper.setToken(loginResult.getAccessToken().getToken());
                Toast.makeText(ContextProvider.getAppContext(), "Logged in", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(ContextProvider.getAppContext(), "don't want", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(ContextProvider.getAppContext(), "error", Toast.LENGTH_LONG).show();
            }
        });

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                Toast.makeText(ContextProvider.getAppContext(), "TOKEN", Toast.LENGTH_LONG).show();
            }
        };
        // If the access token is available already assign it.
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        setTitle("Please login to BLABLABLA");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    BaseTextWatcher loginTextWatcher = new BaseTextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            login.setEnabled(!email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty());
        }
    };

    Response.Listener<LoginResponseDto> loginListener = new Response.Listener<LoginResponseDto>() {
        @Override
        public void onResponse(LoginResponseDto response) {
            String token = response.getData().getToken();
            if (token != null && !token.isEmpty()) {
                SharedPrefHelper.setToken(token);
                SharedPrefHelper.setEmail(email.getText().toString());
                SharedPrefHelper.setUserName(response.getData().getName());
                SharedPrefHelper.setUserId(response.getData().getUser_id());
                startMainActivity();
            } else {
                setEnableAll(true);
                requestError(response.getErrors());
            }
        }
    };

    Response.ErrorListener errorLoginListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            requestError(new String(error.networkResponse.data));
        }
    };

    private void requestError(String msg) {
        pass.setText("");

        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.setText(msg);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        setEnableAll(true);
        loading.setVisibility(View.GONE);
    }

    private void setEnableAll(boolean enable) {
        login.setEnabled(enable && !email.getText().toString().isEmpty() && !pass.getText().toString().isEmpty());
        email.setEnabled(enable);
        pass.setEnabled(enable);
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }

}
