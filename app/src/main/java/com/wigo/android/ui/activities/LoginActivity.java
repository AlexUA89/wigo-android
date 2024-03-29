package com.wigo.android.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.wigo.android.R;
import com.wigo.android.core.ContextProvider;
import com.wigo.android.core.preferences.SharedPrefHelper;
import com.wigo.android.core.server.dto.WigoUserInfoResponseDto;
import com.wigo.android.core.server.requestapi.errors.WigoException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends Activity {

    View loading = null;
    LoginButton loginButton = null;
    CallbackManager callbackManager = null;
    AccessTokenTracker accessTokenTracker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getResources().getString(R.string.login_activity_title));
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.wigo.android", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }

        if (accessToken != null) {
            fillUserInfoAndGoNextActivity(accessToken.getToken());
            return;
        }

        setContentView(R.layout.login_activity);
        loading = findViewById(R.id.login_activity_loading_animation);

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_facebook_button);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fillUserInfoAndGoNextActivity(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(ContextProvider.getAppContext(), "Canceled login", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(ContextProvider.getAppContext(), "Login error", Toast.LENGTH_LONG).show();
            }
        });

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
//                Toast.makeText(ContextProvider.getAppContext(), "TOKEN", Toast.LENGTH_LONG).show();
                System.out.println("TOKEN IS " + currentAccessToken);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (accessTokenTracker != null) {
            accessTokenTracker.stopTracking();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,
                resultCode, data);
    }

    private void fillUserInfoAndGoNextActivity(final String token) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (token != null) {
                    WigoUserInfoResponseDto wigoUserInfoResponseDto = null;
                    try {
                        wigoUserInfoResponseDto = ContextProvider.getWigoRestClient().getUserInfoFromWigo(token);
                        SharedPrefHelper.setUserName(wigoUserInfoResponseDto.getUser().getName());
                        SharedPrefHelper.setUserNickName(wigoUserInfoResponseDto.getUser().getNickname());
                        SharedPrefHelper.setUserId(wigoUserInfoResponseDto.getUser().getId().toString());
                        SharedPrefHelper.setToken(wigoUserInfoResponseDto.getToken());
                        finish();
                    } catch (final WigoException requestError) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ContextProvider.getAppContext(), "Can not connect to server: " + requestError.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                        finish();
                    }
                }
            }
        }).start();
    }

}
