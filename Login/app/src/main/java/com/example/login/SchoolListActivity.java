package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class SchoolListActivity extends AppCompatActivity {

    public ArrayList<School> listOfSchools = new ArrayList<>();
    RecyclerView recyclerView;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    DatabaseReference database;
    Bitmap image = null;

    RecyclerViewClickListener listener;
    static Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_list);
        database = FirebaseDatabase.getInstance().getReference();
        c = getApplicationContext();
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        recyclerView = (RecyclerView)findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this.getApplicationContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        update();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }
        });



//Listen for clicks in the main recyclerview
        listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Create an Intent to the BookDetail Activity, and pass in the info about the specific Book that was clicked
                Intent i = new Intent(getApplicationContext(), SchoolInfoActivity.class);
                final School school = listOfSchools.get(position);
                i.putExtra("School",school);

                //Get the image from the book's image url
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            URL url = new URL(school.imageUri);
                            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                //Start the process above with this line of code
                thread.start();

                try {
                    //wait for the thread to die
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Convert the image from before into a byte array so that it can be passed into the new intent
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();

                i.putExtra("Image", byteArray);
                startActivity(i);
            }
        };

    }





    private void update() {
        database.child("Schools").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listOfSchools.clear();
                recyclerView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                //Iterate through all children in the Books root to get information for each ISBN
                Iterator i = dataSnapshot.getChildren().iterator();
                while(i.hasNext()){

                    String schoolID = ((DataSnapshot) i.next()).getKey();
                    //Get the ISBN from the key of the child, and then proceed to find the title, author, description, image url, and rating
                    String name = ((dataSnapshot).child(schoolID).child("Name").getValue().toString());

                    String location = ((dataSnapshot).child(schoolID).child("Location").getValue().toString());
                    int funding_goal = Integer.parseInt(((dataSnapshot).child(schoolID).child("Funding Goal").getValue().toString()));
                    int raised = Integer.parseInt(((dataSnapshot).child(schoolID).child("Raised").getValue().toString()));
                    String donation_manager_id = ((dataSnapshot).child(schoolID).child("Donation Manager").getValue().toString());
                    String uri = ((dataSnapshot).child(schoolID).child("ImageURI").getValue().toString());
                    String description = ((dataSnapshot).child(schoolID).child("Description").getValue().toString());
                    Iterator j = (dataSnapshot).child(schoolID).child("Items").getChildren().iterator();
                    ArrayList<String> items = new ArrayList<>();
                    while(j.hasNext()){
                        items.add(((DataSnapshot) j.next()).getValue().toString());
                    }

                    listOfSchools.add(new School(schoolID, name, uri, location, raised, funding_goal, description, donation_manager_id, items));
                }

                showCards();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        swipeRefreshLayout.setRefreshing(false);
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void showCards() {
        SchoolAdapter schoolAdapter = new SchoolAdapter(listOfSchools, listener);
        recyclerView.setAdapter(schoolAdapter);
    }

    public static String getName() { return "Explore"; }

}
//
//class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.SchoolViewHolder> {
//    private ArrayList<School> schools;
//    private RecyclerViewClickListener mListener;
//    //Default constructor
//    SchoolAdapter(ArrayList<School> schools, RecyclerViewClickListener listener) {
//        this.schools = schools;
//        mListener = listener;
//    }
//
//    @Override
//    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
//        super.onAttachedToRecyclerView(recyclerView);
//    }
//
//    @Override
//    public int getItemCount() {
//        return schools.size();
//    }
//
//    @Override
//    public void onBindViewHolder(SchoolViewHolder schoolViewHolder, int i) {
//        //Set each field to its corresponding attribute
//        School school = schools.get(i);
//        schoolViewHolder.name.setText(school.name);
//        double latitude = Double.parseDouble(school.location.split(",")[0]);
//        double longitude = Double.parseDouble(school.location.split(",")[1].substring(1));
//
//        Geocoder geocoder = new Geocoder(SchoolListActivity.c, Locale.getDefault());
//        List<Address> addresses = null;
//        try {
//            addresses = geocoder.getFromLocation(latitude, longitude, 1);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        String cityName = addresses.get(0).getLocality();
////        String stateName = addresses.get(0).getAddressLine(1);
//        String countryName = addresses.get(0).getCountryName();
//
//        schoolViewHolder.location.setText(cityName + ", " + countryName);
//        schoolViewHolder.description.setText(school.description);
//        schoolViewHolder.fundsRaised.setMax(school.totalMoney);
//        schoolViewHolder.fundsRaised.setProgress(school.raisedMoney);
//
//        //Load the proper image into the imageView using the Glide framework
//        Glide.with(schoolViewHolder.itemView)
//                .load(school.imageUri)
//                .into(schoolViewHolder.schoolImage);
//    }
//
//    @Override
//    public SchoolViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
//        //Inflate the view using the proper xml layout
//        View itemView = LayoutInflater.
//                from(viewGroup.getContext()).
//                inflate(R.layout.school_card, viewGroup, false);
//
//        return new SchoolViewHolder(itemView, mListener);
//    }
//
//    static class SchoolViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//
//        CardView cardView;
//        TextView name;
//        TextView location;
//        TextView description;
//        ProgressBar fundsRaised;
//        ImageView schoolImage;
//
//        private RecyclerViewClickListener mListener;
//
//        SchoolViewHolder(View v, RecyclerViewClickListener mListener) {
//            super(v);
//            //instantiation of views
//            cardView = (CardView)       v.findViewById(R.id.cardView);
//            name =  (TextView)         v.findViewById(R.id.schoolName);
//            location = (TextView)         v.findViewById(R.id.schoolLocation);
//            description = (TextView)    v.findViewById(R.id.schoolDescription);
//            fundsRaised = (ProgressBar)     v.findViewById(R.id.schoolFundsRaised);
//            schoolImage = (ImageView)     v.findViewById(R.id.schoolImageView);
//
//            this.mListener = mListener;
//            v.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            mListener.onClick(v, getAdapterPosition());
//        }
//    }
//}
