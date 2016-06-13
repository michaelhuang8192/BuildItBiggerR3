package com.tinyappsdev.lib;


import org.junit.Test;
import org.junit.Assert;

/**
 * Created by pk on 6/9/2016.
 */
public class JokesTest {


    @Test
    public void test() throws InterruptedException {

        Assert.assertTrue("Should Get One Joke", Jokes.getJokes(1).length == 1);

        Assert.assertTrue("Should Get Three Jokes", Jokes.getJokes(3).length == 3);

        System.out.println( Jokes.getJokes(1)[0] );

    }


}
