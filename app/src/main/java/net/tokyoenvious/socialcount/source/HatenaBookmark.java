package net.tokyoenvious.socialcount.source;

import android.net.Uri;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HatenaBookmark extends Source {
    public HatenaBookmark(String url) {
        super(url);
    }

    @Override
    Integer fetchCountSync() throws java.io.IOException {
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
        String responseBody = response.body().string();
        if (responseBody.isEmpty()) {
            return 0;
        } else {
            return Integer.decode(responseBody);
        }
    }

    @Override
    public Uri getUri() {
        return Uri.parse("http://b.hatena.ne.jp/entry.touch/" + url);
    }
}
