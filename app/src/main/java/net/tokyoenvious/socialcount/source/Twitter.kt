package net.tokyoenvious.socialcount.source

import com.google.gson.Gson

import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

import android.content.Intent
import android.net.Uri

import java.io.IOException

class Twitter(url: String) : Source(url) {
    private val gson = Gson()

    @Throws(IOException::class)
    internal override fun fetchCountSync(): Int? {
        val uri = Uri.Builder().scheme("https").authority("cdn.syndication.twitter.com").path("/widgets/tweetbutton/count.json").appendQueryParameter("url", url).build()

        val request = Request.Builder().url(uri.toString()).build()

        val response = client.newCall(request).execute()

        val count = gson.fromJson(response.body().charStream(), TwitterCount::class.java)
        return count.count
    }

    internal class TwitterCount {
        var count: Int? = null
        var url: String? = null
    }

    override fun makeActionIntent(): Intent {
        val uri = Uri.Builder().scheme("https").authority("twitter.com").path("/search").appendQueryParameter("q", url).build()
        return Intent(Intent.ACTION_VIEW, uri)
    }
}
