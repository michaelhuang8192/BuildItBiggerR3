/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.tinyappsdev.backend;

import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.tinyappsdev.backend.myApi.MyApi;
import com.tinyappsdev.backend.myApi.model.MyJoke;

import org.junit.Test;
import org.junit.Assert;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MyEndpointTest {

    @Test
    public void test() throws IOException {
        MyApi.Builder builder = new MyApi.Builder(
                new ApacheHttpTransport(),
                new JacksonFactory(),
                null)
                .setRootUrl("http://127.0.0.1:8080/_ah/api/")
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
            });

        MyApi myApiService = builder.build();


        List<MyJoke> jokes;
        Random random = new Random(System.currentTimeMillis());
        for(int k = 0; k < 3; k++) {
            System.out.println(String.format(">>>>Phase #%d", k + 1));
            int num = random.nextInt(20) + 1;

            jokes = myApiService.getJokes().setNum(num).execute().getItems();
            Assert.assertEquals("Should be " + num, num, jokes.size());
            for (int i = 0; i < jokes.size(); i++) {
                Assert.assertNotNull("Should not be null", jokes.get(i).getJoke());
                System.out.println(String.format("Joke[%s]: %s", i, jokes.get(i).getJoke()));
            }
        }
       // Assert.assertNotNull(null);

    }

}
