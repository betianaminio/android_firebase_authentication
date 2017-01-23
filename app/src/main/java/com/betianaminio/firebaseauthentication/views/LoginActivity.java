package com.betianaminio.firebase_authentication.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.betianaminio.firebase_authentication.auth.AuthenticationManager;
import com.betianaminio.firebase_authentication.auth.ILoginListener;
import com.betianaminio.firebase_authentication.R;
import com.betianaminio.firebase_authentication.providers.ProvidersConfiguration;
import com.facebook.FacebookSdk;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class LoginActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeSDK();
        setContentView(R.layout.activity_login);

        AuthenticationManager.getInstance().create(this,new ILoginListener() {
            @Override
            public void onLoginSuccess() {

                startActivity( new Intent( LoginActivity.this, ProfileActivity.class));
                finish();
            }

            @Override
            public void onLoginFailed(String error) {

                Toast.makeText(LoginActivity.this,error,Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        AuthenticationManager.getInstance().addListener();
    }

    @Override
    public void onStop() {
        super.onStop();

        AuthenticationManager.getInstance().removeListener();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();


    }

    @Override
    public void onActivityResult(int request_code, int result_code, Intent data ){
        super.onActivityResult(request_code,result_code,data);

        AuthenticationManager.getInstance().onActivityResult(request_code,result_code,data);

    }

    private void initializeSDK(){

        TwitterAuthConfig authConfig = new TwitterAuthConfig(ProvidersConfiguration.TWITTER_KEY, ProvidersConfiguration.TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        FacebookSdk.sdkInitialize(getApplicationContext());

    }
}
