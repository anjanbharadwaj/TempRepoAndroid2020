package com.example.login;

import android.os.Parcel;
import android.os.Parcelable;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.Map;

public class UserSearchable extends User implements SearchSuggestion {

    public UserSearchable (User user){
        this.name = ""+user.name;
        this.bio = ""+user.bio;
        this.language = ""+user.language;
        this.location = ""+user.location;
        this.email = ""+user.email;
        this.phone = ""+user.phone;
        this.school = ""+user.school;
        this.pfpUrl = ""+user.pfpUrl;
        this.friendUIDs = user.friendUIDs;
        this.uid = user.uid;
        this.usertype = user.usertype;
    }
    public UserSearchable(Parcel parcel){
        this.name = parcel.readString();
        this.bio = parcel.readString();
        this.language = parcel.readString();
        this.location = parcel.readString();
        this.email = parcel.readString();
        this.phone = parcel.readString();
        this.school = parcel.readString();
        this.pfpUrl = parcel.readString();
        this.friendUIDs = parcel.readHashMap(String.class.getClassLoader());//new ArrayList<>();
        this.uid = parcel.readString();
        this.usertype = parcel.readString();
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
        dest.writeString(pfpUrl);
        dest.writeMap(friendUIDs);
        dest.writeString(uid);
        dest.writeString(usertype);

    }
    @Override
    public String toString() {
        return "UserSearchable{" +
                "name='" + name + '\'' +
                ", bio='" + bio + '\'' +
                ", language='" + language + '\'' +
                ", location='" + location + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", school='" + school + '\'' +
                ", pfpUrl='" + pfpUrl + '\'' +
                ", friendUIDs='" + friendUIDs + '\'' +
                ", uid='" + uid + '\'' +
                ", uid='" + usertype +
                '}';
    }

    public static final Parcelable.Creator<UserSearchable> CREATOR = new Parcelable.Creator<UserSearchable>() {

        @Override
        public UserSearchable createFromParcel(Parcel parcel) {
            return new UserSearchable(parcel);
        }

        @Override
        public UserSearchable[] newArray(int size) {
            return new UserSearchable[0];
        }
    };

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) return false;
        User u = (User) obj;

        return uid.equals(u.uid);
    }


}
