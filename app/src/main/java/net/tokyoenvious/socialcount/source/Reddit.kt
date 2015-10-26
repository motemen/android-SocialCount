package net.tokyoenvious.socialcount.source

import com.google.gson.Gson

import com.squareup.okhttp.Request
import com.squareup.okhttp.Response

import android.content.Intent
import android.net.Uri

import java.io.IOException

class Reddit(url: String) : Source(url) {
    private val gson = Gson()
    private var result: RedditInfoResult? = null

    @Throws(IOException::class)
    internal override fun fetchCountSync(): Int? {
        val uri = Uri.Builder().scheme("http").authority("www.reddit.com").path("/api/info.json").appendQueryParameter("url", url).build()

        val request = Request.Builder().url(uri.toString()).build()

        val response = client.newCall(request).execute()

        result = gson.fromJson(response.body().charStream(), RedditInfoResult::class.java)
        val totalScore = result?.data?.children?.sumBy {
            child -> child.data?.score ?: 0
        }
        return totalScore
    }

    private fun buildActionUri(): Uri {
        val itemPermalink: String? = result?.data?.children?.getOrNull(0)?.data?.permalink

        if (itemPermalink != null) {
            return Uri.Builder().scheme("http").authority("www.reddit.com").path(itemPermalink).build()
        } else {
            return Uri.Builder().scheme("http").authority("www.reddit.com").path("/submit").appendQueryParameter("url", url).build()
        }
    }

    override fun makeActionIntent(): Intent {
        return Intent(
                Intent.ACTION_VIEW,
                buildActionUri())
    }

    internal class RedditInfoResult {
        var data: RedditInfo? = null

        internal class RedditInfo {
            var children: Array<Child>? = null

            internal class Child {
                var data: Entry? = null

                internal class Entry {
                    var score: Int = 0
                    var permalink: String? = null
                }
            }
        }
    }
}
