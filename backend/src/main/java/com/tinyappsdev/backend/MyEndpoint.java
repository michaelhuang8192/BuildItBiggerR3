/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.tinyappsdev.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.DefaultValue;
import com.tinyappsdev.lib.Jokes;

import javax.inject.Named;

/** An endpoint class we are exposing */
@Api(
  name = "myApi",
  version = "v1",
  namespace = @ApiNamespace(
    ownerDomain = "backend.tinyappsdev.com",
    ownerName = "backend.tinyappsdev.com",
    packagePath=""
  )
)
public class MyEndpoint {

    /** A simple endpoint method that takes a name and says Hi back */
    @ApiMethod(name = "getJokes", httpMethod = ApiMethod.HttpMethod.GET)
    public MyJoke[] getJokes(@Named("num") @DefaultValue("1") int num) {

        String[] jokes = Jokes.getJokes(num);
        MyJoke[] myJokes = new MyJoke[jokes.length];

        for(int i = 0; i < jokes.length; i++) {
            MyJoke myJoke = myJokes[i] = new MyJoke();
            myJoke.setJoke(jokes[i]);
        }

        return myJokes;
    }

}
