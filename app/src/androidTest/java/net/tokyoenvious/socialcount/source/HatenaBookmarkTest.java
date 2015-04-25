package net.tokyoenvious.socialcount.source;

import junit.framework.TestCase;

public class HatenaBookmarkTest extends TestCase {
    public void testFetchCount() throws java.io.IOException {
        int count = new HatenaBookmark().fetchCount("http://www.example.com/");
        assertTrue(count > 0);
    }
}
