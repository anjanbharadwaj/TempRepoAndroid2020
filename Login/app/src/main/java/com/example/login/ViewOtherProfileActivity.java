package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ViewOtherProfileActivity extends AppCompatActivity {
    static TextView name;
    static TextView bio;
    static TextView about;
    static TextView phone;
    static TextView email;
    static TextView location;
    static TextView language;
    static TextView school;

    static ConstraintLayout phoneLayout;
    static ConstraintLayout emailLayout;
    static ConstraintLayout locationLayout;
    static ConstraintLayout languageLayout;
    static ConstraintLayout schoolLayout;
    static CircularImageView profilePic;

    static DatabaseReference profileRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        ImageButton addFriend = findViewById(R.id.edit_or_add_button);
        addFriend.setImageResource(R.drawable.add_friend_icon);
        profileRoot = FirebaseDatabase.getInstance().getReference().child("Users");


        profilePic = findViewById(R.id.profilePic);

        name = findViewById(R.id.name);
        about = findViewById(R.id.aboutLabel);
        bio = findViewById(R.id.bio);
        phone = findViewById(R.id.phoneValue);
        email = findViewById(R.id.emailValue);
        location = findViewById(R.id.locationValue);
        language = findViewById(R.id.languageValue);
        school = findViewById(R.id.schoolValue);


        phoneLayout = findViewById(R.id.phoneConstraint);
        emailLayout = findViewById(R.id.emailConstraint);
        locationLayout = findViewById(R.id.locationConstraint);
        languageLayout = findViewById(R.id.languageConstraint);
        schoolLayout = findViewById(R.id.schoolConstraint);

        String uid = getIntent().getStringExtra("UID").toString();
        loadProfileInfo(getApplicationContext(), uid);
    }


    public static void loadProfileInfo(Context c, String uid){
        try {
            StorageReference reference = FirebaseStorage.getInstance().getReference().child("Users").child(uid);
            GlideApp.with(c).load(reference).apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).circleCrop()).into(profilePic);
        }
        catch(Exception e){
            Log.e("ProfInfo", "failed");
        }
        profileRoot.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("ProfInfo", "on data change");
                String name = dataSnapshot.child("name").getValue().toString();
                ViewOtherProfileActivity.name.setText(name);

                String bio = null;
                try {
                    bio = dataSnapshot.child("bio").getValue().toString();
                    ViewOtherProfileActivity.bio.setText(bio);
                } catch (Exception e) {
                    ViewOtherProfileActivity.bio.setText("");
                }
                String phone = null;
                try {
                    phone = dataSnapshot.child("phone").getValue().toString();
                    ViewOtherProfileActivity.phone.setText(phone);
                } catch (Exception e) {
                    ViewOtherProfileActivity.phoneLayout.setVisibility(View.GONE);
                }
                String email = null;
                try {
                    email = dataSnapshot.child("email").getValue().toString();
                    ViewOtherProfileActivity.email.setText(email);
                } catch (Exception e) {
                    ViewOtherProfileActivity.emailLayout.setVisibility(View.GONE);
                }
                String location = null;
                try {
                    location = dataSnapshot.child("location").getValue().toString();
                    ViewOtherProfileActivity.location.setText("From ");
                    SpannableString locationBold = new SpannableString(location);
                    locationBold.setSpan(new StyleSpan(Typeface.BOLD), 0, locationBold.length(), 0);

                    ViewOtherProfileActivity.location.append(locationBold);
                } catch (Exception e) {
                    ViewOtherProfileActivity.locationLayout.setVisibility(View.GONE);
                }
                String language = null;
                try {
                    language = dataSnapshot.child("language").getValue().toString();
                    ViewOtherProfileActivity.language.setText("Speaks ");
                    SpannableString languageBold =  new SpannableString(language);
                    languageBold.setSpan(new StyleSpan(Typeface.BOLD), 0, languageBold.length(), 0);

                    ViewOtherProfileActivity.language.append(languageBold);
                } catch (Exception e) {
                    ViewOtherProfileActivity.languageLayout.setVisibility(View.GONE);
                }
                String school = null;
                try {
                    school = dataSnapshot.child("school").getValue().toString();
                    getSchoolName(school);

                } catch (Exception e) {
                    ViewOtherProfileActivity.schoolLayout.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void getSchoolName(String school1){
        DatabaseReference schoolsRef = FirebaseDatabase.getInstance().getReference().child("Schools").child(school1);
        schoolsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                School s = dataSnapshot.getValue(School.class);
                String school = s.name;
                ProfileFragment.school.setText("Studies at ");
                SpannableString schoolBold =  new SpannableString(school);
                schoolBold.setSpan(new StyleSpan(Typeface.BOLD), 0, schoolBold.length(), 0);

                ProfileFragment.school.append(schoolBold);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
