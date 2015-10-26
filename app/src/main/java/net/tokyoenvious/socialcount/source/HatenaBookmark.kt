package net.tokyoenvious.socialcount.source

import android.content.Intent
import android.net.Uri

import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

class HatenaBookmark(url: String) : Source(url) {

    @Throws(java.io.IOException::class)
    internal override fun fetchCountSync(): Int? {
        val uri = Uri.Builder().scheme("http").authority("api.b.st-hatena.com").path("/entry.count").appendQueryParameter("url", url).build()

        val request = Request.Builder().url(uri.toString()).build()

        val response = client.newCall(request).execute()
        val responseBody = response.body().string()
        if (responseBody.isEmpty()) {
            return 0
        } else {
            return Integer.decode(responseBody)
        }
    }

    override fun makeActionIntent(): Intent {
        return Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://b.hatena.ne.jp/entry.touch/" + url))
    }
}
