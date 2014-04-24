package org.shizhz.directive;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.shizhz.directive.DirectiveInfo;
import org.shizhz.directive.DirectiveParser;
import org.shizhz.exception.UnRecognizedDirectiveException;

public class DirectiveParserTest {

    private DirectiveParser parser;

    @Before
    public void setup() {
        parser = DirectiveParser.newInstance();
    }

    @Test
    public void testParseNullAndEmpty() throws UnRecognizedDirectiveException {
        assertNull(parser.parse(null));
        assertNull(parser.parse(""));
        assertNull(parser.parse("    "));
    }

    @Test(expected = UnRecognizedDirectiveException.class)
    public void testParserException() throws UnRecognizedDirectiveException {
        parser.parse("z AB2, BD2");
        fail();
    }

    @Test
    public void testParserDirective() throws UnRecognizedDirectiveException {
        DirectiveInfo directive = parser.parse("g AB2, 3, 4");
        assertNotNull(directive);
        assertEquals("g AB2, 3, 4", directive.getDirective());
        assertEquals(DirectiveInfo.DirectiveType.G,
                directive.getDirectiveType());
        assertArrayEquals(new String[] { "AB2", "3", "4" },
                directive.getDirectiveParams());
        assertArrayEquals(new String[] {}, parser.parse("g")
                .getDirectiveParams());
        assertArrayEquals(new String[] { "A", "B", "C" },
                parser.parse("g a , b, c ,").getDirectiveParams());
        assertArrayEquals(new String[] { "A", "B", "C" },
                parser.parse("g a , b, , , ,  c ,").getDirectiveParams());

        assertEquals(DirectiveInfo.DirectiveType.D, parser.parse("d A-B-D")
                .getDirectiveType());
        assertEquals(DirectiveInfo.DirectiveType.SD, parser.parse("sd A-B-D")
                .getDirectiveType());
        assertEquals(DirectiveInfo.DirectiveType.PRINT, parser.parse("print")
                .getDirectiveType());
        assertEquals(DirectiveInfo.DirectiveType.TLTD, parser.parse("tltd")
                .getDirectiveType());
        assertEquals(DirectiveInfo.DirectiveType.TMS, parser.parse("tMs")
                .getDirectiveType());
        assertEquals(DirectiveInfo.DirectiveType.TS, parser.parse("Ts")
                .getDirectiveType());
        assertEquals("Ts", parser.parse("Ts").getDirectiveName());
        assertEquals("Shortest Distance", parser.parse("sd").getDirectiveType()
                .getDesc());
    }
}
