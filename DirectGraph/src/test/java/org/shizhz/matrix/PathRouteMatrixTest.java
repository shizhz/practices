package org.shizhz.matrix;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.shizhz.exception.IllegalInputException;
import org.shizhz.route.City;
import org.shizhz.route.Route;
import org.shizhz.route.RouteParser;
import org.shizhz.util.Logger;

public class PathRouteMatrixTest {

    private static final boolean VERBOSE = false;

    private static final Logger logger = Logger.newInstance(System.out);

    @Test
    public void testMatrixInitiation() throws IllegalInputException {
        RouteParser parser = RouteParser.newInstance();
        String input = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7, EF9";
        List<Route> rs = parser.parse(input);

        RouteMatrix<List<Route>> m = PathRouteMatrix.newInstance();
        m.addRoutes(rs);
        m.build();

        for (String route : input.split(",")) {
            Route r = parser.parse(route).get(0);
            assertEquals(
                    r,
                    m.getMatrixElement(r.getSourceCity(),
                            r.getDestinationCity()).get(0));
        }

        List<City> cities = m.getMatrixRouteCities();
        int matrixSize = 0;
        for (City row : cities) {
            for (City column : cities) {
                List<Route> routeList = m.getMatrixElement(row, column);
                if (routeList != null) {
                    matrixSize += routeList.size();
                }
            }
        }

        assertEquals(rs.size(), matrixSize);
    }

    private City getSourceCity(String routes) {
        return new City(routes.split("-")[0]);
    }

    private City getDestCity(String routes) {
        String[] array = routes.split("-");
        return new City(array[array.length - 1]);
    }

    private int getStops(String routes) {
        return routes.split("-").length - 1;
    }

    private void printRoutes(List<Route> rs, String message) {
        if (VERBOSE) {
            logger.logln("----" + message + "----");
            if (rs == null) {
                logger.logln("\tNo route found");
                return;
            }
            for (Route route : rs) {
                logger.logln(route.fullRouteRepr() + " : "
                        + route.getDistance());
            }
        }
    }

    private List<Route> findRoutes(RouteMatrix<List<Route>> m, City from,
            City to, int stops) {
        List<Route> result = m.power(stops).getMatrixElement(from, to);
        return result == null ? new ArrayList<Route>() : result;
    }

    private Route findRoute(RouteMatrix<List<Route>> m, String route) {
        List<Route> results = findRoutes(m, getSourceCity(route),
                getDestCity(route), getStops(route));

        if (results == null || results.isEmpty()) {
            return null;
        }

        for (Route r : results) {
            if (r.fullRouteRepr().equals(route)) {
                return r;
            }
        }

        return null;
    }

    @Test
    public void testFindRouteDistance() throws IllegalInputException {
        String input = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7";
        RouteMatrix<List<Route>> matrix = PathRouteMatrix.newInstance();
        matrix.addRoutes(RouteParser.newInstance().parse(input));
        matrix.build();

        assertEquals(9, findRoute(matrix, "A-B-C").getDistance());
        assertEquals(5, findRoute(matrix, "A-D").getDistance());
        assertEquals(13, findRoute(matrix, "A-D-C").getDistance());
        assertEquals(22, findRoute(matrix, "A-E-B-C-D").getDistance());
        assertNull(findRoute(matrix, "A-E-D"));
    }

    @Test
    public void testFindRoutes() throws IllegalInputException {
        String input = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7";
        RouteMatrix<List<Route>> matrix = PathRouteMatrix.newInstance();
        matrix.addRoutes(RouteParser.newInstance().parse(input));
        matrix.build();

        List<Route> cToc = new ArrayList<Route>();
        cToc.addAll(findRoutes(matrix, new City("C"), new City("C"), 1));
        cToc.addAll(findRoutes(matrix, new City("C"), new City("C"), 2));
        cToc.addAll(findRoutes(matrix, new City("C"), new City("C"), 3));

        printRoutes(cToc, "From C to C with maximum 3 stops: ");
        assertEquals(2, cToc.size());
        Route firstRoute = cToc.get(0);
        Route secRoute = cToc.get(1);

        assertNotEquals(firstRoute, secRoute);
        assertTrue("C-D-C".equals(firstRoute.fullRouteRepr())
                || "C-D-C".equals(secRoute.fullRouteRepr()));
        assertTrue("C-E-B-C".equals(firstRoute.fullRouteRepr())
                || "C-E-B-C".equals(secRoute.fullRouteRepr()));

        List<Route> aToc = new ArrayList<Route>();
        aToc.addAll(findRoutes(matrix, new City("A"), new City("C"), 4));
        printRoutes(aToc, "From A to C with 4 stops: ");
        assertEquals(3, aToc.size());
        Set<String> routeRepr = new HashSet<>();
        for (Route r : aToc) {
            routeRepr.add(r.fullRouteRepr());
        }

        assertTrue(routeRepr.contains("A-B-C-D-C"));
        assertTrue(routeRepr.contains("A-D-C-D-C"));
        assertTrue(routeRepr.contains("A-D-E-B-C"));
    }

    @Test
    public void testTransitiveClosure() throws IllegalInputException {
        String input = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7";
        RouteMatrix<List<Route>> matrix = PathRouteMatrix.newInstance();
        matrix.addRoutes(RouteParser.newInstance().parse(input));
        matrix.build();

        RouteMatrix<List<Route>> closure = matrix.getMatrixTransitiveClosure();

        List<City> cities = matrix.getMatrixRouteCities();
        for (City row : cities) {
            for (City column : cities) {
                List<Route> routes = closure.getMatrixElement(row, column);
                if (routes != null) {
                    Collections.sort(routes, new Comparator<Route>() {

                        @Override
                        public int compare(Route o1, Route o2) {
                            return o1.getDistance() - o2.getDistance();
                        }
                    });
                }
                printRoutes(routes, "Route from " + row.getName() + " to "
                        + column.getName());
            }
        }
    }

    @Test
    public void testAddMatrix() throws IllegalInputException {
        String input = "AB5, BC4";
        RouteMatrix<List<Route>> matrix = PathRouteMatrix.newInstance();
        matrix.addRoutes(RouteParser.newInstance().parse(input));
        matrix.build();

        input = "BC3, AC3";
        RouteMatrix<List<Route>> matrix2 = PathRouteMatrix.newInstance();
        matrix2.addRoutes(RouteParser.newInstance().parse(input));
        matrix2.build();

        RouteMatrix<List<Route>> sum = matrix.add(matrix2);

        City a = new City("A");
        City b = new City("B");
        City c = new City("C");

        assertArrayEquals(new Route[] { new Route("A", "B", 5) }, sum
                .getMatrixElement(a, b).toArray(new Route[1]));
        assertArrayEquals(new Route[] { new Route("A", "C", 3) }, sum
                .getMatrixElement(a, c).toArray(new Route[1]));
        assertArrayEquals(new Route[] { new Route("B", "C", 4),
                new Route("B", "C", 3) },
                sum.getMatrixElement(b, c).toArray(new Route[2]));
    }

    private void testMatrixEqual(RouteMatrix<List<Route>> m1,
            RouteMatrix<List<Route>> m2) {
        assertEquals(m1.getMatrixDimensions(), m2.getMatrixDimensions());
        List<City> cities = m1.getMatrixRouteCities();

        for (City row : cities) {
            for (City column : cities) {
                List<Route> m1Routes = m1.getMatrixElement(row, column);
                List<Route> m2Routes = m2.getMatrixElement(row, column);
                if (m1Routes == null) {
                    assertNull(m2Routes);
                } else {
                    Collections.sort(m1Routes);
                    Collections.sort(m2Routes);
                    assertArrayEquals(
                            m1Routes.toArray(new Route[m1Routes.size()]),
                            m2Routes.toArray(new Route[m1Routes.size()]));
                }
            }
        }
    }

    @Test
    public void testAddtionIdentify() throws IllegalInputException {
        String input = "AD1, BA2, BC3, CA5, CD6, DC7";

        RouteMatrix<List<Route>> m = PathRouteMatrix.newInstance();
        m.addRoutes(RouteParser.newInstance().parse(input));
        m.build();

        RouteMatrix<List<Route>> identify = m.getAdditionIdentify();

        testMatrixEqual(m, m.add(identify));
        testMatrixEqual(m, identify.add(m));
    }

    @Test
    public void testMultiplyIdentify() throws IllegalInputException {
        String input = "AD1, BA2, BC3, CA5, CD6, DC7";

        RouteMatrix<List<Route>> m = PathRouteMatrix.newInstance();
        m.addRoutes(RouteParser.newInstance().parse(input));
        m.build();

        RouteMatrix<List<Route>> identify = m.getMultiplyIdentify();

        testMatrixEqual(m, m.multiply(identify));
        testMatrixEqual(m, identify.multiply(m));
    }
    
    @Test
    public void testAddDuplicateMatrixCities() {
        RouteMatrix<List<Route>> m = PathRouteMatrix.newInstance();
        m.addMatrixRouteCities(Arrays.asList(new City[] {
                new City("A"),new City("B"), new City("A")
        }));
        assertEquals(2, m.getMatrixRouteCities().size());
        assertTrue(m.getMatrixRouteCities().contains(new City("A")));
        assertTrue(m.getMatrixRouteCities().contains(new City("B")));
    }
}
