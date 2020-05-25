package com.example.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.ArrayList;

public class User implements SearchSuggestion {

    String UID;
    String name;
    String bio;
    String language;
    String location;
    String email;
    String phone;
    String school;
    ArrayList<String> friendUIDs;
    boolean hasFriends = true;
    public User(String name, String bio, String language, String location, String email, String phone, String school, ArrayList<String> friendUIDs) {
        this.name = name;
        this.bio = bio;
        this.language = language;
        this.location = location;
        this.email = email;
        this.phone = phone;
        this.school = school;
        this.friendUIDs = friendUIDs;
        if(friendUIDs==null) hasFriends = false;
    }

    public User(){
        hasFriends = false;
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
                ", friendUIDs=" + friendUIDs +
                '}';
    }


    @Override
    public String getBody() {
        return name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(bio);
        dest.writeString(language);
        dest.writeString(location);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(school);
        dest.writeStringList(friendUIDs);
    }

    public static final Parcelable.Creator<School> CREATOR = new Parcelable.Creator<School>() {

        @Override
        public School createFromParcel(Parcel parcel) {
            return new School(parcel);
        }

        @Override
        public School[] newArray(int size) {
            return new School[0];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        User u = (User) obj;

        return UID.equals(u.UID);
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }
}
