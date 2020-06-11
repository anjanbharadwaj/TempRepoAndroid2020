package com.example.login;

import androidx.annotation.NonNull;

import android.content.Context;
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
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.palette.graphics.Palette;
import android.os.Vibrator;


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
import java.util.List;
import java.util.Locale;

import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.form.Check;
import eltos.simpledialogfragment.form.Input;
import eltos.simpledialogfragment.form.SimpleFormDialog;
import eltos.simpledialogfragment.input.SimpleInputDialog;

public class SchoolInfoActivity extends SlidingActivity implements SimpleDialog.OnDialogResultListener, SimpleInputDialog.InputValidator {
    final String DONATION = "donate";
    boolean donateClicked = false;
    TextView detailDescription;
    TextView detailFundingLabel;
    ProgressBar detailFundingProgressBar;
    Button detailDonate;
    TextView detailManager;
    CardView detailManagerCardView;
    TextView detailManagerName;
    ExpandedListView detailItemsListView;
    MapView mapView;
    GoogleMap map;

    ArrayList<String> items;
    ArrayAdapter<String> adapter;

    School school;
    @Override
    public void init(Bundle savedInstanceState) {

        school = getIntent().getParcelableExtra("School");

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
        detailManagerName = findViewById(R.id.detailManagerName);
        detailDescription = findViewById(R.id.nameEditText);
        detailFundingLabel = findViewById(R.id.detailFundingLabel);
        detailFundingProgressBar = findViewById(R.id.detailFundingProgressBar);
        detailManager = findViewById(R.id.detailManager);
        detailDonate = findViewById(R.id.donateButton);
        detailManagerCardView = findViewById(R.id.detailManagerCardView);
        detailItemsListView = findViewById(R.id.detailItemsListView);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        detailManagerCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(school.organizerID.equals(uid)){
                    Toast.makeText(getApplicationContext(), "You're trying to visit your own profile!", Toast.LENGTH_LONG).show();
                    Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vib.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vib.vibrate(500);
                    }

                } else {
                    Intent intent = new Intent(getApplicationContext(), ViewOtherProfileActivity.class);
                    intent.putExtra("UID", school.organizerID);
                    startActivity(intent);
                }
            }
        });
        detailDescription.setText(school.description);
        detailFundingLabel.setText("$"+school.raisedMoney+" of $"+school.totalMoney);
        detailFundingProgressBar.setMax((int)school.totalMoney);
        detailFundingProgressBar.setProgress((int)school.raisedMoney);

        DatabaseReference managerRef = FirebaseDatabase.getInstance().getReference().child("Users").child(school.organizerID);
        managerRef.keepSynced(true);
        managerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("Init1",dataSnapshot.toString());
                String managername = dataSnapshot.child("name").getValue().toString();
                detailManagerName.setText(managername);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        items = school.items;
        Log.e("itemsinschoolinfo", items.toString());
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        detailItemsListView.setAdapter(adapter);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.i("DEBUG", "onMapReady");

                double latitude = Double.parseDouble(school.location.split(",")[0]);
                double longitude = Double.parseDouble(school.location.split(",")[1].substring(1));


                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
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

//        detailDonate.setBackgroundColor(primaryColor);
        detailDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donateClicked = true;
                String url = "https://www.gofundme.com/f/temporary-do-not-donate-please?utm_source=customer&utm_medium=copy_link&utm_campaign=p_cf+share-flow-1";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
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
        if(donateClicked){
            donateClicked=false;

            SimpleDialog.build().title("Thanks!").msg("Thanks for donating! We appreciate it :)").show(this, DONATION);

//            SimpleFormDialog.build()
//                    .title("Donation")
//                    .msg("Please enter your donation amount so that we can keep the OneSharedSchool platform up-to-date!")
//                    .fields(
//                            Input.plain("Donation Amount").max(5).required().validatePattern("/^(0|[1-9]\\d*)(\\.\\d+)?$/\n", "Needs to be a numeric or decimal value!"))
//                    .show(this, DONATION);
        }
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

    @Override
    public String validate(String dialogTag, @Nullable String input, @NonNull Bundle extras) {
        Log.e("VALIDATE", dialogTag + ": "  + input);
        if(dialogTag.equals(DONATION)){

            if(isNumeric(input)){
                double money = Double.parseDouble(input);
                if(money>0 && money<50000){
                    FirebaseDatabase.getInstance().getReference().child("Schools").child(school.id).child("raisedMoney").setValue(school.raisedMoney+money);
                    Toast.makeText(getApplicationContext(), "Thank you for your donation!", Toast.LENGTH_SHORT).show();
                    return null;
                } else{
                   return "Invalid donation amount!";
                }
            }
            return "Non-numeric donation amount!";

        }
        return null;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        return false;
    }
}
