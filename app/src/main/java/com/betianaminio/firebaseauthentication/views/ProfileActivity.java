package com.betianaminio.firebase_authentication.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.betianaminio.firebase_authentication.auth.AuthenticationManager;
import com.betianaminio.firebase_authentication.R;
import com.bumptech.glide.Glide;

public class ProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageView profile_image    = (ImageView)findViewById(R.id.img_user);
        TextView  profile_name     = (TextView)findViewById(R.id.text_view_user_name);
        TextView  profile_email    = (TextView)findViewById(R.id.text_view_email);
        TextView  profile_provider = (TextView)findViewById(R.id.text_view_provider);

        Button    btn_sign_out  = (Button)findViewById(R.id.button_sign_out);

        Glide.with(this).load(AuthenticationManager.getInstance().getCurrentUser().getPhotoUrl()).into(profile_image);

        profile_name.setText( AuthenticationManager.getInstance().getCurrentUser().getDisplayName());

        profile_email.setText( AuthenticationManager.getInstance().getCurrentUser().getEmail());

        profile_provider.setText( AuthenticationManager.getInstance().getCurrentUser().getProviders().get(0));

        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

					signOut();
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

    }
	
	@Override
    public void onBackPressed(){

        signOut();
    }
	
	private void signOut(){

        AuthenticationManager.getInstance().signOut();

        startActivity(new Intent( ProfileActivity.this, LoginActivity.class));
		ProfileActivity.this.finish();
    }
}
