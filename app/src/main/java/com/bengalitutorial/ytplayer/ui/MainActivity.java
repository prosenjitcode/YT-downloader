package com.bengalitutorial.ytplayer.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.bengalitutorial.ytplayer.R;
import com.bengalitutorial.ytplayer.adapter.VideoListAdapter;
import com.bengalitutorial.ytplayer.api.Constans;
import com.bengalitutorial.ytplayer.api.RetrofitClient;
import com.bengalitutorial.ytplayer.models.Item;
import com.bengalitutorial.ytplayer.models.MovieDetails;
import com.bengalitutorial.ytplayer.service.ServiceInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private VideoListAdapter videoListAdapter;
    private boolean saved = false;
    private List<Item> vidios = new ArrayList<>();

    private static final String PREFS = "PREFS";
    private static final String PREFS_SEARCH = "PREFS_SEARCH";
    private SharedPreferences sharedPreferences;
    private ServiceInterface service;

    private LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        service = RetrofitClient.getRetrofit().create(ServiceInterface.class);
        sharedPreferences = getBaseContext().getSharedPreferences(PREFS, MODE_PRIVATE);

        recyclerView = (RecyclerView) findViewById(R.id.listView);
         layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        new FatchMovie().execute("");
    }


    private void LoadMovie(String query) {

        Call<MovieDetails> call = service.getAllMovies("snippet", "25", query, "video", Constans.API_KEY);
        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        vidios.clear();
                        vidios = response.body().getItems();
                        videoListAdapter = new VideoListAdapter(vidios, MainActivity.this);
                        recyclerView.setAdapter(videoListAdapter);

                    }

               } else {
                    Toast.makeText(MainActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                Toast.makeText(MainActivity.this, "On Failure" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }



    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_options, menu);
        MenuItem searchView = menu.findItem(R.id.menu_search);

        searchView.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                item.getActionView().requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });

        SearchView menu_search = (SearchView) searchView.getActionView();

        final SearchView.SearchAutoComplete searchAutoComplete = menu_search.findViewById(R.id.search_src_text);
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white);
        searchAutoComplete.setThreshold(0);

        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) searchAutoComplete.findViewById(R.id.search_src_text);
        final View dropDownAnchor = menu_search.findViewById(autoCompleteTextView.getDropDownAnchor());
        if (dropDownAnchor != null) {
            dropDownAnchor.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    Rect screenSize = new Rect();
                    getWindowManager().getDefaultDisplay().getRectSize(screenSize);
                    autoCompleteTextView.setDropDownWidth(screenSize.width());
                }
            });
        }

        if (sharedPreferences.contains(PREFS_SEARCH)) {
            final String dataArr[] = sharedPreferences.getString(PREFS_SEARCH, null).split(";");
            ArrayAdapter<String> autocompleteAdapter = new ArrayAdapter<String>(this, R.layout.dropdown, dataArr);
            searchAutoComplete.setAdapter(autocompleteAdapter);
        }

        menu_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                LoadMovie(query);

                if (sharedPreferences.contains(PREFS_SEARCH)) {
                    // check if this query already exist
                    if (!Arrays.asList(sharedPreferences.getString(PREFS_SEARCH, null).split(";")).contains(query)) {
                        sharedPreferences
                                .edit()
                                .putString(PREFS_SEARCH, sharedPreferences.getString(PREFS_SEARCH, null) + ";" + query)
                                .apply();
                    }
                } else {
                    sharedPreferences
                            .edit()
                            .putString(PREFS_SEARCH, query)
                            .apply();
                }

                // update autocomplete
                String newDataArr[] = sharedPreferences.getString(PREFS_SEARCH, null).split(";");
                ArrayAdapter<String> newAutocompleteAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.dropdown, newDataArr);
                searchAutoComplete.setAdapter(newAutocompleteAdapter);

                // hide keybord
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(findViewById(R.id.listView).getWindowToken(), 0);

                return true;
            }
        });

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString = (String) adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
            }
        });

        return true;
    }

    public class FatchMovie extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            if (this != null)
                LoadMovie(strings[0]);

            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            recyclerView.setAdapter(videoListAdapter);
        }

        @Override
        protected void onPreExecute() {

        }


    }




}
