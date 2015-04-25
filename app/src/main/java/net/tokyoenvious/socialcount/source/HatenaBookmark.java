package net.tokyoenvious.socialcount.source;

import android.net.Uri;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class HatenaBookmark {
    OkHttpClient client = new OkHttpClient();

    int fetchCount(String url) throws java.io.IOException {
        Uri uri = new Uri.Builder()
                .scheme("http")
                .authority("api.b.st-hatena.com")
                .path("/entry.count")
                .appendQueryParameter("url", url)
                .build();

        Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        Response response = client.newCall(request).execute();
        return Integer.parseInt(response.body().string());
    }
}
