package com.example.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.cursoradapter.widget.CursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import java.util.StringTokenizer;


public class HomeActivity extends AppCompatActivity implements SchoolListFragment.OnFragmentInteractionListener, FavoritesFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener{


    TabLayout tabLayout;
    ViewPager viewPager;
    Toolbar toolbar;
    CoordinatorLayout coordinatorLayout;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    FloatingSearchView searchView;

    static ArrayList<School> suggestions = new ArrayList<>();
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
        final DatabaseReference searchRef = database.getReference().child("Schools");

        searchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                suggestions.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    School school = d.getValue(School.class);
                    String id = school.id;
                    String name = school.name;
                    String location = school.location;
                    String organizerID = school.organizerID;
                    String description = school.description;
                    int raisedMoney = school.raisedMoney;
                    int totalMoney = school.totalMoney;
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
                        while (iterator.hasNext())
                        {
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
        setContentView(R.layout.activity_home);

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
                Intent i = new Intent(getApplicationContext(), SchoolInfoActivity.class);
                i.putExtra("School",searchSuggestion);
                final School school = (School) searchSuggestion;

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

            @Override
            public void onSearchAction(String currentQuery) {

            }
        });




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
                    return new FavoritesFragment();
                case 1:
                    return new SchoolListFragment();
                case 2:
                    return new ProfileFragment();//change to profile later
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
                    return FavoritesFragment.getName();
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
