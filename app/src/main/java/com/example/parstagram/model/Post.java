package com.example.parstagram.model;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Post")
public class Post extends ParseObject {
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_AT = "createdAt";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_USER_LIKE = "usersLike";


    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }

    public void setDescription(String description){
        put(KEY_DESCRIPTION,description);
    }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile image){
        put(KEY_IMAGE,image);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER,user);
    }


    public List <ParseUser> getLikesUsers(){
        return getList(KEY_USER_LIKE);
    }

    public void addLike(ParseUser user){
        List<ParseUser> likeUsers = getLikesUsers();
        likeUsers.add(user);
        this.put(KEY_USER_LIKE, likeUsers);
    }

    public void removeLike(ParseUser user){
        int position = 0;
        List<ParseUser> likeUsers = getLikesUsers();
        for(int i = 0; i < likeUsers.size(); i++){
            if(user.getObjectId().equals(likeUsers.get(i).getObjectId())){
                position = i;
            }
        }
        likeUsers.remove(position);
        this.put(KEY_USER_LIKE,likeUsers);
    }

    public int getLikes(){
        List<ParseUser> likeUsers = getLikesUsers();
        return likeUsers.size();
    }

    public void queryPosts(){
        ParseQuery<Post> postQuery = new ParseQuery<Post>(Post.class);
        postQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if(e == null){
                    for(int i = 0; i < posts.size(); i++) {
                        Log.d("Post", "Post[" + i +"] = "
                                + posts.get(i).getDescription()
                                + "\nusername = " + posts.get(i).getUser().getUsername());
                    }
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        public Query getTop(){
            setLimit(20);
            return this;
        }

        public Query withUser(){
            include("user");
            return this;
        }
    }
}
