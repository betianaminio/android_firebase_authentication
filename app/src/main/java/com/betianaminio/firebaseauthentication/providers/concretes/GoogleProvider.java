package com.betianaminio.firebase_authentication.providers.concretes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.betianaminio.firebase_authentication.R;
import com.betianaminio.firebase_authentication.auth.AuthenticationManager;
import com.betianaminio.firebase_authentication.providers.ProvidersConfiguration;
import com.betianaminio.firebase_authentication.providers.abstracts.SocialProvider;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;


public class GoogleProvider extends SocialProvider {

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void initialize(Activity activity, View view) {

        this.mActivity = activity;
        this.mProviderType = ProvidersConfiguration.PROVIDER_TYPE_GOOGLE;

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(YOUR_DEFAULT_WEB_CLIENT_ID)
                .requestEmail()
                .build();

        this.mGoogleApiClient = new GoogleApiClient.Builder(this.mActivity)
                .addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();
            }
        });

    }

    @Override
    protected void login() {

        Intent loginIntent = Auth.GoogleSignInApi.getSignInIntent(this.mGoogleApiClient);
        this.mActivity.startActivityForResult( loginIntent, RC_SIGN_IN);

    }

    @Override
    public void logout() {

        this.mGoogleApiClient.connect();

        this.mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                Auth.GoogleSignInApi.signOut(GoogleProvider.this.mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {

                    }
                });

            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        });


    }

    @Override
    public void onActivityResult(int request_code, int response_code, Intent data) {

        if ( request_code == RC_SIGN_IN){

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if ( result.isSuccess()){

                GoogleSignInAccount account = result.getSignInAccount();

                AuthenticationManager.getInstance().setCurrentProvider(this);


                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);

                AuthenticationManager.getInstance().startAuthentication(credential,this.mActivity);

            }else{

                String message = "Google: " + result.getStatus().getStatusMessage();
                AuthenticationManager.getInstance().failedToAuthenticate(message);

            }

        }

    }

    @Override
    public boolean isLogged() {
        return false;
    }

}
