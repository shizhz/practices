package org.shizhz.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class JoinerTest {

    @Test
    public void testJoinStringSet() {
        assertEquals("", Joiner.on(",").join(null));
        assertEquals("", Joiner.on(",").join(new String[] {}));
        assertEquals(",", Joiner.on(",").join(new String[] { "", "" }));
        assertEquals("A", Joiner.on("-").join(new String[] { "A" }));
        assertEquals("A-B-C",
                Joiner.on("-").join(new String[] { "A", "B", "C" }));
    }
}
