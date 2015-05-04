package net.tokyoenvious.socialcount;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.TextView;

import net.tokyoenvious.socialcount.source.HatenaBookmark;
import net.tokyoenvious.socialcount.source.Reddit;
import net.tokyoenvious.socialcount.source.Twitter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.app.AppObservable;
import rx.functions.Action1;

public class MainActivity extends Activity {
    @InjectView(R.id.textViewHatenaBookmark)
    TextView hatenaBookmarkTextView;

    @InjectView(R.id.textViewTwitter)
    TextView twitterTextView;

    @InjectView(R.id.textViewReddit)
    TextView redditTextView;

    @Override
    protected void onPause() {
        super.onPause();

        overridePendingTransition(0, R.anim.abc_slide_out_top);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.abc_slide_in_top, 0);

        ButterKnife.inject(this);

        String url = null;

        Intent intent = getIntent();
        Log.d("action", intent.getAction());
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            url = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        assert(url != null);

        AppObservable.bindActivity(this, new HatenaBookmark().fetchCount(url))
                .subscribe(
                        count -> hatenaBookmarkTextView.setText(count.toString()),
                        throwable -> Log.e("main", throwable.getMessage())
                );

        AppObservable.bindActivity(this, new Twitter().fetchCount(url))
                .subscribe(
                        count -> twitterTextView.setText(count.toString()),
                        throwable -> Log.e("main", throwable.getMessage())
                );

        AppObservable.bindActivity(this, new Reddit().fetchCount(url))
                .subscribe(
                        count -> redditTextView.setText(count.toString()),
                        throwable -> Log.e("main", throwable.getMessage())
                );
    }
}
