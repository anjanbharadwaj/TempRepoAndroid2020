package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


public class SchoolListFragment extends Fragment implements ObservableScrollViewCallbacks {

    DatabaseReference database;
    static FragmentActivity activity;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    ObservableRecyclerView recyclerView;
    RecyclerViewClickListener listener;
    Bitmap image = null;

    public ArrayList<School> getListOfSchools() {
        return listOfSchools;
    }

    public ArrayList<School> listOfSchools = new ArrayList<>();

    private OnFragmentInteractionListener mListener;
    public static HomeFeedValueEventListener homeFeedValueEventListener;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initialize our database reference
        database = FirebaseDatabase.getInstance().getReference();
        activity = getActivity();
        homeFeedValueEventListener = new HomeFeedValueEventListener();
        Log.e("oncreate", "a");


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        database.child("Schools").removeEventListener(homeFeedValueEventListener);
        Log.e("Tag", "FragmentA.onDestroy() has been called.");
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Create an Intent to the BookDetail Activity, and pass in the info about the specific Book that was clicked
                Intent i = new Intent(getContext(), SchoolInfoActivity.class);
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

        return inflater.inflate(R.layout.fragment_school_list, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        Log.e("Init", "In OnViewCreated");
        // initialise our views and set various attributes/layouts/listeners
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
         progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        recyclerView = (ObservableRecyclerView) view.findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setScrollViewCallbacks(this);

        update(true);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update(false);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
    private void showCards() {
        SchoolAdapter schoolAdapter = new SchoolAdapter(listOfSchools, listener);
        recyclerView.setAdapter(schoolAdapter);
    }

    private void update(boolean first) {
        if(!first){
            database.child("Schools").removeEventListener(homeFeedValueEventListener);
        }
        first = false;
        database.child("Schools").keepSynced(true);
        database.child("Schools").addListenerForSingleValueEvent(homeFeedValueEventListener);
    }

    class HomeFeedValueEventListener implements ValueEventListener{

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            listOfSchools.clear();
            recyclerView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            for(DataSnapshot ds: dataSnapshot.getChildren()){
                School school = ds.getValue(School.class);
                school.items.remove(0);
                listOfSchools.add(school);
                Log.e("School", school.toString());
            }

            showCards();
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }

    public static String getName() { return "Explore"; }


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

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}


class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.SchoolViewHolder> {
    private ArrayList<School> schools;
    private RecyclerViewClickListener mListener;
    //Default constructor
    SchoolAdapter(ArrayList<School> schools, RecyclerViewClickListener listener) {
        this.schools = schools;
        mListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return schools.size();
    }

    @Override
    public void onBindViewHolder(SchoolViewHolder schoolViewHolder, int i) {
        //Set each field to its corresponding attribute
        School school = schools.get(i);
        schoolViewHolder.name.setText(school.name);
        double latitude = Double.parseDouble(school.location.split(",")[0]);
        double longitude = Double.parseDouble(school.location.split(",")[1].substring(1));

        Geocoder geocoder = new Geocoder(SchoolListFragment.activity, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String cityName = addresses.get(0).getLocality();
//        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getCountryName();

        schoolViewHolder.location.setText(cityName + ", " + countryName);
        schoolViewHolder.description.setText(school.description);
        schoolViewHolder.fundsRaised.setMax((int)school.totalMoney);
        schoolViewHolder.fundsRaised.setProgress((int)school.raisedMoney);

        //Load the proper image into the imageView using the Glide framework
        Glide.with(schoolViewHolder.itemView)
                .load(school.imageUri)
                .into(schoolViewHolder.schoolImage);
    }

    @Override
    public SchoolViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //Inflate the view using the proper xml layout
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.school_card, viewGroup, false);

        return new SchoolViewHolder(itemView, mListener);
    }

    static class SchoolViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        TextView name;
        TextView location;
        TextView description;
        ProgressBar fundsRaised;
        ImageView schoolImage;

        private RecyclerViewClickListener mListener;

        SchoolViewHolder(View v, RecyclerViewClickListener mListener) {
            super(v);
            //instantiation of views
            cardView = (CardView)       v.findViewById(R.id.cardView);
            name =  (TextView)         v.findViewById(R.id.schoolName);
            location = (TextView)         v.findViewById(R.id.schoolLocation);
            description = (TextView)    v.findViewById(R.id.schoolDescription);
            fundsRaised = (ProgressBar)     v.findViewById(R.id.schoolFundsRaised);
            schoolImage = (ImageView)     v.findViewById(R.id.schoolImageView);

            this.mListener = mListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }
}
