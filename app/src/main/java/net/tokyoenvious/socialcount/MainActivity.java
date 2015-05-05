package net.tokyoenvious.socialcount;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.transition.TransitionManager;
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
            url = null;
        }

        Uri data = intent.getData();
        Log.d("data", data == null ? "(null)" : data.toString());

        assert(url != null);

        startFetches();
    }

    private void startFetches() {
        int shortAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

        hatenaBookmarkTextView.setAlpha(0f);
        Log.d("startFetches", "alpha=0");


        AppObservable.bindActivity(this, new HatenaBookmark().fetchCount(url))
                .subscribe(
                        count -> {
                            Log.d("startFetches", "got");

                            hatenaBookmarkTextView.animate()
                                    .alpha(1f)
                                    .setDuration(shortAnimationDuration)
                                    .setListener(null);

                            hatenaBookmarkLogo.animate()
                                    .alpha(0f)
                                    .setDuration(shortAnimationDuration)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            hatenaBookmarkLogo.setVisibility(View.GONE);
                                        }
                                    });

                            hatenaBookmarkTextView.setText(count.toString());
                        },
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
