package com.example.login;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import java.util.ArrayList;

public class School implements SearchSuggestion {
    public String id;
    public String name;
    public String imageUri;
    public String location;
    public int raisedMoney;
    public int totalMoney;
    public String description;
    public String organizerID;


    public ArrayList<String> items;
    public School(){

    }
    public School(String id, String name, String imageUri, String location, int raisedMoney, int totalMoney, String description, String organizerID, ArrayList<String> items) {
        this.id = id;
        this.name = name;
        this.imageUri = imageUri;
        this.location = location;
        this.raisedMoney = raisedMoney;
        this.totalMoney = totalMoney;
        this.description = description;
        this.organizerID = organizerID;
        this.items = items;
        this.items.remove(0);
    }

    public School(Parcel parcel){
        this.id = parcel.readString();
        this.name = parcel.readString();
        this.imageUri = parcel.readString();
        this.location = parcel.readString();
        this.raisedMoney = parcel.readInt();
        this.totalMoney = parcel.readInt();
        this.description = parcel.readString();
        this.organizerID = parcel.readString();
        this.items = parcel.readArrayList(String.class.getClassLoader());//new ArrayList<>();
//        if(this.items!=null) this.items.remove(0);
    }
    public String toString() {

        return id + " " + name + " " + imageUri+ " " +location+ " " +raisedMoney+ " " +totalMoney+ " " +description+ " " +organizerID+ " " +items.toString() ;
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
        School s = (School) obj;

        return id.equals(s.id);
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
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(imageUri);
        dest.writeString(location);
        dest.writeInt(raisedMoney);
        dest.writeInt(totalMoney);
        dest.writeString(description);
        dest.writeString(organizerID);
        dest.writeList(items);//.writeStringList(items);
    }

    //Required field for the interface to be able to create a new School object from a passed in parcel

}
