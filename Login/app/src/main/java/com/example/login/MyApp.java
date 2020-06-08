package com.example.login;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.Configuration;
import co.chatsdk.firebase.FirebaseNetworkAdapter;
import co.chatsdk.firebase.file_storage.FirebaseFileStorageModule;
import co.chatsdk.firebase.push.FirebasePushHandler;
import co.chatsdk.firebase.push.FirebasePushModule;
import co.chatsdk.ui.manager.BaseInterfaceAdapter;
import io.reactivex.plugins.RxJavaPlugins;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        Context context = getApplicationContext();

        try {
            // Create a new configuration
            Configuration.Builder builder = new Configuration.Builder();

            // Perform any other configuration steps (optional)
            builder.firebaseRootPath("prod");
            // Initialize the Chat SDK
            ChatSDK.initialize(context, builder.build(), FirebaseNetworkAdapter.class, BaseInterfaceAdapter.class);
            RxJavaPlugins.setErrorHandler(throwable -> {
                throwable.printStackTrace();
            });
            // File storage is needed for profile image upload and image messages
            FirebaseFileStorageModule.activate();
            builder.publicRoomCreationEnabled(true);

            FirebasePushModule.activate();
            ChatSDK.ui().setMainActivity(CustomMainActivityForChat.class);
//            Log.e("MainActivity", ChatSDK.ui().getMainActivity().getName().toString());
            // Activate any other modules you need.
            // ...
        } catch (Exception e) {
            // Handle any exceptions
            e.printStackTrace();
        }
        ChatSDK.ui().removeTab(3);
        ChatSDK.ui().removeTab(2);



    }

}
