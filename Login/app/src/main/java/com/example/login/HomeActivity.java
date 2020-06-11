package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import co.chatsdk.core.session.ChatSDK;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.form.Check;
import eltos.simpledialogfragment.form.FormElement;
import eltos.simpledialogfragment.form.Input;
import eltos.simpledialogfragment.form.SimpleFormDialog;
import eltos.simpledialogfragment.list.CustomListDialog;
import eltos.simpledialogfragment.list.SimpleListDialog;

import static eltos.simpledialogfragment.list.CustomListDialog.SINGLE_CHOICE;
import static eltos.simpledialogfragment.list.CustomListDialog.SINGLE_CHOICE_DIRECT;


public class HomeActivity extends AppCompatActivity implements SimpleFormDialog.OnDialogResultListener, SchoolListFragment.OnFragmentInteractionListener, MyContactsFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener{

    SchoolListFragment schoolListFragment;

    Map<String, String> codestoschoolsMap;

    final String ONBOARDING1 = "1";
    final String ONBOARDING2 = "2";
    final String ONBOARDING3 = "3";
    final String ONBOARDING4 = "4";
    final String ONBOARDING5 = "5";
    final String ONBOARDING6 = "6";
    final String ONBOARDING7 = "7";
    final String ONBOARDING8 = "8";
    final String ONBOARDING9 = "9";
    final String ONBOARDING10 = "10";
    final String ONBOARDING11 = "11";
    final String ONBOARDING12 = "12";

    String onboardingname;
    String onboardingusertype;
    String onboardingphone;
    String onboardinglang;
    String onboardingbio;
    String onboardingschool;
    String onboardinglocation;
    String onboardingcode;
    String onboardingschoolid;
    boolean onboarding = true;
    int positionoftab = 1;
    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    FloatingSearchView searchView;

    static ArrayList<SearchSuggestion> suggestions = new ArrayList<>();
    static ArrayList<String> list = new ArrayList<String>();
    int position;
    Bitmap image = null;


    final int VOICE_SEARCH_CODE = 3012;

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    private void updateSearches(String query) {
        searchView.showProgress();

        final String newQuery = query;
        Log.e("Fragment Right Now", ""+positionoftab);
        if(positionoftab==0) {
            final DatabaseReference searchRef = database.getReference().child("Users");
            searchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    suggestions.clear();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        User user = d.getValue(User.class);
                        user.uid = d.getKey().toString();
                        UserSearchable userSearchable = new UserSearchable(user);

                        String lQuery = newQuery.toLowerCase();
                        StringTokenizer st = new StringTokenizer(lQuery);


                        boolean allTokens = false;

                        while (st.hasMoreTokens()) {
                            String currToken = st.nextToken();
                            if (userSearchable.name.toLowerCase().contains(currToken) || userSearchable.location.toLowerCase().contains(currToken) || userSearchable.bio.contains(currToken)) {
                                allTokens = true;
                            } else {
                                allTokens = false;
                                break;
                            }
                        }
                        if (allTokens) {
                            suggestions.add(userSearchable);
                        }


                    }


                    searchView.swapSuggestions(suggestions);
                    searchView.hideProgress();
                    Log.e("Suggestions", suggestions.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        else if(positionoftab==1) {
            final DatabaseReference searchRef = database.getReference().child("Schools");
            searchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    suggestions.clear();
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        School school = d.getValue(School.class);
                        school.items.remove(0);
                        String id = school.id;
                        String name = school.name;
                        String location = school.location;
                        String organizerID = school.organizerID;
                        String description = school.description;
                        double raisedMoney = school.raisedMoney;
                        double totalMoney = school.totalMoney;
                        String imageUri = school.imageUri;
                        ArrayList<String> items = school.items;
                        String lQuery = newQuery.toLowerCase();
                        StringTokenizer st = new StringTokenizer(lQuery);

                        double latitude = Double.parseDouble(location.split(",")[0]);
                        double longitude = Double.parseDouble(location.split(",")[1].substring(1));

                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        String cityName = addresses.get(0).getLocality();
                        String countryName = addresses.get(0).getCountryName();
                        String totalLocation = cityName + ", " + countryName;

                        ListIterator<String> iterator = items.listIterator();
                        while (iterator.hasNext()) {
                            iterator.set(iterator.next().toLowerCase());
                        }


                        boolean allTokens = false;

                        while (st.hasMoreTokens()) {
                            String currToken = st.nextToken();
                            if (name.toLowerCase().contains(currToken) || totalLocation.toLowerCase().contains(currToken) || items.contains(currToken)) {
                                allTokens = true;
                            } else {
                                allTokens = false;
                                break;
                            }
                        }
                        if (allTokens) {
                            suggestions.add(new School(id, name, imageUri, location, raisedMoney, totalMoney, description, organizerID, items));
                        }


                    }


                    searchView.swapSuggestions(suggestions);
                    searchView.hideProgress();
                    Log.e("Suggestions", suggestions.toString());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else if(positionoftab==2){
            Toast.makeText(getApplicationContext(), "You can't use search while on your profile!", Toast.LENGTH_LONG).show();
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

            return super.onOptionsItemSelected(item);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_start);

        searchView = (FloatingSearchView) findViewById(R.id.searchView);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        coordinatorLayout.bringToFront();

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager()));

        //intialize the tab layout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //add 3 new tabs.
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setupWithViewPager(viewPager);

        //default tab that it loads on is the middle tab
        viewPager.setCurrentItem(1);


        //create page listener to return the tab at a certain position.
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                positionoftab = tab.getPosition();
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //set the behavior when the menu is clicked.
        searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.voice:
                        // Voice search
                        startVoiceRecognition();
                        searchView.setSearchFocused(true);
                        break;
                    case R.id.feedback:
                        //Send a bug report via email using email intent.
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/html");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tinovationofficerteam@gmail.com"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "OneSharedSchool App Bug Report");
                        intent.putExtra(Intent.EXTRA_TEXT, "My bug...");

                        startActivity(Intent.createChooser(intent, "Send Email"));
                        break;
                    case R.id.logout:
                        //use our authentication database to sign out.
                        ChatSDK.auth().logout().subscribe();
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut();
                        //move user to sign in page once signed out.
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        break;
                    default:
                        break;
                }
            }
        });

        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {
                updateSearches(newQuery);
            }

        });

        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                updateSearches(searchView.getQuery());
            }

            @Override
            public void onFocusCleared() {

            }
        });

        //manages search query suggestions.

        searchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                String body = item.getBody();
                String htmlText = body;
                String query = ""+searchView.getQuery();

                if (query.length() == 0) return;

                ArrayList<String> queryTokens = new ArrayList<>();

                StringTokenizer st = new StringTokenizer(query);
                while (st.hasMoreTokens()) {
                    queryTokens.add(st.nextToken().toLowerCase());
                }


                for (String currQuery : queryTokens) {
                    htmlText = htmlText.replaceAll("(?i)" + currQuery, "<font color=#999999>" + currQuery + "</font>");
                }

                textView.setText(Html.fromHtml(htmlText));
            }

        });


        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                searchView.setSearchFocused(false);
                if(positionoftab==0){
                    UserSearchable userToPass = (UserSearchable) searchSuggestion;
                    if(userToPass.uid.equals(FirebaseAuth.getInstance().getUid())){
                        tabLayout.selectTab(tabLayout.getTabAt(2));
                        viewPager.setCurrentItem(2);
                    } else {
                        Intent i = new Intent(getApplicationContext(), ViewOtherProfileActivity.class);

                        Log.e("UserToPass", userToPass.toString());
                        i.putExtra("UID", userToPass.uid);
                        startActivity(i);
                    }
                }
                else if(positionoftab==1) {
                    Intent i = new Intent(getApplicationContext(), SchoolInfoActivity.class);
                    i.putExtra("School", searchSuggestion);
                    final School school = (School) searchSuggestion;

                    //Get the image from the book's image url
                    Thread thread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try {
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

            }

            @Override
            public void onSearchAction(String currentQuery) {

            }
        });

        //first if checks if this is the first time running. second one checks if we actually should onboard based on extra from intent.
        if(onboarding){
            Bundle b = getIntent().getExtras();
            if(b!=null) {
                onboarding = (Boolean) getIntent().getExtras().get("Onboarding");
                if(onboarding){
                    onboarding = false;
                    runOnboarding(1);
                }
            }
        }
        onboarding = false;


    }

    public void runOnboarding(int stage){
        SimpleDialog d;
        switch(stage) {
            case 1:
                d = SimpleFormDialog.build().title("Welcome to OneSharedSchool!").msg("This quick tutorial will walk you through the different aspects of the app" +
                        " and will get you set up with your own profile!");
                d.cancelable(false);
                d.show(this, ONBOARDING1);
                break;
            case 2:
                d = SimpleFormDialog.build().title("What should we call you?").fields(
                        Input.name("NAME").hint("Enter your full name.").required()
                );
                d.cancelable(false);
                d.show(this, ONBOARDING2);
                break;
            case 3:
                d= SimpleListDialog.build()
                        .title("Hey " + onboardingname + "!").msg("Are you a: ")
                        .items(new String[]{"Student", "Teacher", "Donor"})
                        .choiceMode(SINGLE_CHOICE);
                d.cancelable(false);
                d.show(this, ONBOARDING3);
                break;
            case 4:
                d = SimpleFormDialog.build().title("Sweet! Mind entering your phone number?").msg("This is an optional step.").fields(
                        Input.phone("PHONE")
                );
                d.cancelable(false);
                d.show(this, ONBOARDING4);
                break;
            case 5:
                d = SimpleListDialog.build()
                        .title("Language preferences").msg("Unfortunately, translation isn't available just yet!")
                        .items(getApplicationContext(), new int[]{R.string.english, R.string.spanish, R.string.mandarin, R.string.hindi, R.string.russian, R.string.arabic})
                        .choiceMode(SINGLE_CHOICE);
                d.cancelable(false);
                d.show(this, ONBOARDING5);
                break;
            case 6:
                int unicode = 0x1F605;
                d = SimpleDialog.build()
                        .title("Almost there \uD83D\uDE05!");
                d.cancelable(false);
                d.show(this, ONBOARDING6);
                break;
            case 7:
                d = SimpleFormDialog.build().title(onboardingname + ": tell us a little bit about yourself!").msg("This is an optional step").fields(
                        Input.plain("BIO").hint("1-2 sentences about yourself, your hobbies, etc."));
                d.cancelable(false);
                d.show(this, ONBOARDING7);
                break;
            case 8:
                d = SimpleFormDialog.build().title("School Registration")
                        .msg("Please enter your school's 10-digit, alphanumeric access code:")
                        .fields(
                                Input.plain("CODE").validatePatternAlphanumeric().min(10).max(10).required(),
                                Check.box(null).label("I am a student from this school").required()
                        );
                d.cancelable(false);
                d.show(this, ONBOARDING8);
                break;
            case 9:
                d = SimpleFormDialog.build().title("School Registration")
                        .msg("Error! We couldn't find that code. Please enter your school's 10-digit, alphanumeric access code:")
                        .fields(
                                Input.plain("CODE").validatePatternAlphanumeric().min(10).max(10).required(),
                                Check.box(null).label("I am a student from this school").required()
                        );
                d.cancelable(false);
                d.show(this, ONBOARDING8);
                break;
            case 10:
                d = SimpleListDialog.build()
                        .title("Confirm")
                        .msg("To confirm: " + onboardingschool + " is your school, right?")
                        .items(getApplicationContext(), new int[]{R.string.yes, R.string.no})
                        .choiceMode(SINGLE_CHOICE);
                d.cancelable(false);
                d.show(this, ONBOARDING10);
                break;
            case 11:
                int unicode2 = 0x1F973;
                d = SimpleDialog.build()
                        .title("All done \uD83D\uDE05"  + "!");
                d.cancelable(false);
                d.show(this, ONBOARDING11);
                break;
            default:
                d = SimpleFormDialog.build().title("Welcome to OneSharedSchool!").msg("This quick tutorial will walk you through the different aspects of the app" +
                        " and will get you set up with your own profile!");
                d.cancelable(false);
                d.show(this, ONBOARDING1);
        }


    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {
        if(which == BUTTON_POSITIVE) {
            if (ONBOARDING1.equals(dialogTag)) {
                runOnboarding(2);
            }
            if (ONBOARDING2.equals(dialogTag)) {
                onboardingname = extras.getString("NAME");
                runOnboarding(3);
            }
            if (ONBOARDING3.equals(dialogTag)) {
                Log.e("extras", extras.toString());
                onboardingusertype = extras.get("SimpleListDialog.selectedSingleLabel").toString();
                Log.e("usertype", onboardingusertype);
                runOnboarding(4);
            }
            if (ONBOARDING4.equals(dialogTag)) {
                onboardingphone = extras.getString("PHONE");
                runOnboarding(5);
            }
            if (ONBOARDING5.equals(dialogTag)) {
                onboardinglang = extras.getString("SimpleListDialog.selectedSingleLabel");
                runOnboarding(6);
            }
            if (ONBOARDING6.equals(dialogTag)) {
                runOnboarding(7);
            }
            if (ONBOARDING7.equals(dialogTag)) {
                onboardingbio = extras.getString("BIO");
                if(onboardingusertype.equals("Student") || onboardingusertype.equals("Teacher")) {
                    DatabaseReference codestoschools = FirebaseDatabase.getInstance().getReference().child("SchoolCodes");
                    codestoschools.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            codestoschoolsMap = (Map) dataSnapshot.getValue();
                            runOnboarding(8);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else{
                    runOnboarding(11);
                }
            }
            if (ONBOARDING8.equals(dialogTag)) {
                onboardingcode = extras.getString("CODE");
                if(verifyCode(onboardingcode)){
                    runOnboarding(10);
                } else {
                    runOnboarding(9);
                }
            }
            if (ONBOARDING9.equals(dialogTag)) {
                onboardingcode = extras.getString("CODE");
                if(verifyCode(onboardingcode)){
                    runOnboarding(10);
                }
                else {
                    runOnboarding(9);
                }
            }
            if (ONBOARDING10.equals(dialogTag)) {
                String confirmation = extras.getString("SimpleListDialog.selectedSingleLabel");
                if(confirmation.equals("Yes")){
                    setupprofile();
                    runOnboarding(11);
                } else{
                    runOnboarding(9);
                }
            }


            return true;
        }
        return false;
    }
    public void setupprofile(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid().toString());
        userRef.child("name").setValue(onboardingname);
        userRef.child("language").setValue(onboardinglang);
        userRef.child("location").setValue(onboardinglocation);
        userRef.child("bio").setValue(onboardingbio);
        userRef.child("phone").setValue(onboardingphone);
        userRef.child("school").setValue(onboardingschoolid);

        ProfileFragment.loadProfileInfo(getApplicationContext(), FirebaseAuth.getInstance().getUid().toString());

    }
    public List<Address> latLongToText(String schoolLocation){


        double latitude = Double.parseDouble(schoolLocation.split(",")[0]);
        double longitude = Double.parseDouble(schoolLocation.split(",")[1].substring(1));


        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses;
    }

    public boolean verifyCode(String code){
        String s_id = "-1";
        if(codestoschoolsMap.containsKey(code)){
            s_id = String.valueOf(codestoschoolsMap.get(code));
            Log.e("SchoolID", s_id);
        }
        if(s_id.equals("-1")){
            return false;
        }
        Log.e("ListOfSchools",schoolListFragment.getListOfSchools().toString());
        for(School s : schoolListFragment.getListOfSchools()){
            Log.e("School s",s.id);
            if(s.id.equals(s_id)){

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("school").setValue(s.id);
                List<Address> addresses = latLongToText(s.location);
                String cityName = addresses.get(0).getLocality();
                String countryName = addresses.get(0).getCountryName();
                onboardinglocation = cityName + ", " + countryName;
                onboardingschoolid = s.id;
                onboardingschool = s.name;
                break;
            }
        }
        return true;

    }



    public void startVoiceRecognition() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
        intent.putExtra("android.speech.extra.PROMPT", "Speak Now");
        this.startActivityForResult(intent, VOICE_SEARCH_CODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == VOICE_SEARCH_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra("android.speech.extra.RESULTS");
//            searchView.setQuery(matches.get(0),true);
            searchView.setSearchText(matches.get(0));
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private class PageAdapter extends FragmentPagerAdapter {

        PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new MyContactsFragment();
                case 1:
                    schoolListFragment = new SchoolListFragment();
                    return schoolListFragment;
                case 2:
                    return new ProfileFragment();
                default:
                    return new SchoolListFragment();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return MyContactsFragment.getName();
                case 1:
                    return SchoolListFragment.getName();
                case 2:
                    return ProfileFragment.getName();
                default:
                    return SchoolListFragment.getName();
            }
        }
    }


}
