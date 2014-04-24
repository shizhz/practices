package org.shizhz.route;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.shizhz.exception.IllegalInputException;

public class RouteParserTest {

    @Test
    public void testRoutePattern() {
        final String routePattern = "[A-Z]{2}[1-9][0-9]*";

        assertTrue("AB12".matches(routePattern));

        assertFalse("AB-12".matches(routePattern));
        assertFalse("AB03".matches(routePattern));
        assertFalse("CdBj1200".matches(routePattern));
        assertFalse("ABC1200".matches(routePattern));
        assertFalse("AB 12".matches(routePattern));
        assertFalse("".matches(routePattern));
        assertFalse("ab".matches(routePattern));
        assertFalse("ab1".matches(routePattern));
        assertFalse("B12".matches(routePattern));
        assertFalse("AB0090".matches(routePattern));
    }

    @Test(expected = IllegalInputException.class)
    public void testParseRoutesWithNullInput() throws IllegalInputException {

        RouteParser routeParser = new RouteParser();
        routeParser.parse(null);
    }

    @Test(expected = IllegalInputException.class)
    public void testParseRoutesWithWrongFormattedRoute()
            throws IllegalInputException {
        RouteParser routeParser = new RouteParser();
        routeParser.parse("AB123, AD23, CD");
    }

    @Test(expected = IllegalInputException.class)
    public void testParseRoutesWithDuplicateRoute()
            throws IllegalInputException {
        RouteParser routeParser = new RouteParser();
        routeParser.parse("AB123, AD23, AB3");
    }

    @Test(expected = IllegalInputException.class)
    public void testParseRoutesWithWrongRoute() throws IllegalInputException {
        RouteParser routeParser = new RouteParser();
        routeParser.parse("AB123, CC34");
    }

    @Test
    public void testParseRoutes() throws IllegalInputException {
        RouteParser routeParser = new RouteParser();
        List<Route> routes = routeParser.parse("AB120, AC2,, CD234"); // duplicated
                                                                      // separator
                                                                      // will be
                                                                      // just
                                                                      // ignored

        assertNotNull(routes);
        assertEquals(3, routes.size());
        assertEquals("AB120", routes.get(0).toString());
        assertEquals("AC2", routes.get(1).toString());
        assertEquals("CD234", routes.get(2).toString());
    }

    @Test
    public void testParseCities() throws IllegalInputException {
        RouteParser routeParser = new RouteParser();

        Set<City> cities = routeParser
                .parseCities("AB12, AC345, CD48, DB32, BC97");

        assertNotNull(cities);
        assertEquals(4, cities.size());
        assertTrue(cities.contains(new City("A")));
        assertTrue(cities.contains(new City("B")));
        assertTrue(cities.contains(new City("C")));
        assertTrue(cities.contains(new City("D")));
        assertFalse(cities.contains(new City("E")));
    }

    @Test
    public void testParseCitiesFromRouteList() throws IllegalInputException {
        RouteParser routeParser = new RouteParser();
        List<Route> routes = routeParser.parse("AB12, AC345, CD48, DB32, BC97");

        Set<City> cities = routeParser.parseCities(routes);

        assertNotNull(cities);
        assertEquals(4, cities.size());
        assertTrue(cities.contains(new City("A")));
        assertTrue(cities.contains(new City("B")));
        assertTrue(cities.contains(new City("C")));
        assertTrue(cities.contains(new City("D")));
        assertFalse(cities.contains(new City("E")));
    }
}
