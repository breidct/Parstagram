package com.example.parstagram.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.parstagram.GridAdapter;
import com.example.parstagram.R;
import com.example.parstagram.model.EndlessRecyclerViewScrollListener;
import com.example.parstagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class GridFragment extends Fragment {

    public static final String TAG = "PostsFragment";

    protected RecyclerView rvPosts;
    protected GridAdapter adapter;
    protected List<Post> mPosts;
    protected SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
    //onCreateView to inflate view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                queryPosts();
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);



        rvPosts = view.findViewById(R.id.rvPosts);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);

        //create the data source
        mPosts = new ArrayList<>();
        // create the adapter
        adapter = new GridAdapter(getContext(), mPosts);
        //set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        //set the layout manager on the recyclerview
        rvPosts.setLayoutManager(gridLayoutManager);

//        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
//            @Override
//            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//                // Triggered only when new data needs to be appended to the list
//                // Add whatever code is needed to append new items to the bottom of the list
//                queryPosts();
//            }
//        };
//        rvPosts.addOnScrollListener(scrollListener);
        queryPosts();

    }



    protected void queryPosts(){
        Post.Query postQuery = new Post.Query();
        postQuery.getTop().withUser();
        postQuery.setLimit(20);
        postQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        postQuery.addDescendingOrder(Post.KEY_CREATED_AT);
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e != null){
                    Log.e(TAG,"Error with query");
                    e.printStackTrace();
                    return;

                }
                mPosts.addAll(posts);
                adapter.notifyDataSetChanged();

                for(int i = 0; i < posts.size(); i++) {
                    Post post = posts.get(i);
                    Log.d("Post", "Post[" + i +"] = "
                            + post.getDescription()
                            + "\nusername = " + post.getUser().getUsername());
                }
            }
        });
    }

}
