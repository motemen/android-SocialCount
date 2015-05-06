package net.tokyoenvious.socialcount;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.tokyoenvious.socialcount.source.HatenaBookmark;
import net.tokyoenvious.socialcount.source.Reddit;
import net.tokyoenvious.socialcount.source.Twitter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.android.app.AppObservable;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends Activity {
    @InjectView(R.id.hatenaBookmarkCount)
    TextView hatenaBookmarkTextView;

    @InjectView(R.id.twitterCount)
    TextView twitterTextView;

    @InjectView(R.id.redditCount)
    TextView redditTextView;

    @InjectView(R.id.backgroundOverlay)
    LinearLayout backgroundOverlay;

    @InjectView(R.id.hatenaBookmarkLogo)
    ImageView hatenaBookmarkLogo;

    @InjectView(R.id.twitterLogo)
    ImageView twitterLogo;

    @InjectView(R.id.redditLogo)
    ImageView redditLogo;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(0, R.anim.abc_slide_out_top);
    }

    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.abc_slide_in_top, 0);

        ButterKnife.inject(this);

        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            url = intent.getStringExtra(Intent.EXTRA_TEXT);
        } else {
            url = "http://www.example.com/";
        }

        startFetchers();
    }

    class ViewCrossfader {
        View toBeShown;
        View toBeHidden;
        int duration;

        ViewCrossfader(View toBeShown, View toBeHidden, int duration) {
            this.toBeShown  = toBeShown;
            this.toBeHidden = toBeHidden;
            this.duration   = duration;

            toBeShown.setAlpha(0f);
        }

        void start() {
            toBeShown.animate()
                    .alpha(1f)
                    .setDuration(duration)
                    .setListener(null);

            toBeHidden.animate()
                    .alpha(0f)
                    .setDuration(duration)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            toBeHidden.setVisibility(View.GONE);
                        }
                    });
        }
    }

    private CompositeSubscription fetchers = new CompositeSubscription();

    private void startFetchers() {
        int duration = getResources().getInteger(android.R.integer.config_longAnimTime);

        ViewCrossfader hatenaBookmarkFader = new ViewCrossfader(hatenaBookmarkTextView, hatenaBookmarkLogo, duration);
        ViewCrossfader twitterFader        = new ViewCrossfader(twitterTextView, twitterLogo, duration);
        ViewCrossfader redditFader         = new ViewCrossfader(redditTextView, redditLogo, duration);

        fetchers.add(
                AppObservable.bindActivity(this, new HatenaBookmark().fetchCount(url))
                        .subscribe(
                                count -> {
                                    hatenaBookmarkTextView.setText(count.toString());
                                    hatenaBookmarkFader.start();
                                },
                                // TODO: toast
                                throwable -> Log.e("main", throwable.getMessage())
                        )
        );

        fetchers.add(
                AppObservable.bindActivity(this, new Twitter().fetchCount(url))
                        .subscribe(
                                count -> {
                                    twitterTextView.setText(count.toString());
                                    twitterFader.start();
                                },
                                throwable -> Log.e("main", throwable.getMessage())
                        )
        );

        fetchers.add(
                AppObservable.bindActivity(this, new Reddit().fetchCount(url))
                        .subscribe(
                                count -> {
                                    redditTextView.setText(count.toString());
                                    redditFader.start();
                                },
                                throwable -> Log.e("main", throwable.getMessage())
                        )
        );
    }

    @Override
    protected void onDestroy() {
        fetchers.unsubscribe();

        super.onDestroy();
    }

    @OnClick(R.id.hatenaBookmarkCount)
    public void showHatenaBookmark(View view) {
        Intent hatenaBookmarkEntryIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://b.hatena.ne.jp/entry.touch/" + url)
        );
        startActivity(hatenaBookmarkEntryIntent);
    }

    @OnClick(R.id.twitterCount)
    public void showTwitter(View view) {
        Intent twitterSearchIntent = new Intent(
                Intent.ACTION_VIEW,
                new Uri.Builder()
                        .scheme("https")
                        .authority("twitter.com")
                        .path("/search")
                        .appendQueryParameter("q", url)
                        .build()
        );
        startActivity(twitterSearchIntent);
    }

    @OnClick(R.id.redditCount)
    public void showReddit(View view) {
        // TODO: if submission was only one, open it
        // TODO: URIs must go models
        Intent redditSearchIntent = new Intent(
                Intent.ACTION_VIEW,
                new Uri.Builder()
                        .scheme("http")
                        .authority("www.reddit.com")
                        .path("/submit")
                        .appendQueryParameter("url", url)
                        .build()
        );
        startActivity(redditSearchIntent);
    }

    @OnClick(R.id.backgroundOverlay)
    public void onBackgroundOverlayClicked(View view) {
        onBackPressed();
    }
}
