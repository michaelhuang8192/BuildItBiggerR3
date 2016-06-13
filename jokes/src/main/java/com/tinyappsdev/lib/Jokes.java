package com.tinyappsdev.lib;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.GZIPInputStream;


public class Jokes {

    public final static String JOKES_API = "http://api.icndb.com/jokes";

    private static String[] sJokes;
    private static STATE sState = STATE.NONE;
    private static Object sLock = new Object();
    private static Random sRandom = new Random(System.currentTimeMillis());

    enum STATE {
        ERROR,
        NONE,
        LOADING,
        READY
    };

    {
        //loadJokes();
    }

    protected static void loadJokes() {
        synchronized (sLock) {
            if(sState != STATE.NONE && sState != STATE.ERROR) return;
            sState = STATE.LOADING;
        }

        fetchJokesFromRemoteServer();
    }

    public static boolean isReady() {
        return sState == STATE.READY;
    }

    /*
    get random jokes from cache
     */
    public static String[] getJokes(int num) {
        return getJokes(num, true);
    }

    public static String[] getJokes(int num, boolean wait) {
        if(!isReady()) {
            loadJokes();
            if(!isReady()) {
                if (!wait) return null;

                synchronized (sLock) {
                    try {
                        if (sState == STATE.LOADING) sLock.wait();

                    } catch (InterruptedException e) {
                        return null;
                    }
                }
                if (!isReady()) return null;
            }
        }

        String[] jokes = sJokes;
        int sz = jokes.length;
        num = Math.min(num, sz);
        String[] rand_jokes = new String[num];

        for(int i = 0; i < num; i++) {
            rand_jokes[i] = jokes[sRandom.nextInt(sz)];
        }

        return rand_jokes;
    }


    protected static String getContentFromRemoteServer(String url) throws IOException {
        HttpURLConnection conn = null;
        conn = (HttpURLConnection) (new URL(JOKES_API)).openConnection();

        conn.setRequestProperty("Accept-Encoding", "gzip");

        String enc = conn.getContentEncoding();
        InputStream ins = conn.getInputStream();
        if(enc != null && enc.toLowerCase().indexOf("gzip") >= 0) {
            ins = new GZIPInputStream(ins);
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = rd.readLine()) != null)
            result.append(line);
        rd.close();

        return result.toString();
    }

    protected static void fetchJokesFromRemoteServer() {
        //System.out.println(">fetchJokesFromRemoteServer ...");

        try {
            String res = getContentFromRemoteServer(JOKES_API);

            Gson gson = new Gson();
            List list = (List)gson.fromJson(res, Map.class).get("value");
            String[] jokes = new String[list.size()];
            for(int i = 0; i < list.size(); i++) {
                jokes[i] = (String)((Map)list.get(i)).get("joke");
            }
            sJokes = jokes;

            synchronized (sLock) {
                sState = STATE.READY;
                sLock.notifyAll();
            }

        } catch(JsonSyntaxException | IOException e) {
            e.printStackTrace();

        } finally {

            synchronized (sLock) {
                if(sState == STATE.LOADING) {
                    sState = STATE.ERROR;
                    sLock.notifyAll();
                }
            }

        }

    }

}
