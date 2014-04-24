package org.shizhz.route;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.shizhz.exception.UnconnectedRouteException;

public class RouteTest {

    @Test(expected = UnconnectedRouteException.class)
    public void testAddUnconnectedRoutes() throws UnconnectedRouteException {
        Route route1 = new Route(new City("A"), new City("B"), 123);
        Route route2 = new Route(new City("C"), new City("D"), 32);

        route1.addInnerRoute(route2);
    }

    @Test(expected = UnconnectedRouteException.class)
    public void testAddSelf() throws UnconnectedRouteException {
        Route route = new Route("A", "B", 12);
        route.addInnerRoute(route);
    }

    @Test(expected = UnconnectedRouteException.class)
    public void testAddTwoRoutesWithSameSourceCity()
            throws UnconnectedRouteException {
        Route route1 = new Route(new City("A"), new City("B"), 123);
        Route route2 = new Route(new City("A"), new City("D"), 32);

        route1.addInnerRoute(route2);
    }

    @Test(expected = UnconnectedRouteException.class)
    public void testAddTwoRoutesWithSameDestinationCity()
            throws UnconnectedRouteException {
        Route route1 = new Route("A", "B", 123);
        Route route2 = new Route("C", "B", 32);

        route1.addInnerRoute(route2);
    }

    @Test
    public void testAddRoutes() throws UnconnectedRouteException {
        Route route1 = new Route("A", "B", 123);
        assertEquals(route1, route1.addInnerRoute(null));

        Route route2 = new Route("B", "C", 32);

        Route route3 = route1.addInnerRoute(route2);

        assertEquals("A", route3.getSourceCity().getName());
        assertEquals(new City("C"), route3.getDestinationCity());
        assertEquals(route1.getDistance() + route2.getDistance(),
                route3.getDistance());
    }

    @Test
    public void testInnerRoutes() throws UnconnectedRouteException {
        Route route1 = new Route("A", "B", 12);

        assertNotNull(route1.getInnerRoutes());
        assertEquals(1, route1.getInnerRoutes().size());
        assertEquals(route1, route1.getInnerRoutes().get(0));

        Route route2 = new Route("B", "C", 23);
        Route route3 = route1.addInnerRoute(route2);

        assertNotNull(route3.getInnerRoutes());
        assertEquals(2, route3.getInnerRoutes().size());
        assertEquals(route1, route3.getInnerRoutes().get(0));
        assertEquals(route2, route3.getInnerRoutes().get(1));
    }

    @Test
    public void testRouteRepr() throws UnconnectedRouteException {
        Route route1 = new Route("A", "B", 12);

        assertNotNull(route1.fullRouteRepr());
        assertEquals("A-B", route1.fullRouteRepr());
        assertEquals("A-B", route1.shortRouteRepr());

        Route route2 = new Route("B", "C", 23);
        Route route3 = route1.addInnerRoute(route2);

        assertEquals("A-B-C", route3.fullRouteRepr());
        assertEquals("A-C", route3.shortRouteRepr());

        Route route4 = new Route("", "", 1);
        assertEquals("-", route4.fullRouteRepr());
        assertEquals("-", route4.shortRouteRepr());

        Route r4 = new Route("C", "A", 1);
        Route route5 = route3.addInnerRoute(r4);
        assertEquals("A-B-C-A", route5.fullRouteRepr());
        assertEquals("A-A", route5.shortRouteRepr());
    }

    @Test
    public void testStartsWith() {
        assertTrue(new Route("A", "B", 2).startsWith(new Route("D", "A", 6)));
        assertFalse(new Route("A", "B", 2).startsWith(new Route("D", "C", 6)));
        assertTrue(new Route("", "B", 2).startsWith(new Route("D", "", 6)));
        assertFalse(new Route("A", "B", 2).startsWith(null));
    }

    @Test
    public void testCompareTo() {
        Route r1 = new Route("A", "B", 5);
        Route r2 = new Route("A", "B", 2);
        Route r3 = new Route("A", "C", 4);
        Route r4 = new Route("B", "D", 10);
        Route r5 = new Route("C", "B", 7);
        Route r6 = new Route("D", "C", 4);

        assertTrue(r1.compareTo(r2) > 0);
        assertTrue(r1.compareTo(r3) < 0);
        assertTrue(r1.compareTo(new Route("A", "B", 5)) == 0);
        assertTrue(r4.compareTo(r5) < 0);

        Route[] routes = new Route[] { r1, r2, r3, r4, r5, r6 };
        Arrays.sort(routes);
        assertArrayEquals(new Route[] { r2, r1, r3, r4, r5, r6 }, routes);
    }

    @Test
    public void testRouteType() throws UnconnectedRouteException {
        Route r1 = new Route("A", "B", 12);
        Route r2 = new Route("B", "C", 1);

        assertEquals(Route.Type.SIGNLE, r1.getType());
        assertEquals(Route.Type.SIGNLE, r2.getType());
        assertEquals(Route.Type.COMPOSITE, r1.addInnerRoute(r2).getType());
    }

    @Test
    public void testEquals() throws UnconnectedRouteException {
        assertFalse(new Route("A", "B", 1).equals(new Route("B", "B", 1)));
        assertFalse(new Route("A", "B", 2).equals(null));

        Route r1 = new Route("A", "B", 12);

        assertTrue(r1.equals(r1));

        Route r2 = new Route("A", "B", 12);
        assertTrue(r1.equals(r2));
        assertTrue(r2.equals(r1));
        assertTrue(r1.hashCode() == r2.hashCode());

        Route r3 = new Route("A", "B", 12);
        assertTrue(r1.equals(r2));
        assertTrue(r2.equals(r3));
        assertTrue(r1.equals(r3));

        Route r4 = r1.addInnerRoute(new Route("B", "D", 5)).addInnerRoute(
                new Route("D", "E", 4));
        Route r5 = r2.addInnerRoute(new Route("B", "D", 5)).addInnerRoute(
                new Route("D", "E", 4));
        assertTrue(r4.equals(r4));
        assertTrue(r4.equals(r5));
        assertTrue(r5.equals(r4));
        assertTrue(r4.hashCode() == r5.hashCode());

        Route r6 = r2.addInnerRoute(new Route("B", "D", 5)).addInnerRoute(
                new Route("D", "E", 4));
        assertTrue(r4.equals(r5));
        assertTrue(r5.equals(r6));
        assertTrue(r4.equals(r6));

        Route r7 = r6.addInnerRoute(new Route("E", "F", 12));
        Route r8 = r6.addInnerRoute(new Route("E", "G", 12));
        assertFalse(r7.equals(r8));

        Route r9 = r6.addInnerRoute(new Route("E", "F", 13));
        assertFalse(r7.equals(r9));

        assertFalse(r1.equals(r9));
        assertFalse(r1.equals("string"));
        assertFalse(r6.equals(r7));
    }

    @Test
    public void testAddCompositeRoute() {
        Route r1 = new Route("A", "B", 3).addInnerRoute(new Route("B", "C", 4));
        assertTrue(r1.getType() == Route.Type.COMPOSITE);
        Route r2 = new Route("C", "D", 5).addInnerRoute(new Route("D", "E", 6));
        assertTrue(r2.getType() == Route.Type.COMPOSITE);

        Route r3 = r1.addInnerRoute(r2);
        assertEquals("A-B-C-D-E", r3.fullRouteRepr());
        assertEquals(18, r3.getDistance());
    }
}
