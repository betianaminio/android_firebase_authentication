package com.betianaminio.firebase_authentication.providers.abstracts;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.betianaminio.firebase_authentication.providers.ProvidersConfiguration;

public abstract class SocialProvider {

    protected Activity mActivity;
    protected String  mProviderType;

    public abstract void initialize( Activity activity, View view);
    protected abstract void login();
    public abstract void logout();
    public abstract void onActivityResult( int request_code, int response_code, Intent data);

    public abstract boolean isLogged();


    public String getProviderType(){
        return this.mProviderType;
    };
}
