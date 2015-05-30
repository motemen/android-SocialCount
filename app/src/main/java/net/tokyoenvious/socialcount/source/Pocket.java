package net.tokyoenvious.socialcount.source;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import android.content.Intent;
import android.net.Uri;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Pocket extends Source {
    public Pocket(String url) { super(url); }

    @Override
    Integer fetchCountSync() throws IOException {
        Uri uri = new Uri.Builder()
                .scheme("https")
                .authority("widgets.getpocket.com")
                .path("/v1/button")
                .appendQueryParameter("count", "horizontal")
                .appendQueryParameter("v", "1")
                .appendQueryParameter("url", url)
                .build();

        Request request = new Request.Builder()
                .url(uri.toString())
                .build();

        Response response = client.newCall(request).execute();
        Pattern p = Pattern.compile("<em id=\"cnt\">(\\d+)</em>");
        Matcher m = p.matcher(response.body().string());
        if (m.find()) {
            return Integer.decode(m.group(1));
        }

        return 0;
    }

    @Override
    public Intent makeActionIntent() {
        Intent intent = new Intent();
        intent.setClassName("com.ideashower.readitlater.pro", "com.ideashower.readitlater.activity.AddActivity");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        return intent;
    }
}
