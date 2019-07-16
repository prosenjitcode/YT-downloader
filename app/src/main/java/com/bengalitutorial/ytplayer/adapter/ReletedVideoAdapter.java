package com.bengalitutorial.ytplayer.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bengalitutorial.ytplayer.R;
import com.bengalitutorial.ytplayer.models.Item;
import com.bengalitutorial.ytplayer.service.OnItemClickListener;
import com.bengalitutorial.ytplayer.ui.DownloadActivity;
import com.bengalitutorial.ytplayer.ui.VideoDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReletedVideoAdapter extends RecyclerView.Adapter<ReletedVideoAdapter.RVH> {
    private List<Item> itemList;
    private Context context;
    private OnItemClickListener clickListener;

    public ReletedVideoAdapter(List<Item> itemList, Context context, OnItemClickListener clickListener) {
        this.itemList = itemList;
        this.context = context;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.releted_list, parent, false);
        return new RVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVH holder, final int position) {
        final Item movie = itemList.get(position);
        Picasso.get().load(movie.getSnippet().getThumbnails().getMedium().getUrl())
                .into(holder.imageView);
        holder.title.setText(movie.getSnippet().getTitle());
        holder.channelTitle.setText(movie.getSnippet().getChannelTitle());

       holder.ytDown.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final String youtubeLink = "http://youtube.com/watch?v=" + movie.getId().getVideoId();
               Intent intent = new Intent(context.getApplicationContext(), DownloadActivity.class);
               intent.putExtra("URL",youtubeLink);
               context.startActivity(intent);
           }
       });
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public class RVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView, ytDown;
        TextView title, channelTitle;

        public RVH(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.videoImage);
            ytDown = (ImageView) itemView.findViewById(R.id.downlodb);
            title = (TextView) itemView.findViewById(R.id.rTitle);
            channelTitle = (TextView) itemView.findViewById(R.id.channelName);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            clickListener.OnItemClick(getAdapterPosition());

        }
    }
}
