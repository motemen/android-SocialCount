package net.tokyoenvious.socialcount;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.tokyoenvious.socialcount.source.HatenaBookmark;
import net.tokyoenvious.socialcount.source.Pocket;
import net.tokyoenvious.socialcount.source.Reddit;
import net.tokyoenvious.socialcount.source.Source;
import net.tokyoenvious.socialcount.source.Twitter;

import java.util.regex.Matcher;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.app.AppObservable;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends Activity {
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        overridePendingTransition(R.anim.abc_slide_in_top, 0);

        ButterKnife.inject(this);

        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            Matcher matcher = Patterns.WEB_URL.matcher(intent.getStringExtra(Intent.EXTRA_TEXT));
            if (matcher.find()) {
                url = matcher.group();
            } else {
                url = null;
            }
        } else {
            url = "http://www.example.com/";
        }

        initializeSources();
    }

    @Override
    protected void onDestroy() {
        fetchers.unsubscribe();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.abc_slide_out_top);
    }

    @OnClick(R.id.backgroundOverlay)
    public void onBackgroundOverlayClicked(View view) {
        onBackPressed();
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

    private void initializeSources() {
        new SourceView<>(new HatenaBookmark(url), R.id.hatenaBookmarkCount, R.id.hatenaBookmarkLogo).start();
        new SourceView<>(new Twitter(url), R.id.twitterCount, R.id.twitterLogo).start();
        new SourceView<>(new Reddit(url), R.id.redditCount, R.id.redditLogo).start();
        new SourceView<>(new Pocket(url), R.id.pocketCount, R.id.pocketLogo).start();
    }

    class SourceView<T extends Source> {
        T source;
        TextView countView;
        View placeholderView;

        SourceView(T source, int countViewId, int placeholderViewId) {
            this.source = source;
            this.countView = (TextView)MainActivity.this.findViewById(countViewId);
            this.placeholderView = MainActivity.this.findViewById(placeholderViewId);

            addEventListeners();
        }

        private void addEventListeners() {
            this.countView.setOnClickListener(
                    v -> {
                        Intent intent = new Intent(Intent.ACTION_VIEW, source.getUri());
                        startActivity(intent);
                    }
            );
        }

        public void start() {
            int duration = getResources().getInteger(android.R.integer.config_longAnimTime);

            ViewCrossfader fader = new ViewCrossfader(countView, placeholderView, duration);

            fetchers.add(
                    AppObservable.bindActivity(MainActivity.this, source.fetchCount())
                            .subscribe(
                                    count -> {
                                        countView.setText(count.toString());
                                        fader.start();
                                    },
                                    throwable -> {
                                        Log.e("main", throwable.getMessage());
                                        Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                            )
            );
        }
    }
}
