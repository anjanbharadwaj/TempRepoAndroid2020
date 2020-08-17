package com.example.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;
import java.util.Map;

import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.AccountDetails;
import co.chatsdk.firebase.wrappers.UserWrapper;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static co.chatsdk.core.types.AccountDetails.username;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class ProfileFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    static FragmentActivity activity;

    static TextView name;
    static TextView bio;
    static TextView about;
    static TextView phone;
    static TextView email;
    static TextView location;
    static TextView language;
    static TextView school;
    static ImageButton editProfile;
    static ImageButton message;
    static ConstraintLayout phoneLayout;
    static ConstraintLayout emailLayout;
    static ConstraintLayout locationLayout;
    static ConstraintLayout languageLayout;
    static ConstraintLayout schoolLayout;
    static CircularImageView profilePic;
    static DatabaseReference profileRoot;
    static HashMap<String, Object> userMeta;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        profileRoot = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_profile_personal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        message = view.findViewById(R.id.message_button);
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ChatSDK.ui().startMainActivity(getContext());

                Class a = ChatSDK.ui().getMainActivity();
                startActivity(new Intent(getContext(), a));
            }
        });
        editProfile = view.findViewById(R.id.edit_or_add_button);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                userMeta = new HashMap<>(ChatSDK.currentUser().metaMap());
                Intent intent = new Intent(getActivity().getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });

        name = view.findViewById(R.id.name);
        about = view.findViewById(R.id.aboutLabel);
        bio = view.findViewById(R.id.bio);
        phone = view.findViewById(R.id.phoneValue);
        email = view.findViewById(R.id.emailValue);
        location = view.findViewById(R.id.locationValue);
        language = view.findViewById(R.id.languageValue);
        school = view.findViewById(R.id.schoolValue);


        phoneLayout = view.findViewById(R.id.phoneConstraint);
        emailLayout = view.findViewById(R.id.emailConstraint);
        locationLayout = view.findViewById(R.id.locationConstraint);
        languageLayout = view.findViewById(R.id.languageConstraint);
        schoolLayout = view.findViewById(R.id.schoolConstraint);
        profilePic = view.findViewById(R.id.profilePic);

        loadProfileInfo(getActivity().getApplicationContext(), FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
    }
    public static void chatInit(User u){

        ChatSDK.currentUser().setAvatarURL("https://image.shutterstock.com/image-photo/red-apple-on-white-background-260nw-158989157.jpg");

        DatabaseReference ref_chatprofile = FirebaseDatabase.getInstance().getReference().child("prod").child("users").child(FirebaseAuth.getInstance().getUid().toString()).child("meta");
        ref_chatprofile.child("name").setValue(u.name);
        ref_chatprofile.child("name-lowercase").setValue(u.name.toLowerCase());

        ChatSDK.currentUser().update();

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
//            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
//            loadProfileInfo(getContext(), uid);

        }

    }

    public static void loadProfileInfo(Context c, String uid){
        profileRoot.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                User u = null;
                User u = dataSnapshot.getValue(User.class);
                if(u.pfpUrl!=null) {
                    Log.e("pfpurl", u.pfpUrl);

                    GlideApp.with(c).load(u.pfpUrl).apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).circleCrop()).into(profilePic);
                } else {
                    GlideApp.with(c).load(R.drawable.profile_picture_basic).apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true).circleCrop()).into(profilePic);
                }
                String name = u.name;
                ProfileFragment.name.setText(name);

                String bio = null;
                try {
                    bio = u.bio;
                    if(!bio.isEmpty()) ProfileFragment.bio.setText(bio);
                } catch (Exception e) {
                    ProfileFragment.bio.setText("");
                }
                String phone = null;
                try {
                    phone = u.phone;
                    if(!phone.isEmpty()) {
                        ProfileFragment.phone.setText(phone);
                        ProfileFragment.phoneLayout.setVisibility(View.VISIBLE);
                    } else ProfileFragment.phoneLayout.setVisibility(View.GONE);

                } catch (Exception e) {
                    ProfileFragment.phoneLayout.setVisibility(View.GONE);
                }
                String email = null;
                try {
                    email = u.email;
                    if(!email.isEmpty()) {
                        ProfileFragment.email.setText(email);
                        ProfileFragment.emailLayout.setVisibility(View.VISIBLE);
                    } else ProfileFragment.emailLayout.setVisibility(View.GONE);
                } catch (Exception e) {
                    ProfileFragment.emailLayout.setVisibility(View.GONE);
                }
                String location = null;
                try {
                    location = u.location;
                    if(!location.isEmpty() && !location.equals("-1")) {
                        ProfileFragment.location.setText("From ");
                        SpannableString locationBold = new SpannableString(location);
                        locationBold.setSpan(new StyleSpan(Typeface.BOLD), 0, locationBold.length(), 0);

                        ProfileFragment.location.append(locationBold);
                        ProfileFragment.locationLayout.setVisibility(View.VISIBLE);
                    } else ProfileFragment.locationLayout.setVisibility(View.GONE);
                } catch (Exception e) {
                    ProfileFragment.locationLayout.setVisibility(View.GONE);
                }
                String language = null;
                try {
                    language = u.language;
                    if(!language.isEmpty()) {
                        ProfileFragment.language.setText("Speaks ");
                        SpannableString languageBold = new SpannableString(language);
                        languageBold.setSpan(new StyleSpan(Typeface.BOLD), 0, languageBold.length(), 0);

                        ProfileFragment.language.append(languageBold);
                        ProfileFragment.languageLayout.setVisibility(View.VISIBLE);
                    } else ProfileFragment.languageLayout.setVisibility(View.GONE);
                } catch (Exception e) {
                    ProfileFragment.languageLayout.setVisibility(View.GONE);
                }
                String school = null;
                try {
                    school = u.school;
                    if(!school.isEmpty() && !school.equals("-1")) {
                        getSchoolName(school);
                        ProfileFragment.schoolLayout.setVisibility(View.VISIBLE);
                    } else ProfileFragment.schoolLayout.setVisibility(View.GONE);
                } catch (Exception e) {
                    ProfileFragment.schoolLayout.setVisibility(View.GONE);
                }
//                if(userMeta!=null)




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
    public static String getName() { return "Profile"; }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


}
