package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.palette.*;
import androidx.palette.graphics.Palette;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.klinker.android.sliding.SlidingActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class SchoolInfoActivity extends SlidingActivity{

    TextView detailDescription;
    TextView detailFundingLabel;
    ProgressBar detailFundingProgressBar;
    TextView detailFavoriteLabel2;
    RatingBar detailFavorite;
    TextView detailManager;
    TextView detailManagerEmail;
    TextView detailManagerPhone;
    ExpandedListView detailItemsListView;
    MapView mapView;
    GoogleMap map;

    ArrayList<String> items;
    ArrayAdapter<String> adapter;
    @Override
    public void init(Bundle savedInstanceState) {

        final School school = getIntent().getParcelableExtra("School");

        setTitle(school.name);

        byte[] byteArray = getIntent().getByteArrayExtra("Image");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap image = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);
        Bitmap original = image.copy(image.getConfig(), true);
        /*the following lines of code alters the color scheme of the top bar of the app and the buttons in our app
          to match the book cover's color
         */
        image = darkenBitMap(image);
        image = blur(image);
        Log.e("IMG", image.toString());
        Log.e("IMG",""+image.getByteCount());
        setImage(image);
        Palette p = Palette.from(original).generate();
        int def = 0xffffff;
        final int primaryColor = p.getDominantColor(def);
        int primaryColorDark = manipulateColor(primaryColor, 0.5f);

        if (primaryColor != def) {
            setPrimaryColors(primaryColor, primaryColorDark);
        }

        setContent(R.layout.activity_school_info);

        detailDescription = findViewById(R.id.detailDescription);
        detailFundingLabel = findViewById(R.id.detailFundingLabel);
        detailFundingProgressBar = findViewById(R.id.detailFundingProgressBar);
        detailFavoriteLabel2 = findViewById(R.id.detailFavoriteLabel2);
        detailFavorite = findViewById(R.id.detailFavorite);
        detailManager = findViewById(R.id.detailManager);
        detailManagerEmail = findViewById(R.id.detailManagerEmail);
        detailManagerPhone = findViewById(R.id.detailManagerPhone);
        detailItemsListView = findViewById(R.id.detailItemsListView);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference favoritesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Favorites");
        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("UID", uid);

                Iterator i = dataSnapshot.getChildren().iterator();
                while(i.hasNext()){
                    String key = ((DataSnapshot)i.next()).getKey().toString();
                    Log.e("KEY", key);
                    Log.e("ID", school.id);

                    if(key.equals(school.id)){
                        detailFavorite.setRating(1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        detailFavorite.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(rating==1){
                    detailFavoriteLabel2.setText("This school will show up on your favorites list.");
                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Favorites").child(school.id).setValue("");
                }
                else{
                    detailFavoriteLabel2.setText("This school is not yet on your favorites list.");
                    FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Favorites").child(school.id).removeValue();
                }
            }
        });
        detailManagerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailaddress = detailManagerEmail.getText().toString();
                if(emailaddress.length() > 0) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", emailaddress, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailaddress}); // String[] addresses

                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            }
        });
        detailManagerPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                String phonenum = detailManagerPhone.getText().toString();
                if(phonenum.length()>0) {
                    intent.setData(Uri.parse("tel:" +phonenum));
                    startActivity(intent);
                }
            }
        });
        detailDescription.setText(school.description);
        detailFundingLabel.setText("$"+school.raisedMoney+" of $"+school.totalMoney);
        detailFundingProgressBar.setMax(school.totalMoney);
        detailFundingProgressBar.setProgress(school.raisedMoney);

        DatabaseReference managerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(school.organizerID);
        managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String managername = dataSnapshot.child("name").getValue().toString();
                String manageremail = dataSnapshot.child("email").getValue().toString();
                String managerphone = dataSnapshot.child("phone").getValue().toString();
                int showpublic = Integer.parseInt(dataSnapshot.child("ShowPublic").getValue().toString());
                detailManagerEmail.setText("");
                detailManagerPhone.setText("");

                switch(showpublic) {
                    case 0:
                        detailManager.setText(managername);
                        break;
                    case 1:
                        detailManager.setText(managername);
                        detailManagerEmail.setText(manageremail);
                        break;
                    case 2:
                        detailManager.setText(managername);
                        detailManagerPhone.setText(managerphone);
                        break;
                    case 3:
                        detailManager.setText(managername);
                        detailManagerEmail.setText(manageremail);
                        detailManagerPhone.setText(managerphone);
                        break;
                    default:
                        detailManager.setText(managername);
                        break;
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        items = school.items;
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        detailItemsListView.setAdapter(adapter);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.i("DEBUG", "onMapReady");

                double latitude = Double.parseDouble(school.location.split(",")[0]);
                double longitude = Double.parseDouble(school.location.split(",")[1].substring(1));


                Geocoder geocoder = new Geocoder(SchoolListActivity.c, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String cityName = addresses.get(0).getLocality();
                String countryName = addresses.get(0).getCountryName();

                String markerText = cityName + ", " + countryName;
                LatLng position = new LatLng(latitude, longitude);
                Marker marker  = googleMap.addMarker(new MarkerOptions().position(position).title(markerText));

                //zoom to position with level 16
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, 16);
                googleMap.animateCamera(cameraUpdate);


            }
        });


    }


    //this is a function that blurs an image to create a more aesthetic look/portray the cover as a background, not foreground
    public Bitmap blur(Bitmap image) {
        if (image == null) return null;

        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(this);
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

        //Use the Intrinsic Gausian blur filter on the entire image, and return the editted bitmap
        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        intrinsicBlur.setRadius(1);
        intrinsicBlur.setInput(tmpIn);
        intrinsicBlur.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    //this is a function that darkens an image to create a more aesthetic look/portray the cover as a background, not foreground
    private Bitmap darkenBitMap(Bitmap bm) {

        Canvas canvas = new Canvas(bm);
        //The Color.RED value and 0xFF7F7F7F value are used to create a dark filter
        Paint p = new Paint(Color.RED);
        ColorFilter filter = new LightingColorFilter(0xFF7F7F7F, 0x00000000);    // darken
        p.setColorFilter(filter);
        canvas.drawBitmap(bm, new Matrix(), p);

        return bm;
    }

    //This method changes the rgb values by a scalar to either darken/lighten the image
    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));

    }

}
