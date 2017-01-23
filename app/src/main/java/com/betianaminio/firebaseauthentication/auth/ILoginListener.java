package com.betianaminio.firebase_authentication.auth;

public interface ILoginListener {

    void onLoginSuccess();
    void onLoginFailed(String error);
}
