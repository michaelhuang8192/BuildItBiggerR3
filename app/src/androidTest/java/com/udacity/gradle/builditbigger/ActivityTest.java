package com.udacity.gradle.builditbigger;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;

import com.tinyappsdev.backend.myApi.model.MyJoke;
import com.tinyappsdev.jokeactivity.JokeActivity;

import org.junit.Assert;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Created by pk on 6/11/2016.
 */


public class ActivityTest
        extends ActivityInstrumentationTestCase2<MainActivity>
        implements Application.ActivityLifecycleCallbacks {

    private Object mJokeActivityResumedNotifier = new Object();
    private Activity mJokeActivity = null;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(activity instanceof JokeActivity) {
            synchronized (mJokeActivityResumedNotifier) {
                mJokeActivity = activity;
                mJokeActivityResumedNotifier.notifyAll();
            }
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    public ActivityTest() {
        super(MainActivity.class);
    }

    static class AsyncTaskRunnable implements Runnable {
        Object mLock = new Object();
        boolean isReady = false;
        MyJoke mMyJoke = null;
        Context mContext;

        @Override
        public void run() {
            new MainActivity.MyAsyncTask() {

                @Override
                protected void onPostExecute(MyJoke myJoke) {
                    mMyJoke = myJoke;

                    synchronized (mLock) {
                        isReady = true;
                        mLock.notifyAll();
                    }

                }

            }.execute(mContext.getString(R.string.backend_server));
        }
    };

    @Test
    public void testAsyncTaskLoadJokes() throws InterruptedException {
        final MainActivity ma = getActivity();

        AsyncTaskRunnable runnable = new AsyncTaskRunnable();
        runnable.mContext = ma;

        getInstrumentation().runOnMainSync(runnable);

        synchronized(runnable.mLock) {
            if(!runnable.isReady)
                runnable.mLock.wait(10000);
        }

        Assert.assertTrue(runnable.isReady);
        Assert.assertNotNull(runnable.mMyJoke);
        Assert.assertNotNull(runnable.mMyJoke.getJoke());
        Assert.assertTrue(runnable.mMyJoke.getJoke().length() > 0);

        //Assert.assertNotNull(null);

    }

    @Test
    public void testGetJokes() throws IOException {
        MainActivity mainActivity = getActivity();

        List<MyJoke> jokes;
        Random random = new Random(System.currentTimeMillis());
        for(int k = 0; k < 3; k++) {
            System.out.println(String.format(">>>>Phase #%d", k + 1));
            int num = random.nextInt(20) + 1;

            jokes = mainActivity.getJokesFromServer(
                    mainActivity.getString(R.string.backend_server),
                    num
            );
            Assert.assertEquals("Should be " + num, num, jokes.size());
            for (int i = 0; i < jokes.size(); i++) {
                Assert.assertNotNull("Should not be null", jokes.get(i).getJoke());
                System.out.println(String.format("Joke[%s]: %s", i, jokes.get(i).getJoke()));
            }
        }

    }

    @Test
    public void testActivity() throws IOException, InterruptedException {
        final MainActivity ma = getActivity();

        ma.getApplication().registerActivityLifecycleCallbacks(this);

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ma.loadJokes();
            }
        });

        synchronized (mJokeActivityResumedNotifier) {
            if(mJokeActivity == null)
                mJokeActivityResumedNotifier.wait(10000);
        }

        Assert.assertNotNull(mJokeActivity);

        String joke = null;
        try {
            joke = mJokeActivity.getIntent().getExtras().getString("joke");
        } catch(Exception e) {
        }

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                mJokeActivity.finish();
            }
        });

        Assert.assertNotNull(joke);
        Assert.assertTrue(joke.length() > 0);
    }

}
