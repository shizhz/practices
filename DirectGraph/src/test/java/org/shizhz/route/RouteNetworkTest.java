package org.shizhz.route;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.shizhz.exception.IllegalInputException;
import org.shizhz.exception.NoRouteExistingException;

public class RouteNetworkTest {
    private RouteNetwork routeNetwork;

    @Before
    public void setup() throws IllegalInputException {
        String routesInput = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7";
        routeNetwork = new RouteNetwork(routesInput);
    }

    @Test(expected = IllegalInputException.class)
    public void testAddRoutes() throws IllegalInputException {
        RouteNetwork routeNetwork = new RouteNetwork();
        routeNetwork.addRoutes("a,d");
    }

    @Test
    public void testRoutePattern() {
        String pattern = "([A-Z]-)+[A-Z]$";
        assertTrue("A-B-C".matches(pattern));
        assertTrue("A-B".matches(pattern));
        assertTrue("A-B-C-D-E-F".matches(pattern));
        assertFalse("A".matches(pattern));
        assertFalse("A-".matches(pattern));
        assertFalse("-A-B".matches(pattern));
        assertFalse("".matches(pattern));
        assertFalse("A-B -C".matches(pattern));
    }

    @Test(expected = IllegalInputException.class)
    public void testRouteNetworkException() throws IllegalInputException {
        new RouteNetwork("AB3,AD4,9");
    }

    @Test(expected = IllegalInputException.class)
    public void testFindRouteDistanceInputInvalid()
            throws IllegalInputException, NoRouteExistingException {
        try {
            routeNetwork.findRouteDistance("d");
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.findRouteDistance(null);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }
        try {
            routeNetwork.findRouteDistance("");
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        routeNetwork.findRouteDistance("A-D-");
    }

    @Test
    public void testFindRoute() throws IllegalInputException,
            NoRouteExistingException {
        assertEquals(9, routeNetwork.findRouteDistance("A-B-C"));
        assertEquals(5, routeNetwork.findRouteDistance("A-D"));
        assertEquals(13, routeNetwork.findRouteDistance("A-D-C"));
        assertEquals(22, routeNetwork.findRouteDistance("A-E-B-C-D"));
    }

    @Test(expected = NoRouteExistingException.class)
    public void testFindRouteDistanceNoSuchRoute()
            throws IllegalInputException, NoRouteExistingException {
        routeNetwork.findRouteDistance("A-E-D");
    }

    @Test(expected = IllegalInputException.class)
    public void testTripsAmountWithStopsInputException()
            throws IllegalInputException, NoRouteExistingException {
        try {
            routeNetwork.tripsAmountWithStops("d", 1);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsAmountWithStops(null, 1);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }
        try {
            routeNetwork.tripsAmountWithStops("", 2);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsAmountWithStops("A", 2);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsAmountWithStops("A-E-", 2);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsAmountWithStops("A-E-D", 2);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsAmountWithStops("A-D", 0);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        routeNetwork.tripsAmountWithStops("A-D", -1);
    }

    @Test
    public void testTripsAmountWithStops() throws IllegalInputException,
            NoRouteExistingException {
        assertEquals(3, routeNetwork.tripsAmountWithStops("A-C", 4));
    }

    @Test(expected = NoRouteExistingException.class)
    public void testTripsAmountWithStopsNoSuchRoute()
            throws IllegalInputException, NoRouteExistingException {
        routeNetwork.tripsAmountWithStops("D-A", 2);
    }

    @Test(expected = IllegalInputException.class)
    public void testTripsAmountWithMaximumStopsInputException()
            throws IllegalInputException, NoRouteExistingException {
        try {
            routeNetwork.tripsAmountWithMaximumStops("d", 1);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsAmountWithMaximumStops(null, 1);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }
        try {
            routeNetwork.tripsAmountWithMaximumStops("", 2);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsAmountWithMaximumStops("A", 2);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsAmountWithMaximumStops("A-E-", 2);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsAmountWithMaximumStops("A-E-D", 2);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsAmountWithMaximumStops("A-D", 0);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        routeNetwork.tripsAmountWithMaximumStops("A-D", -1);
    }

    @Test
    public void testTripsAmountWithMaximumStops() throws IllegalInputException,
            NoRouteExistingException {
        assertEquals(2, routeNetwork.tripsAmountWithMaximumStops("C-C", 3));
    }

    @Test(expected = NoRouteExistingException.class)
    public void testTripsAmountWithMaximumStopsNoSuchRoute()
            throws IllegalInputException, NoRouteExistingException {
        routeNetwork.tripsAmountWithMaximumStops("D-A", 3);
    }

    @Test(expected = IllegalInputException.class)
    public void testShortestPathInputException() throws IllegalInputException,
            NoRouteExistingException {
        try {
            routeNetwork.shortestPath("d");
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.shortestPath(null);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }
        try {
            routeNetwork.shortestPath("");
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.shortestPath("A");
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.shortestPath("A-E-");
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.shortestPath("A-E-D");
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.shortestPath("A-");
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        routeNetwork.shortestPath("-A-");
    }

    @Test
    public void testShortestPath() throws IllegalInputException,
            NoRouteExistingException {
        assertEquals(9, routeNetwork.shortestPath("A-C").getDistance());
        assertEquals(9, routeNetwork.shortestPath("B-B").getDistance());
    }

    @Test(expected = NoRouteExistingException.class)
    public void testShortestPathNoSuchRoute() throws IllegalInputException,
            NoRouteExistingException {
        routeNetwork.shortestPath("D-A");
    }

    @Test(expected = IllegalInputException.class)
    public void testTripsLessThanDistanceInputException()
            throws IllegalInputException, NoRouteExistingException {

        try {
            routeNetwork.tripsLessThanDistance("d", 1);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsLessThanDistance(null, 1);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }
        try {
            routeNetwork.tripsLessThanDistance("", 2);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsLessThanDistance("A", 2);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsLessThanDistance("A-E-", 2);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsLessThanDistance("A-E-D", 2);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        try {
            routeNetwork.tripsLessThanDistance("A-D", 0);
            fail();
        } catch (Exception e) {
            assertEquals(IllegalInputException.class, e.getClass());
        }

        routeNetwork.tripsLessThanDistance("A-D", -1);
    }

    @Test
    public void testTripsLessThanDistance() throws IllegalInputException,
            NoRouteExistingException {
        assertEquals(7, routeNetwork.tripsLessThanDistance("C-C", 30));
        assertEquals(3, routeNetwork.tripsLessThanDistance("C-C", 20));
    }

    @Test(expected = NoRouteExistingException.class)
    public void testTripsLessThanDistanceNoSuchRoute()
            throws IllegalInputException, NoRouteExistingException {
        routeNetwork.tripsAmountWithMaximumStops("D-A", 9);
    }

    @Test
    public void testRepr() {
        String expectedMatrix = "\n       A   B   C   D   E\n"
                + "   A   0   1   0   1   1\n" + "   B   0   0   1   0   0\n"
                + "   C   0   0   0   1   1\n" + "   D   0   0   1   0   1\n"
                + "   E   0   1   0   0   0\n";

        assertEquals(expectedMatrix, routeNetwork.repr());
    }
}
