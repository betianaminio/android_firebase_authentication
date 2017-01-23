package com.betianaminio.firebase_authentication.auth;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.betianaminio.firebase_authentication.R;
import com.betianaminio.firebase_authentication.providers.concretes.FacebookProvider;
import com.betianaminio.firebase_authentication.providers.concretes.GoogleProvider;
import com.betianaminio.firebase_authentication.providers.abstracts.SocialProvider;
import com.betianaminio.firebase_authentication.providers.concretes.TwitterProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AuthenticationManager {

    private static AuthenticationManager s_instance;

    public static synchronized AuthenticationManager getInstance( ){

        if ( s_instance == null)
            s_instance = new AuthenticationManager();

        return s_instance;
    }

    public static void destroy(){

        s_instance         = null;
    }

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ILoginListener mLoginListener;

    private ArrayList<SocialProvider> mAvailableProviders;
    private SocialProvider mCurrentProvider;


    private AuthenticationManager(){

    }

    public void create( Activity activity, ILoginListener listener){

        this.mLoginListener = listener;

        this.mAuth = FirebaseAuth.getInstance();

        this.mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if ( firebaseAuth.getCurrentUser() != null )
                    Log.d("FB Auth","user id is: " + firebaseAuth.getCurrentUser().getDisplayName());

            }
        };

        createProviders(activity);

    }

    private void createProviders( Activity activity){

        this.mAvailableProviders = new ArrayList<SocialProvider>();

        FacebookProvider facebookProvider = new FacebookProvider();
        facebookProvider.initialize( activity, activity.findViewById(R.id.facebook_login_button));

        this.mAvailableProviders.add( facebookProvider );

        TwitterProvider twitterProvider = new TwitterProvider();
        twitterProvider.initialize(activity,activity.findViewById(R.id.twitter_login_button));

        this.mAvailableProviders.add(twitterProvider);

        GoogleProvider googleProvider = new GoogleProvider();
        googleProvider.initialize(activity,activity.findViewById(R.id.google_login_button));

        this.mAvailableProviders.add(googleProvider);
    }

    public void addListener(){

        this.mAuth.addAuthStateListener(this.mAuthListener);
    }

    public void removeListener(){

        if ( this.mAuthListener != null )
            this.mAuth.removeAuthStateListener(this.mAuthListener);
    }

    public void startAuthentication(AuthCredential credential, Activity activity){

        this.mAuth.signInWithCredential( credential ).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if ( task.isSuccessful() ){

                    AuthenticationManager.this.mLoginListener.onLoginSuccess();

                }
                else
                    AuthenticationManager.this.failedToAuthenticate(task.getException().getMessage());

            }
        });

    }

    public void failedToAuthenticate(String error_message ){

            this.mLoginListener.onLoginFailed( error_message );
    }

    public void onActivityResult(int request_code, int result_code, Intent data){

        for ( SocialProvider provider : this.mAvailableProviders) {

            if ( provider != null )
                provider.onActivityResult(request_code,result_code,data);

        }

    }

    public void setCurrentProvider( SocialProvider provider){

        this.mCurrentProvider = provider;
    }

    public SocialProvider getCurrentProvider(){
        return this.mCurrentProvider;
    }

    public FirebaseUser getCurrentUser(){

        return this.mAuth.getCurrentUser();
    }

    public void signOut(){

        this.mAuth.signOut();

        this.mCurrentProvider.logout();

    }
}
