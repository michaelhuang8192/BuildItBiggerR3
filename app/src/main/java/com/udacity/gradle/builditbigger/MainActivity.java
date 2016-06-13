package com.udacity.gradle.builditbigger;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.tinyappsdev.backend.myApi.MyApi;
import com.tinyappsdev.backend.myApi.model.MyJoke;
import com.tinyappsdev.jokeactivity.JokeActivity;

import java.io.IOException;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private static MyApi sMyApiService;
    private AsyncTask mAsyncTask;
    private Context mContext;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;

        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onJokeReady(MyJoke myJoke) {
        if(myJoke == null) {
            Toast.makeText(
                    mContext,
                    getString(R.string.no_joke_available),
                    Toast.LENGTH_LONG
            ).show();

        } else {
            Intent intent = new Intent(mContext, JokeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("joke", myJoke.getJoke());
            intent.putExtras(bundle);
            startActivity(intent);
        }

        mProgressBar.setVisibility(View.GONE);
    }

    protected void loadJokes() {
        if(mAsyncTask != null && mAsyncTask.getStatus() != AsyncTask.Status.FINISHED ) return;

        mProgressBar.setVisibility(View.VISIBLE);
        mAsyncTask = new MyAsyncTask() {
            @Override
            protected void onCancelled(MyJoke myJoke) {
                super.onCancelled(myJoke);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            protected void onPostExecute(MyJoke myJoke) {
                onJokeReady(myJoke);
            }

        }.execute(getString(R.string.backend_server));

    }

    static public List<MyJoke> getJokesFromServer(String server, int num) throws IOException {
        if(sMyApiService == null) {
            MyApi.Builder builder = new MyApi.Builder(
                    AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(),
                    null
            ).setRootUrl(String.format("http://%s:8080/_ah/api/", server));

            sMyApiService = builder.build();
        }

        return sMyApiService.getJokes().setNum(num).execute().getItems();
    }

    static public class MyAsyncTask extends AsyncTask<String, Object, MyJoke> {
        @Override
        protected MyJoke doInBackground(String... params) {
            try {
                System.out.println(params[0]);
                List<MyJoke> jokes = getJokesFromServer(params[0], 1);
                if(jokes == null || jokes.size() == 0) return null;
                return jokes.get(0);

            } catch(IOException e) {
                e.printStackTrace();
            }

            return null;
        }

    }

}
