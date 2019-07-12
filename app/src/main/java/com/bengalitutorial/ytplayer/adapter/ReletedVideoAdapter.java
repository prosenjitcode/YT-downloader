package com.bengalitutorial.ytplayer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bengalitutorial.ytplayer.R;
import com.bengalitutorial.ytplayer.models.Item;
import com.bengalitutorial.ytplayer.service.OnItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReletedVideoAdapter extends RecyclerView.Adapter<ReletedVideoAdapter.RVH> {
    private List<Item> itemList;
    private Context context;
    private OnItemClickListener clickListener;

    public ReletedVideoAdapter(List<Item> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @NonNull
    @Override
    public RVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.releted_list,parent,false);
        return new RVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVH holder, final int position) {
        Item movie = itemList.get(position);
        Picasso.get().load(movie.getSnippet().getThumbnails().getMedium().getUrl())
                .into(holder.imageView);
        holder.title.setText(movie.getSnippet().getTitle());
        holder.channelTitle.setText(movie.getSnippet().getChannelTitle());

    }

    @Override
    public int getItemCount() {
        return itemList!=null?itemList.size():0;
    }

    public class RVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView title,channelTitle;
        public RVH(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.videoImage);
            title = (TextView) itemView.findViewById(R.id.rTitle);
            channelTitle = (TextView) itemView.findViewById(R.id.channelName);
        }


        @Override
        public void onClick(View v) {
            Toast.makeText(context,"hi",Toast.LENGTH_SHORT).show();
        }
    }
}
