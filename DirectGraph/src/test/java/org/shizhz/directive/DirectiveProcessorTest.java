package org.shizhz.directive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.shizhz.exception.DirectiveException;
import org.shizhz.exception.UnRecognizedDirectiveException;
import org.shizhz.util.Logger;

public class DirectiveProcessorTest {
    private Logger logger = Logger.newInstance(System.out);

    private DirectiveProcessor processor;

    private DirectiveParser parser;
    private String routesInput = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7";

    @Before
    public void setup() {
        processor = DirectiveProcessor.newInstance();
        parser = DirectiveParser.newInstance();
    }

    @Test
    public void testProcessNull() throws DirectiveException {
        assertEquals("", processor.process(""));
    }

    @Test
    public void testProcessUncognizedDirectiveException() {
        try {
            processor.process("asdf d");
        } catch (Exception e) {
            assertEquals(UnRecognizedDirectiveException.class, e.getCause()
                    .getClass());
        }
    }

    @Test
    public void testProcessAddRoute() throws DirectiveException {
        assertEquals(DirectiveInfo.DirectiveType.G.getDesc() + " Done",
                processor.process(parser.parse("g AB2, AC4")));
    }

    @Test(expected = DirectiveException.class)
    public void testProcessAddRouteException() throws DirectiveException {
        try {
            processor.process(parser.parse("g"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("g AB2 AB4"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        processor.process(parser.parse("g A-E-D"));
    }

    @Test(expected = DirectiveException.class)
    public void testProcessFindDistanceException() throws DirectiveException {
        try {
            processor.process(parser.parse("d"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        processor.process(parser.parse("d AB"));

    }

    @Test
    public void testProcessFindDistance() throws DirectiveException {
        processor.process(parser.parse("g " + routesInput));
        assertEquals(DirectiveInfo.DirectiveType.D.getDesc() + " A-B-C : 9\n",
                processor.process(parser.parse("d A-B-C")));
        assertEquals(DirectiveInfo.DirectiveType.D.getDesc()
                + " A-D : 5, A-D-C : 13\n",
                processor.process(parser.parse("d a-d, A-D-C")));
        assertEquals(DirectiveInfo.DirectiveType.D.getDesc()
                + " A-D : 5, E-A : NO SUCH ROUTE\n",
                processor.process(parser.parse("d a-d, E-A")));
    }

    @Test(expected = DirectiveException.class)
    public void testProcessTripsWithStopsException() throws DirectiveException {
        try {
            processor.process(parser.parse("ts"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("ts AB"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("ts A-B-c"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("ts A-B-c, a"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("ts A-B-c, -1"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        processor.process(parser.parse("g " + routesInput));
        processor.process(parser.parse("ts A-E-D, 2"));
    }

    @Test
    public void testProcessTripsWithStops() throws DirectiveException {
        processor.process(parser.parse("g " + routesInput));
        assertEquals(DirectiveInfo.DirectiveType.TS.getDesc() + " A-C : 3",
                processor.process("ts a-c, 4"));
    }

    @Test(expected = DirectiveException.class)
    public void testProcessTripsWithMaximumStopsException()
            throws DirectiveException {
        try {
            processor.process(parser.parse("tms"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("tms AB"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("tms A-B-c"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("tms A-B-c, a"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("tms A-B-c, -1"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        processor.process(parser.parse("g " + routesInput));
        processor.process(parser.parse("tms A-E-D, 2"));
    }

    @Test
    public void testProcessTripsWithMaximumStops() throws DirectiveException {
        processor.process(parser.parse("g " + routesInput));
        assertEquals(DirectiveInfo.DirectiveType.TMS.getDesc() + " C-C : 2",
                processor.process("tms c-c, 3"));
    }

    @Test(expected = DirectiveException.class)
    public void testProcessTripsLessThanDistanceException()
            throws DirectiveException {
        try {
            processor.process(parser.parse("tltd"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("tltd AB"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("tltd A-B-c"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("tltd A-B-c, a"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        try {
            processor.process(parser.parse("tltd A-B, -1"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
        }

        processor.process(parser.parse("g " + routesInput));
        processor.process(parser.parse("tltd A-D, 2"));
    }

    @Test
    public void testProcessTripsLessThanDistance() throws DirectiveException {
        processor.process(parser.parse("g " + routesInput));
        assertEquals(DirectiveInfo.DirectiveType.TLTD.getDesc() + " C-C : 7",
                processor.process("tltd c-c, 30"));
    }

    @Test(expected = DirectiveException.class)
    public void testProcessFindShortestPathException()
            throws DirectiveException {
        DirectiveInfo directive = parser.parse("sd");
        try {
            processor.process(parser.parse("sd"));
        } catch (Exception e) {
            assertEquals(DirectiveException.class, e.getClass());
            assertEquals("Directive " + directive.getDirectiveName()
                    + " needs parameters.\n", e.getMessage());
        }

        processor.process(parser.parse("sd AB"));
    }

    @Test
    public void testProcessFindShortestPath() throws DirectiveException {
        processor.process(parser.parse("g " + routesInput));
        assertEquals(DirectiveInfo.DirectiveType.SD.getDesc()
                + " A-C : 9, B-B : 9\n",
                processor.process(parser.parse("sd A-C,B-B")));

        assertEquals(DirectiveInfo.DirectiveType.SD.getDesc()
                + " A-A : NO SUCH ROUTE\n",
                processor.process(parser.parse("sd a-a")));
        assertEquals(DirectiveInfo.DirectiveType.SD.getDesc()
                + " A-C : 9, B-B : 9, A-A : NO SUCH ROUTE\n",
                processor.process(parser.parse("sd A-C,B-B, a-a")));
    }

    @Test
    public void testPrintNetwork() throws DirectiveException {
        String expectedMatrix = "Print Route Network \n       A   B   C   D   E\n"
                + "   A   0   1   0   1   1\n"
                + "   B   0   0   1   0   0\n"
                + "   C   0   0   0   1   1\n"
                + "   D   0   0   1   0   1\n"
                + "   E   0   1   0   0   0\n";
        assertEquals("Print Route Network Route network is empty",
                processor.process("print"));
        processor.process("g " + routesInput);
        assertEquals(expectedMatrix, processor.process("print"));
    }

    private List<String> getHelpAsList(String helpInfo) {
        List<String> helpResult = new ArrayList<>();
        for (String help : helpInfo.split("\n")) {
            helpResult.add(help.trim());
        }

        return helpResult;
    }

    @Test
    public void testProcessHelp() throws DirectiveException {
        List<String> helps = getHelpAsList(processor.process("help g"));
        assertTrue(helps.size() == 2);
        assertTrue(helps.contains("Usage Information"));
        assertTrue(helps.contains("G : Add Route to Graph"));

        helps = getHelpAsList(processor.process("help k, d"));
        assertTrue(helps.size() == 3);
        assertTrue(helps.contains("Usage Information"));
        assertTrue(helps.contains("D : Distance"));
        assertTrue(helps.contains("K : Unsupported directive."));

        helps = getHelpAsList(processor.process("help"));
        assertEquals(DirectiveInfo.DirectiveType.values().length + 1,
                helps.size());

        logger.logln(processor.process("help"));
    }
    
    @Test(expected=DirectiveException.class)
    public void testProcessDefault() throws DirectiveException {
        DirectiveInfo directive = new DirectiveInfo();
        processor.process(directive);
    }
}
