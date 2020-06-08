package com.example.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import co.chatsdk.core.utils.Strings;
import co.chatsdk.ui.chat.ChatActivity;
import co.chatsdk.ui.threads.ThreadImageBuilder;

public class CustomChatActivity extends ChatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_chat);
    }

    @Override
    protected void reloadActionBar () {
        String displayName = "";//Strings.nameForThread(thread);

        setTitle(displayName);
        titleTextView.setText(displayName);
        ThreadImageBuilder.load(threadImageView, thread);
    }
}
