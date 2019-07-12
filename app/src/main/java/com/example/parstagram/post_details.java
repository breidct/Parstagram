package com.example.parstagram;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.parstagram.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;

public class post_details extends AppCompatActivity {

    private ImageView ivPic;
    private ImageView ivProfile;
    private TextView tvHandle;
    private TextView tvHandle2;
    private TextView tvDescription;
    private RecyclerView rvComments;
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        initializeViews();

        Intent intent = getIntent();
        String postId = intent.getStringExtra("postId");

        final Post.Query postsQuery = new Post.Query();

        try{
            post = postsQuery.withUser().get(postId);
        }catch (ParseException e){
            Log.e("post_details","Could not get post");
            finish();
        }

        setViews();
    }

    private void setViews() {
        tvHandle.setText(post.getUser().getUsername());
        if(!(post.getDescription().equals(""))) {
            tvHandle2.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
        }else{
            tvHandle2.setVisibility(View.GONE);
            tvDescription.setVisibility(View.GONE);
        }
        ParseFile image = post.getImage();
        if(image != null){
            Glide.with(post_details.this)
                    .load(image.getUrl())
                    .into(ivPic);
        }
        ParseFile imageProfile = post.getUser().getParseFile("profilePic");
        RequestOptions requestOptions = new RequestOptions();
        requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
        if(imageProfile != null){
            Glide.with(post_details.this)
                    .load(imageProfile.getUrl())
                    .apply(requestOptions)
                    .into(ivProfile);
        }
    }

    private void initializeViews() {
        ivPic = findViewById(R.id.ivPic);
        ivProfile = findViewById(R.id.ivProfile);
        tvHandle = findViewById(R.id.tvHandle);
        tvHandle2 = findViewById(R.id.tvHandle2);
        tvDescription = findViewById(R.id.tvDescription);
        rvComments = findViewById(R.id.rvComments);
    }
}
