package com.example.parstagram.fragments;

import android.util.Log;

import com.example.parstagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class ScrollFragment extends PostsFragment {

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        rvPosts = view.findViewById(R.id.rvPosts);
//
//        //create the data source
//        mPosts = new ArrayList<>();
//        // create the adapter
//        adapter = new PostsAdapter(getContext(),mPosts);
//        adapter.tvHandle.findViewById(R.id.tvHandle);
//        adapter.tvHandle.setVisibility(View.INVISIBLE);
//        //set the adapter on the recycler view
//        rvPosts.setAdapter(adapter);
//        //set the layout manager on the recyclerview
//        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
//
//        queryPosts();
//
//    }

    @Override
    protected void queryPosts() {
        //PostsAdapter.isProfile = true;
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
                    //Log.d("Post", "Post[" + i +"] = "
                           // + post.getDescription()
                            //+ "\nusername = " + post.getUser().getUsername());
                }
            }
        });
    }
}

