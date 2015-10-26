package net.tokyoenvious.socialcount.source

import com.squareup.okhttp.OkHttpClient

import android.content.Intent
import android.net.Uri
import android.util.Log

import java.io.IOException

import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

abstract class Source internal constructor(internal var url:

                                           String) {
    protected val client = OkHttpClient()

    fun fetchCount(): Observable<Int> {
        return Observable.create { subscriber: Subscriber<in Int> ->
            Log.i(this.javaClass.name + " fetchCount", url)

            try {
                val count = fetchCountSync()
                subscriber.onNext(count)
            } catch (e: IOException) {
                subscriber.onError(e)
            }
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    @Throws(IOException::class)
    internal abstract fun fetchCountSync(): Int?

    abstract fun makeActionIntent(): Intent
}
