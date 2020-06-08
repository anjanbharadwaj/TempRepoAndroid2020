package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class CustomMainActivityForChat extends co.chatsdk.ui.main.MainAppBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onBackPressed() {
        finish();
        // Fixes an issue where if we press back the whole app goes blank
    }
}
