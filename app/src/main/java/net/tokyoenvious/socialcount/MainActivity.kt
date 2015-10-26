package net.tokyoenvious.socialcount

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast

import net.tokyoenvious.socialcount.source.HatenaBookmark
import net.tokyoenvious.socialcount.source.Pocket
import net.tokyoenvious.socialcount.source.Reddit
import net.tokyoenvious.socialcount.source.Source
import net.tokyoenvious.socialcount.source.Twitter

import java.util.regex.Matcher

import butterknife.ButterKnife
import butterknife.OnClick
import rx.android.app.AppObservable
import rx.subscriptions.CompositeSubscription

class MainActivity : Activity() {
    internal var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        overridePendingTransition(R.anim.abc_slide_in_top, 0)

        ButterKnife.inject(this)

        val intent = intent
        if (Intent.ACTION_SEND == intent.action) {
            val matcher = Patterns.WEB_URL.matcher(intent.getStringExtra(Intent.EXTRA_TEXT))
            if (matcher.find()) {
                url = matcher.group()
            } else {
                url = null
            }
        } else {
            url = "http://www.example.com/"
        }

        initializeSources()
    }

    override fun onDestroy() {
        fetchers.unsubscribe()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.abc_slide_out_top)
    }

    @OnClick(R.id.backgroundOverlay)
    fun onBackgroundOverlayClicked(view: View) {
        onBackPressed()
    }

    internal inner class ViewCrossfader(var toBeShown: View, var toBeHidden: View, var duration: Int) {

        init {

            toBeShown.alpha = 0f
        }

        fun start() {
            toBeShown.animate().alpha(1f).setDuration(duration.toLong()).setListener(null)

            toBeHidden.animate().alpha(0f).setDuration(duration.toLong()).setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    toBeHidden.visibility = View.GONE
                }
            })
        }
    }

    private val fetchers = CompositeSubscription()

    private fun initializeSources() {
        SourceView(HatenaBookmark(url), R.id.hatenaBookmarkCount, R.id.hatenaBookmarkLogo).start()
        SourceView(Twitter(url), R.id.twitterCount, R.id.twitterLogo).start()
        SourceView(Reddit(url), R.id.redditCount, R.id.redditLogo).start()
        SourceView(Pocket(url), R.id.pocketCount, R.id.pocketLogo).start()
    }

    internal inner class SourceView<T : Source>(var source: T, countViewId: Int, placeholderViewId: Int) {
        var countView: TextView
        var placeholderView: View

        init {
            this.countView = this@MainActivity.findViewById(countViewId) as TextView
            this.placeholderView = this@MainActivity.findViewById(placeholderViewId)

            addEventListeners()
        }

        private fun addEventListeners() {
            this.countView.setOnClickListener { v ->
                val intent = source.makeActionIntent()
                startActivity(intent)
            }
        }

        fun start() {
            val duration = resources.getInteger(android.R.integer.config_longAnimTime)

            val fader = ViewCrossfader(countView, placeholderView, duration)

            fetchers.add(
                    AppObservable.bindActivity(this@MainActivity, source.fetchCount()).subscribe(
                            { count ->
                                countView.text = count!!.toString()
                                fader.start()
                            }
                    ) { throwable ->
                        Log.e("main", throwable.getMessage())
                        Toast.makeText(this@MainActivity, throwable.getMessage(), Toast.LENGTH_SHORT).show()
                    })
        }
    }
}
