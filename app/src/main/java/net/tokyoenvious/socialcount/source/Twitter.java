package net.tokyoenvious.socialcount.source;

import com.google.gson.Gson;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.net.Uri;

import java.io.IOException;

public class Twitter extends Source {
    private final Gson gson = new Gson();

    public Twitter(String url) {
        super(url);
    }

    @Override
    Integer fetchCountSync() throws IOException {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("cdn.syndication.twitter.com")
                .path("/widgets/tweetbutton/count.json")
                .appendQueryParameter("url", url)
                .build();

        Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        Response response = client.newCall(request).execute();

        TwitterCount count = gson.fromJson(response.body().charStream(), TwitterCount.class);
        return count.count;
    }

    static class TwitterCount {
        Integer count;
        String url;
    }

    @Override
    public Uri getUri() {
        return new Uri.Builder()
                .scheme("https")
                .authority("twitter.com")
                .path("/search")
                .appendQueryParameter("q", url)
                .build();
    }
}
