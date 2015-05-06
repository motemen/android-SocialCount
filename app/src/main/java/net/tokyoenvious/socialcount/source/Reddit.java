package net.tokyoenvious.socialcount.source;

import com.google.gson.Gson;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.net.Uri;

import java.io.IOException;

public class Reddit extends Source {
    private final Gson gson = new Gson();
    private RedditInfoResult result;

    public Reddit(String url) {
        super(url);
    }

    @Override
    Integer fetchCountSync() throws IOException {
        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("www.reddit.com")
                .path("/api/info.json")
                .appendQueryParameter("url", url)
                .build();

        Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        Response response = client.newCall(request).execute();

        result = gson.fromJson(response.body().charStream(), RedditInfoResult.class);
        int totalScore = 0;
        for (RedditInfoResult.RedditInfo.Child child: result.data.children) {
            totalScore += child.data.score;
        }
        return totalScore;
    }

    @Override
    public Uri getUri() {
        if (result != null && result.data.children.length == 1) {
            return new Uri.Builder()
                    .scheme("http")
                    .authority("www.reddit.com")
                    .path(result.data.children[0].data.permalink)
                    .build();
        } else {
            return new Uri.Builder()
                    .scheme("http")
                    .authority("www.reddit.com")
                    .path("/submit")
                    .appendQueryParameter("url", url)
                    .build();
        }
    }

    static class RedditInfoResult {
        RedditInfo data;

        static class RedditInfo {
            Child[] children;

            static class Child {
                Entry data;

                static class Entry {
                    int score;
                    String permalink;
                }
            }
        }
    }
}
