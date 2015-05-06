package net.tokyoenvious.socialcount.source;

import com.squareup.okhttp.OkHttpClient;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class Source {
    protected final OkHttpClient client = new OkHttpClient();

    String url;

    Source(String url) {
        this.url = url;
    }

    public Observable<Integer> fetchCount() {
        return Observable.create(
                (Subscriber<? super Integer> subscriber) -> {
                    Log.i(this.getClass().getName() + " fetchCount", url);

                    try {
                        Integer count = fetchCountSync();
                        subscriber.onNext(count);
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    abstract Integer fetchCountSync() throws IOException;

    abstract public Uri getUri();
}
