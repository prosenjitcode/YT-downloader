package com.bengalitutorial.ytplayer.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bengalitutorial.ytplayer.R;
import com.bengalitutorial.ytplayer.adapter.RecyclerViewOnClickListener;
import com.bengalitutorial.ytplayer.adapter.ReletedVideoAdapter;
import com.bengalitutorial.ytplayer.api.Constans;
import com.bengalitutorial.ytplayer.api.RetrofitClient;
import com.bengalitutorial.ytplayer.models.Item;
import com.bengalitutorial.ytplayer.models.MovieDetails;
import com.bengalitutorial.ytplayer.service.OnItemClickListener;
import com.bengalitutorial.ytplayer.service.ServiceInterface;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoDetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, OnItemClickListener {

    private RecyclerView recyclerView;
    private ReletedVideoAdapter adapter;

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerView playerView;
    private String videoId;
    private List<Item> itemList = new ArrayList<>();
    private YouTubePlayer youTubePlayer;

    private TextView title,channel;
    private String videoTitle,videoChannel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_details);

        Bundle bundle = getIntent().getExtras();
        videoId = bundle.getString("videoId");
        videoTitle = bundle.getString("TITLE");
        videoChannel = bundle.getString("CHANNEL");

        title = (TextView)findViewById(R.id.titleV);
        channel = (TextView)findViewById(R.id.channelV);

        playerView = (YouTubePlayerView)findViewById(R.id.player);
        playerView.initialize(Constans.API_KEY,this);

        recyclerView = (RecyclerView)findViewById(R.id.rList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        ServiceInterface service = RetrofitClient.getRetrofit().create(ServiceInterface.class);
        Call<MovieDetails> call = service.getReletedMovies("snippet", "25", videoId,"video", Constans.API_KEY);
        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, final Response<MovieDetails> response) {
                if (response != null) {
                    if (response.isSuccessful()) {
                        itemList = response.body().getItems();
                        adapter = new ReletedVideoAdapter(itemList, VideoDetailsActivity.this,VideoDetailsActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {

            }
        });
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        this.youTubePlayer = youTubePlayer;
        youTubePlayer.cueVideo(videoId);
        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

        title.setText(videoTitle);
        channel.setText(videoChannel);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this,RECOVERY_REQUEST).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==RECOVERY_REQUEST){
            getYoutubePlayerProvider().initialize(Constans.API_KEY,this);
        }
    }

    protected YouTubePlayer.Provider getYoutubePlayerProvider(){
        return playerView;
    }


    @Override
    public void OnItemClick(int position) {
        String videoIdl = itemList.get(position).getId().getVideoId();
        youTubePlayer.cueVideo(videoIdl);

        title.setText(itemList.get(position).getSnippet().getTitle());
        channel.setText(itemList.get(position).getSnippet().getChannelTitle());
    }
}
