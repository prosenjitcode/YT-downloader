package com.bengalitutorial.ytplayer.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.bengalitutorial.ytplayer.R;
import com.bengalitutorial.ytplayer.adapter.VideoListAdapter;
import com.bengalitutorial.ytplayer.api.Constans;
import com.bengalitutorial.ytplayer.api.RetrofitClient;
import com.bengalitutorial.ytplayer.models.MovieDetails;
import com.bengalitutorial.ytplayer.service.ServiceInterface;

import androidx.appcompat.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.listView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        new FatchMovie().execute("");
    }



    private void LoadMovie() {
        ServiceInterface service = RetrofitClient.getRetrofit().create(ServiceInterface.class);
        Call<MovieDetails> call = service.getAllMovies("snippet", "25", "surfing", Constans.API_KEY);
        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        videoListAdapter = new VideoListAdapter(response.body().getItems(),MainActivity.this);
                        recyclerView.setAdapter(videoListAdapter);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                Toast.makeText(MainActivity.this, "On Failure"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public class FatchMovie extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            if (this != null)
                LoadMovie();

            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {

        }

        @Override
        protected void onPreExecute() {
        }



    }

}
