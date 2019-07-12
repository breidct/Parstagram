package com.example.parstagram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.parstagram.model.Post;
import com.parse.ParseFile;

import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public GridAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid_post, parent, false);
        //isProfile = false;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridAdapter.ViewHolder viewHolder, int position) {
        Post post = posts.get(position);
        viewHolder.bind(post);
    }

    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivGrid;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivGrid = itemView.findViewById(R.id.ivGrid);
        }

        public void bind(final Post post){
            ParseFile image = post.getImage();
            if(image != null){
                //Toast.makeText(context, "You did it loser", Toast.LENGTH_SHORT).show();
                Glide.with(context)
                        .load(image.getUrl())
                        .into(ivGrid);
            }

            ivGrid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, post_details.class);
                    intent.putExtra("postId", post.getObjectId());
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, ivGrid, "postImage");
                    context.startActivity(intent, options.toBundle());
                }
            });
        }
    }

}