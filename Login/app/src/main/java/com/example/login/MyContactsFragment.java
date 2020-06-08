package com.example.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import co.chatsdk.core.session.ChatSDK;

public class MyContactsFragment extends Fragment{

    DatabaseReference database;
    static FragmentActivity activity;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    Bitmap image = null;
    RecyclerViewClickListener listener;
    static UserAdapter userAdapter;
    public static ArrayList<User> listOfUsers = new ArrayList<>();

    private OnFragmentInteractionListener mListener;

    public MyContactsFragment() {
        // Required empty public constructor
    }
    public static String getName() { return "Contacts"; }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseDatabase.getInstance().getReference();
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Create an Intent to the BookDetail Activity, and pass in the info about the specific Book that was clicked
//                Intent i = new Intent(getContext(), SchoolInfoActivity.class);
                final User user = listOfUsers.get(position);
                Intent intent = new Intent(activity.getApplicationContext(), ViewOtherProfileActivity.class);
                intent.putExtra("UID",user.uid);
                startActivity(intent);
//                String user_uid = user.uid;
//                ChatSDK.ui().startChatActivityForID(activity.getApplicationContext(), user_uid);

            }
        };


        return inflater.inflate(R.layout.fragment_my_contacts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = (RecyclerView) view.findViewById(R.id.contacts_recycler);
        recyclerView.setHasFixedSize(true);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);

        update();
    }

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


    public void showCards() {
        userAdapter = new UserAdapter(listOfUsers, listener);
        recyclerView.setAdapter(userAdapter);
    }

    private void update() {
        DatabaseReference usersRoot = database.child("Users");
        usersRoot.keepSynced(true);
        DatabaseReference myFriendsRoot = database.child("Users").child(FirebaseAuth.getInstance().getUid()).child("friendUIDs");
        usersRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSnapshot mydata = dataSnapshot.child(FirebaseAuth.getInstance().getUid()).child("friendUIDs");
                listOfUsers.clear();
                recyclerView.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                for(DataSnapshot ds: mydata.getChildren()){
                    String uid = ds.getKey().toString();
                    User user = dataSnapshot.child(uid).getValue(User.class);
                    user.uid = ds.getKey();
                    listOfUsers.add(user);
                    Log.e("User", user.toString());
                }

                showCards();
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static void changeUserInContactsImmediately(String uid, boolean add){

        DatabaseReference usersRoot = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
//        usersRoot.keepSynced(true);
        ValueEventListener myValEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                u.uid = uid;

                if(add) {
                    listOfUsers.add(u);
                } else{
                    boolean a = listOfUsers.remove(u);
                    Log.e("Removed", ""+a);
                }
                userAdapter.notifyDataSetChanged();
                Log.e("UserChanged", u.toString());
                Log.e("listofusers", listOfUsers.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        usersRoot.addListenerForSingleValueEvent(myValEventListener);
//        usersRoot.removeEventListener(myValEventListener);





    }




}

class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private ArrayList<User> users;
    private RecyclerViewClickListener mListener;
    //Default constructor
    UserAdapter(ArrayList<User> users, RecyclerViewClickListener listener) {
        this.users = users;
        mListener = listener;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public void onBindViewHolder(UserViewHolder userViewHolder, int i) {
        //Set each field to its corresponding attribute
        User user = users.get(i);
        userViewHolder.name.setText(user.name);

        //Load the proper image into the imageView using the Glide framework
        Glide.with(userViewHolder.itemView)
                .load(user.pfpUrl)
                .into(userViewHolder.profilePic);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        //Inflate the view using the proper xml layout
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.user_card, viewGroup, false);

        return new UserViewHolder(itemView, mListener);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cardView;
        TextView name;
        CircularImageView profilePic;

        private RecyclerViewClickListener mListener;

        UserViewHolder(View v, RecyclerViewClickListener mListener) {
            super(v);
            //instantiation of views
            cardView = (CardView)       v.findViewById(R.id.cardView);
            name =  (TextView)         v.findViewById(R.id.userName);
            profilePic =  (CircularImageView)         v.findViewById(R.id.user_profile_pic);
            this.mListener = mListener;
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }
}





















































