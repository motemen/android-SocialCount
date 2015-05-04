package net.tokyoenvious.socialcount.source;

import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;

import android.support.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class TwitterTest {
    @Test
    public void fetchCountSync() throws java.io.IOException {
        Integer count = new Twitter().fetchCountSync("http://www.example.com/");
        assertTrue(count > 0);
    }
}
