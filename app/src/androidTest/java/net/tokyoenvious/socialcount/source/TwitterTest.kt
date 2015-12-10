package net.tokyoenvious.socialcount.source

import org.junit.Test
import org.junit.runner.RunWith

import junit.framework.Assert.assertTrue

import android.support.test.runner.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class TwitterTest {
    @Test
    @Throws(java.io.IOException::class)
    fun fetchCountSync() {
        val count = Twitter("http://www.example.com/").fetchCountSync()
        assertTrue(count!! > 0)
    }
}
