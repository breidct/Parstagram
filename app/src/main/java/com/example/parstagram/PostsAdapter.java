package com.example.parstagram;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.parstagram.model.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;
    //public static boolean isProfile;


    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        //isProfile = false;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Post post = posts.get(position);
        viewHolder.bind(post);

    }
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivImage;
        private ImageView ivProfilePic;
        private TextView tvDescription;
        private TextView tvHandle;
        private TextView tvHandle2;
        private ImageButton ibHeart;
        private ImageButton ibComment;
        private ImageButton ibSend;
        private TextView tvTime;
        List<ParseUser> listUsers;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHandle = itemView.findViewById(R.id.tvHandle);
            tvHandle2 = itemView.findViewById(R.id.tvHandle2);
            ivImage = itemView.findViewById(R.id.ivImage);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTime = itemView.findViewById(R.id.tvTime);
            ibHeart = itemView.findViewById(R.id.ibHeart);
            ibComment = itemView.findViewById(R.id.ibComment);
            ibSend = itemView.findViewById(R.id.ibSend);

        }

        public void bind(final Post post){

            //Intializing all the textboxes to show the correct information
            tvHandle.setText(post.getUser().getUsername());
//            if(isProfile){
//                tvHandle.setVisibility(View.GONE);
//                tvHandle2.setVisibility(View.GONE);
//                ivProfilePic.setVisibility(View.GONE);
//                tvDescription.setVisibility(View.GONE);
//            }

            //Some UI improvements
            if(!(post.getDescription().equals(""))) {
                tvHandle2.setText(post.getUser().getUsername());
                tvDescription.setText(post.getDescription());
            }else{
                tvHandle2.setVisibility(View.GONE);
                tvDescription.setVisibility(View.GONE);
            }
            tvTime.setText(getRelativeTimeAgo(post.getCreatedAt().toString()));
            ParseFile image = post.getImage();
            if(image != null){
                Glide.with(context)
                        .load(image.getUrl())
                        .into(ivImage);
            }

            //Setting up the profile pic
            ParseFile imageProfile = post.getUser().getParseFile("profilePic");
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
            if(imageProfile != null){
                Glide.with(context)
                        .load(imageProfile.getUrl())
                        .apply(requestOptions)
                        .into(ivProfilePic);
            }

            //Heart Button Operations
            listUsers = post.getLikesUsers();
            if(listUsers == null){
                listUsers = new ArrayList<>();
                post.put("usersLike",listUsers);
            }

            //Changing the view of the image button on load depending on whether on the the user has already liked the post
            ParseUser user = ParseUser.getCurrentUser();
            if(!(likedBy(user,post))){
                ibHeart.setImageResource(R.drawable.ufi_heart);
                ibHeart.setColorFilter(Color.BLACK);
            } else {
                ibHeart.setImageResource(R.drawable.ufi_heart_active);
                ibHeart.setColorFilter(Color.RED);
            }
            ibHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isLiked(post);
                }
            });

            //Detailed View onClick
            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, post_details.class);
                    intent.putExtra("postId", post.getObjectId());
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, ivImage, "postImage");
                    context.startActivity(intent, options.toBundle());
                }
            });

            //Detailed View onClick through comment button
            ibComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, post_details.class);
                    intent.putExtra("postId", post.getObjectId());
                    context.startActivity(intent);
                }
            });


        }

        //Set the image and changes the list of users that have liked a certain post
        private void isLiked(Post post) {
            ParseUser user = ParseUser.getCurrentUser();
            if(likedBy(user,post)){
                post.removeLike(user);
                Toast.makeText(itemView.getContext(), "Post unliked", Toast.LENGTH_SHORT).show();
                ibHeart.setImageResource(R.drawable.ufi_heart);
                ibHeart.setColorFilter(Color.BLACK);
                post.saveInBackground();
            }else {
                post.addLike(user);
                Toast.makeText(itemView.getContext(), "Post liked", Toast.LENGTH_SHORT).show();
                ibHeart.setImageResource(R.drawable.ufi_heart_active);
                ibHeart.setColorFilter(Color.RED);
                post.saveInBackground();
            }
        }

        //Checks if current user has liked a post
        public boolean likedBy(ParseUser user, Post post){
            List<ParseUser> usersLike = post.getLikesUsers();
            if (usersLike != null) {
                for (int i = 0; i < usersLike.size(); i++) {
                    if(user.getObjectId().equals(usersLike.get(i).getObjectId())){
                        return true;
                    }
                }
            }
            return false;
        }

        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            String relativeDate = "";
            try {
                long dateMillis = sf.parse(rawJsonDate).getTime();
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return relativeDate;
        }
    }
}
