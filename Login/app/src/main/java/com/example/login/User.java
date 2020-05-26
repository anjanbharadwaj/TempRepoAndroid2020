package com.example.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.ArrayList;
import java.util.Map;

public class User implements SearchSuggestion {

    public String uid;
    public String name;
    public String bio;
    public String language;
    public String location;
    public String email;
    public String phone;
    public String school;
    public Map<String, Object> friendUIDs;

    public User(String name, String bio, String language, String location, String email, String phone, String school, Map<String, Object> friendUIDs) {
        this.name = name;
        this.bio = bio;
        this.language = language;
        this.location = location;
        this.email = email;
        this.phone = phone;
        this.school = school;
        this.friendUIDs = friendUIDs;
    }

    public User(Parcel parcel){
        this.name = parcel.readString();
        this.bio = parcel.readString();
        this.language = parcel.readString();
        this.location = parcel.readString();
        this.email = parcel.readString();
        this.phone = parcel.readString();
        this.school = parcel.readString();

        this.friendUIDs = parcel.readHashMap(String.class.getClassLoader());
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
        dest.writeMap(friendUIDs);
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

        return uid.equals(u.uid);
    }

    public String getuid() {
        return uid;
    }

    public void setuid(String UID) {
        this.uid = UID;
    }
}
