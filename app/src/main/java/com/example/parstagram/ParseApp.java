package com.example.parstagram;

import android.app.Application;

import com.example.parstagram.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("brandon-reid")
                .clientKey("fbu-2019")
                .server("http://breidct-fbu-instagram.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
