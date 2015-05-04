package net.tokyoenvious.socialcount.source;

import com.squareup.okhttp.OkHttpClient;

import android.util.Log;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

abstract class Source {
    protected final OkHttpClient client = new OkHttpClient();

    public Observable<Integer> fetchCount(final String url) {
        return Observable.create(
                (Subscriber<? super Integer> subscriber) -> {
                    Log.i(this.getClass().getName() + " fetchCount", url);

                    try {
                        Integer count = fetchCountSync(url);
                        subscriber.onNext(count);
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    abstract Integer fetchCountSync(String url) throws IOException;
}
