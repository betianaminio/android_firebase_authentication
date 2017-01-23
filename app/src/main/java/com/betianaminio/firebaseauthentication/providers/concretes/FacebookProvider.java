package com.betianaminio.firebase_authentication.providers.concretes;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.betianaminio.firebase_authentication.auth.AuthenticationManager;
import com.betianaminio.firebase_authentication.providers.ProvidersConfiguration;
import com.betianaminio.firebase_authentication.providers.abstracts.SocialProvider;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;


public class FacebookProvider extends SocialProvider {

    private LoginButton mLoginButton;

    private LoginManager mLoginManager;

    private CallbackManager mCallabackManager;

    @Override
    public void initialize(Activity activity, View view) {

        this.mActivity = activity;

        this.mCallabackManager = CallbackManager.Factory.create();

        this.mLoginButton = (LoginButton)view;
        this.mLoginButton.setReadPermissions("email");

        this.mProviderType = ProvidersConfiguration.eProvidersType.TYPE_FACEBOOK;

        this.mLoginManager = LoginManager.getInstance();

        login();
    }

    @Override
    protected void login() {

        this.mLoginButton.registerCallback(this.mCallabackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                AuthenticationManager.getInstance().setCurrentProvider(FacebookProvider.this);


                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                AuthenticationManager.getInstance().startAuthentication(credential,FacebookProvider.this.mActivity);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

                String message = "Facebook - " + error.getMessage();
                AuthenticationManager.getInstance().failedToAuthenticate( message );
            }
        });
    }

    @Override
    public void logout() {

       this.mLoginManager.logOut();

    }

    @Override
    public void onActivityResult(int request_code, int response_code, Intent data) {

        this.mCallabackManager.onActivityResult(request_code,response_code,data);
    }

    @Override
    public boolean isLogged() {
        return false;
    }

}
