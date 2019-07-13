package com.bengalitutorial.ytplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bengalitutorial.ytplayer.R;
import com.bengalitutorial.ytplayer.models.Item;
import com.bengalitutorial.ytplayer.ui.VideoDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VH> {

    private List<Item> itemList;
    private Context context;

    public VideoListAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, int position) {
        final Item video = itemList.get(position);

        Picasso.get().load(video.getSnippet().getThumbnails().getMedium().getUrl())
                .into(holder.imageView);
        holder.title.setText(video.getSnippet().getTitle());
        holder.chTitle.setText(video.getSnippet().getChannelTitle());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VideoDetailsActivity.class);
                intent.putExtra("videoId",video.getId().getVideoId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class VH extends RecyclerView.ViewHolder {

        TextView title, chTitle;
        ImageView imageView;

        public VH(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.videoThumbnail);
            title =  itemView.findViewById(R.id.titleView);
            chTitle =  itemView.findViewById(R.id.channelTitleView);
        }
    }
}
