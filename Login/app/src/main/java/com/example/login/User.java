package com.example.login;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.ArrayList;
import java.util.Map;

public class User {

    public String name;
    public String bio;
    public String language;
    public String location;
    public String email;
    public String phone;
    public String school;
    public String pfpUrl;
    public Map<String, Object> friendUIDs;
    public String uid;

    public User(String name, String bio, String language, String location, String email, String phone, String school, String pfpUrl, Map<String, Object> friendUIDs) {
        this.name = name;
        this.bio = bio;
        this.language = language;
        this.location = location;
        this.email = email;
        this.phone = phone;
        this.school = school;
        this.pfpUrl = pfpUrl;
        this.friendUIDs = friendUIDs;
    }
    public User(User old){
        this.name = ""+old.name;
        this.bio = ""+old.bio;
        this.language = ""+old.language;
        this.location = ""+old.location;
        this.email = ""+old.email;
        this.phone = ""+old.phone;
        this.school = ""+old.school;
        this.pfpUrl = ""+old.pfpUrl;
        this.friendUIDs = old.friendUIDs;

    }
    public User(Parcel parcel){
        this.name = parcel.readString();
        this.bio = parcel.readString();
        this.language = parcel.readString();
        this.location = parcel.readString();
        this.email = parcel.readString();
        this.phone = parcel.readString();
        this.school = parcel.readString();
        this.pfpUrl = parcel.readString();
        this.friendUIDs = parcel.readHashMap(String.class.getClassLoader());//new ArrayList<>();
    }
    public User(){

    }
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", language='" + language + '\'' +
                ", location='" + location + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", school='" + school + '\'' +
                ", pfpUrl='" + pfpUrl + '\'' +
                ", friendUIDs='" + friendUIDs + '\'' +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        User u = (User) obj;

        return uid.equals(u.uid);

    }
}
