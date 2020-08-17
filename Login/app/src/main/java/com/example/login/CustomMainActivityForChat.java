package com.example.login;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class CustomMainActivityForChat extends co.chatsdk.ui.main.MainAppBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void onBackPressed() {
        Log.e("custom back", "success");
        startActivity(new Intent(CustomMainActivityForChat.this, HomeActivity.class));
        finish();
        // Fixes an issue where if we press back the whole app goes blank
    }
}
