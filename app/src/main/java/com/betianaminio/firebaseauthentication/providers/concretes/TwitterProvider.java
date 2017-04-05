package com.betianaminio.firebase_authentication.providers.concretes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.betianaminio.firebase_authentication.auth.AuthenticationManager;
import com.betianaminio.firebase_authentication.providers.ProvidersConfiguration;
import com.betianaminio.firebase_authentication.providers.abstracts.SocialProvider;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import static com.twitter.sdk.android.Twitter.getSessionManager;

public class TwitterProvider extends SocialProvider {

    private final String TWITTER_ACCESS_TOKEN        = "";
    private final String TWITTER_ACCESS_TOKEN_SECRET = "";

    private TwitterLoginButton mLoginButton;

    @Override
    public void initialize(Activity activity, View view) {

        this.mLoginButton = (TwitterLoginButton)view;
        this.mActivity    = activity;
        this.mProviderType = ProvidersConfiguration.PROVIDER_TYPE_TWITTER;

        login();
    }

    @Override
    protected void login() {

        this.mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {

                requestForEmail();
            }

            @Override
            public void failure(TwitterException exception) {
                String message = "Twitter: " + exception.getMessage();
                AuthenticationManager.getInstance().failedToAuthenticate(message);
            }
        });
    }

    @Override
    public void logout() {
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if (twitterSession != null) {
            ClearCookies(this.mActivity.getApplicationContext());
            Twitter.getSessionManager().clearActiveSession();
            Twitter.logOut();
        }
    }

    @Override
    public void onActivityResult(int request_code, int response_code, Intent data) {

        this.mLoginButton.onActivityResult(request_code,response_code,data);
    }

    @Override
    public boolean isLogged() {
        return false;
    }

    private void requestForEmail(){

        TwitterAuthClient authClient = new TwitterAuthClient();

        TwitterSession session = getSessionManager().getActiveSession();

        authClient.requestEmail(session, new Callback<String>() {
            @Override
            public void success(Result<String> result) {

                AuthenticationManager.getInstance().setCurrentProvider(TwitterProvider.this);

                AuthCredential credential = TwitterAuthProvider.getCredential(TWITTER_ACCESS_TOKEN,TWITTER_ACCESS_TOKEN_SECRET);
                AuthenticationManager.getInstance().startAuthentication(credential,TwitterProvider.this.mActivity);

            }

            @Override
            public void failure(TwitterException exception) {

                String message = "Twitter: " + exception.getMessage();
                AuthenticationManager.getInstance().failedToAuthenticate(message);

            }
        });

    }

    public  void ClearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else  {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}
