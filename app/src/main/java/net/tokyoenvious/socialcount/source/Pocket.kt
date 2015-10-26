package net.tokyoenvious.socialcount.source

import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

import android.content.Intent
import android.net.Uri

import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern

class Pocket(url: String) : Source(url) {

    @Throws(IOException::class)
    internal override fun fetchCountSync(): Int? {
        val uri = Uri.Builder().scheme("https").authority("widgets.getpocket.com").path("/v1/button").appendQueryParameter("count", "horizontal").appendQueryParameter("v", "1").appendQueryParameter("url", url).build()

        val request = Request.Builder().url(uri.toString()).build()

        val response = client.newCall(request).execute()
        val p = Pattern.compile("<em id=\"cnt\">(\\d+)</em>")
        val m = p.matcher(response.body().string())
        if (m.find()) {
            return Integer.decode(m.group(1))
        }

        return 0
    }

    override fun makeActionIntent(): Intent {
        val intent = Intent()
        intent.setClassName("com.ideashower.readitlater.pro", "com.ideashower.readitlater.activity.AddActivity")
        intent.putExtra(Intent.EXTRA_TEXT, url)
        return intent
    }
}
