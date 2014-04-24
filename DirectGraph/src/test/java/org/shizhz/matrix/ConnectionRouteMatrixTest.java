package org.shizhz.matrix;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.shizhz.exception.BrokenRouteMatrixExcpetion;
import org.shizhz.exception.IllegalInputException;
import org.shizhz.exception.UnconnectedRouteException;
import org.shizhz.route.City;
import org.shizhz.route.Route;
import org.shizhz.route.RouteParser;
import org.shizhz.util.Logger;

public class ConnectionRouteMatrixTest {
    private static boolean VERBOSE = false;

    private Logger logger;

    private String routeInput;
    private Collection<Route> routes;

    private RouteMatrix<Integer> matrix;

    @Before
    public void setup() throws IllegalInputException {
        routeInput = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7, EF9";
        routes = RouteParser.newInstance().parse(routeInput);

        matrix = ConnectionRouteMatrix.newInstance();
        matrix.addRoutes(routes);
        matrix.build();
        logger = Logger.newInstance(System.out);
    }

    private List<Route> getRoutesByCities(String sourceCityName,
            String destCityName) {
        List<Route> result = new ArrayList<>();

        for (Route route : this.routes) {
            if (sourceCityName != null && destCityName != null) {
                if (route.getSourceCity().getName().equals(sourceCityName)
                        && route.getDestinationCity().getName()
                                .equals(destCityName)) {
                    result.add(route);
                }
            } else if (sourceCityName != null
                    && route.getSourceCity().getName().equals(sourceCityName)) {
                result.add(route);
            } else if (destCityName != null
                    && route.getDestinationCity().getName()
                            .equals(destCityName)) {
                result.add(route);
            }

        }

        return result;
    }

    @Test
    public void testMatrixDimensions() {
        assertEquals(RouteParser.newInstance().parseCities(routes).size(),
                matrix.getMatrixDimensions());
    }

    @Test
    public void testSetMatrixElement() {
        RouteMatrix<Integer> matrix = ConnectionRouteMatrix.newInstance();
        Integer i1 = new Integer(2);
        City row = new City("A");
        City column = new City("B");

        assertNull(matrix.getMatrixElement(row, column));
        assertEquals(matrix.getMatrixRow(row).get(column), matrix
                .getMatrixColumn(column).get(row));

        matrix.setMatrixElement(row, column, i1);

        assertEquals(i1, matrix.getMatrixElement(row, column));
        assertEquals(matrix.getMatrixRow(row).get(column), matrix
                .getMatrixColumn(column).get(row));
    }

    @Test
    public void testEmptyMatrix() {
        RouteMatrix<Integer> m = ConnectionRouteMatrix.newInstance();
        m.build();
        assertEquals(0, m.getMatrixDimensions());
        assertTrue(m.getMatrixRouteCities().isEmpty());
    }

    @Test
    public void testMatrixCities() throws BrokenRouteMatrixExcpetion {
        List<City> matrixCities = matrix.getMatrixRouteCities();

        List<City> parsedCities = new ArrayList<City>();
        parsedCities.addAll(RouteParser.newInstance().parseCities(routes));
        Collections.sort(parsedCities);

        assertEquals(parsedCities.size(), matrixCities.size());
        assertArrayEquals(matrixCities.toArray(), parsedCities.toArray());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddCompositeRoute() throws UnconnectedRouteException {
        List<Route> newRoutes = new ArrayList<>();
        newRoutes.addAll(routes);
        newRoutes.add(new Route("A", "F", 4).addInnerRoute(new Route("F", "G",
                120)));

        RouteMatrix<Integer> routeMatrix = ConnectionRouteMatrix.newInstance();
        routeMatrix.addRoutes(newRoutes);
    }

    @Test
    public void testAddDuplicateRoute() {
        List<Route> newRoutes = new ArrayList<>();
        Route duplicateRoute = new Route("A", "D", 5);
        newRoutes.addAll(routes);
        newRoutes.add(duplicateRoute);

        RouteMatrix<Integer> m = ConnectionRouteMatrix.newInstance();
        m.addRoutes(newRoutes);
        m.build();

        assertEquals(RouteParser.newInstance().parseCities(routes).size(),
                m.getMatrixDimensions());
        assertFalse(m.getMatrixRouteCities().contains(duplicateRoute));
    }

    @Test
    public void testAddNullRoute() {
        RouteMatrix<Integer> routeMatrix = ConnectionRouteMatrix.newInstance();
        routeMatrix.addRoutes(null);
    }

    @Test
    public void testMatrixRow() throws IllegalInputException {
        List<City> matrixCities = matrix.getMatrixRouteCities();
        printMatrix(matrix, "From Row:");
        for (City rowCity : matrixCities) {
            Map<City, Integer> matrixRow = matrix.getMatrixRow(rowCity);

            List<Route> routeFromRowCity = getRoutesByCities(rowCity.getName(),
                    null);
            assertEquals(routeFromRowCity.size(), matrixRow.size());

            for (City columnCity : matrixCities) {
                Integer matrixElementValue = matrixRow.get(columnCity);
                List<Route> routes = getRoutesByCities(rowCity.getName(),
                        columnCity.getName());
                if (matrixElementValue == null) {
                    assertTrue(routes.isEmpty());
                } else {
                    assertEquals(Integer.valueOf(1), matrixElementValue);
                    assertEquals(1, routes.size());
                }
            }
        }
    }

    @Test
    public void testMatrixColumn() throws IllegalInputException {
        List<City> matrixCities = matrix.getMatrixRouteCities();
        printMatrix(matrix, "From Column: ");
        for (City columnCity : matrixCities) {
            Map<City, Integer> matrixColumn = matrix
                    .getMatrixColumn(columnCity);

            List<Route> routeFromColumnCity = getRoutesByCities(null,
                    columnCity.getName());
            assertEquals(routeFromColumnCity.size(), matrixColumn.size());
            for (City rowCity : matrixCities) {
                Integer matrixElementValue = matrix.getMatrixElement(rowCity,
                        columnCity);
                List<Route> routes = getRoutesByCities(rowCity.getName(),
                        columnCity.getName());
                if (matrixElementValue == null) {
                    assertTrue(routes.isEmpty());
                } else {
                    assertEquals(Integer.valueOf(1), matrixElementValue);
                    assertEquals(1, routes.size());
                }
            }
        }
    }

    private void testMatrixEqual(RouteMatrix<Integer> m1,
            RouteMatrix<Integer> m2) {
        assertEquals(m1.getMatrixDimensions(), m2.getMatrixDimensions());

        List<City> matrix1Cities = m1.getMatrixRouteCities();
        List<City> matrix2Cities = m2.getMatrixRouteCities();

        assertArrayEquals(
                matrix1Cities.toArray(new City[matrix1Cities.size()]),
                matrix2Cities.toArray(new City[matrix2Cities.size()]));

        for (City rowCity : matrix1Cities) {
            for (City columnCity : matrix1Cities) {
                assertEquals(m1.getMatrixElement(rowCity, columnCity),
                        m2.getMatrixElement(rowCity, columnCity));
            }
        }
    }

    private void printMatrix(RouteMatrix<Integer> m, String title) {
        if (VERBOSE) {
            List<City> matrixCities = m.getMatrixRouteCities();
            logger.logln("");
            logger.logln(" " + title);
            for (City city : matrixCities) {
                logger.log(" " + city.getName() + "   ");
            }
            logger.logln("");

            for (City row : matrixCities) {
                logger.logln("");
                for (City column : matrixCities) {
                    Integer value = m.getMatrixElement(row, column);
                    logger.log(value == null ? " NIL " : " " + value + " ");
                }
            }
            logger.logln("");
        }
    }

    @Test
    public void testAddNull() {
        assertNull(matrix.add(null));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddEmptyMatrix() {
        RouteMatrix<Integer> emptyMatrix = ConnectionRouteMatrix.newInstance();
        matrix.add(emptyMatrix);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddDifferentDimensionMatrix() {
        RouteMatrix<Integer> m = ConnectionRouteMatrix.newInstance();
        m.addRoutes(Arrays.asList(new Route[] { new Route("A", "B", 12) }));
        m.build();
        m.add(matrix);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAddSameDifferentDimensionMatrixWithDifferentCities() {
        RouteMatrix<Integer> m1 = ConnectionRouteMatrix.newInstance();
        m1.addRoutes(Arrays.asList(new Route[] { new Route("A", "B", 12),
                new Route("C", "B", 21) }));
        m1.build();

        RouteMatrix<Integer> m2 = ConnectionRouteMatrix.newInstance();
        m2.addRoutes(Arrays.asList(new Route[] { new Route("A", "C", 12),
                new Route("C", "D", 21) }));
        m2.build();

        assertEquals(m1.getMatrixDimensions(), m2.getMatrixDimensions());
        m1.add(m2);
    }

    @Test
    public void testAddMatrix() throws IllegalInputException {
        String routeInput1 = "AB5, BC4, DC8, DE6, AD5, CE2, EB3, AE7, EF9, FA7";
        String routeInput2 = "AB5, BC4, CD8, DC8, DE6, CE2, EB3, AE7, EF9, AF10";

        RouteMatrix<Integer> m1 = ConnectionRouteMatrix.newInstance();
        m1.addRoutes(RouteParser.newInstance().parse(routeInput1));
        m1.build();

        RouteMatrix<Integer> m2 = ConnectionRouteMatrix.newInstance();
        m2.addRoutes(RouteParser.newInstance().parse(routeInput2));
        m2.build();

        assertTrue(m1.getMatrixDimensions() == m2.getMatrixDimensions());
        printMatrix(m1, "M1: ");
        printMatrix(m2, "M2: ");

        RouteMatrix<Integer> sum1 = m1.add(m2);
        RouteMatrix<Integer> sum2 = m2.add(m1);
        printMatrix(sum1, "Sum1: ");
        printMatrix(sum2, "Sum2: ");

        assertEquals(m1.getMatrixDimensions(), sum1.getMatrixDimensions());
        assertEquals(sum1.getMatrixDimensions(), sum2.getMatrixDimensions());

        testMatrixEqual(sum1, sum2);

        RouteMatrix<Integer> m3 = ConnectionRouteMatrix.newInstance();
        m3.addRoutes(RouteParser.newInstance().parse("AB2"));
        m3.build();

        RouteMatrix<Integer> m4 = ConnectionRouteMatrix.newInstance();
        m4.addRoutes(RouteParser.newInstance().parse("BA3"));
        m4.build();

        RouteMatrix<Integer> expectedSum = ConnectionRouteMatrix.newInstance();
        expectedSum.addRoutes(RouteParser.newInstance().parse("AB2, BA2"));
        expectedSum.build();

        printMatrix(m3, "M3: ");
        printMatrix(m4, "M4: ");
        printMatrix(expectedSum, "Expected Sum of M3 and M4");
        printMatrix(m3.add(m4), "Sum of M3 and M4: ");

        testMatrixEqual(m4.add(m3), expectedSum);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMultiplyEmptyMatrix() {
        RouteMatrix<Integer> emptyMatrix = ConnectionRouteMatrix.newInstance();
        matrix.multiply(emptyMatrix);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMultiplyDifferentDimensionMatrix() {
        RouteMatrix<Integer> m = ConnectionRouteMatrix.newInstance();
        m.addRoutes(Arrays.asList(new Route[] { new Route("A", "B", 12) }));
        m.build();
        m.multiply(matrix);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testMultiplySameDifferentDimensionMatrixWithDifferentCities() {
        RouteMatrix<Integer> m1 = ConnectionRouteMatrix.newInstance();
        m1.addRoutes(Arrays.asList(new Route[] { new Route("A", "B", 12),
                new Route("C", "B", 21) }));
        m1.build();

        RouteMatrix<Integer> m2 = ConnectionRouteMatrix.newInstance();
        m2.addRoutes(Arrays.asList(new Route[] { new Route("A", "C", 12),
                new Route("C", "D", 21) }));
        m2.build();

        assertEquals(m1.getMatrixDimensions(), m2.getMatrixDimensions());
        m1.multiply(m2);
    }

    @Test
    public void testMultiplyNull() {
        assertNull(matrix.multiply(null));
    }

    @Test
    public void testMultiplyMatrix() throws IllegalInputException {
        String routeInput1 = "AB5, BC4, DC8, DE6, AD5, CE2, EB3, AE7, EF9, FA7";
        String routeInput2 = "AB5, BC4, CD8, DC8, DE6, CE2, EB3, AE7, EF9, AF10";

        RouteMatrix<Integer> m1 = ConnectionRouteMatrix.newInstance();
        m1.addRoutes(RouteParser.newInstance().parse(routeInput1));
        m1.build();

        RouteMatrix<Integer> m2 = ConnectionRouteMatrix.newInstance();
        m2.addRoutes(RouteParser.newInstance().parse(routeInput2));
        m2.build();

        assertTrue(m1.getMatrixDimensions() == m2.getMatrixDimensions());
        printMatrix(m1, "M1: ");
        printMatrix(m2, "M2: ");

        RouteMatrix<Integer> product = m1.multiply(m2);
        printMatrix(product, "Product of M1 and M2: ");
        assertEquals(m1.getMatrixDimensions(), product.getMatrixDimensions());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testPowerNonPositiveExponent() {
        try {
            matrix.power(-1);
            fail("Failed");
        } catch (Exception e) {
            assertEquals(UnsupportedOperationException.class, e.getClass());
        }
        matrix.power(0);
    }

    @Test
    public void testPowerOfMatrix() throws IllegalInputException {
        String routesInput = "AB5, BC4, DC8";

        RouteMatrix<Integer> m = ConnectionRouteMatrix.newInstance();
        m.addRoutes(RouteParser.newInstance().parse(routesInput));
        m.build();

        testMatrixEqual(m.power(1), m);

        printMatrix(m, "Matrix: ");
        testMatrixEqual(m.power(2), m.multiply(m));
        printMatrix(m.power(2), "Matrix power 2: ");
        testMatrixEqual(m.power(3), m.multiply(m).multiply(m));
    }

    @Test
    public void testConnectedNumber() throws IllegalInputException {
        String input = "AB5, BC4, CD8, DC8, DE6, AD5, CE2, EB3, AE7";
        RouteMatrix<Integer> m = ConnectionRouteMatrix.newInstance();
        m.addRoutes(RouteParser.newInstance().parse(input));
        m.build();

        printMatrix(m, "Input Matrix: ");
        RouteMatrix<Integer> product = m.power(4);

        printMatrix(product, "Matrix power 4: ");
        // The number of routes between C and C with exactly 4 stops.
        assertEquals(3, Math.abs(product.getMatrixElement(new City("A"),
                new City("C"))));

        // The number of routes between C and C with maximum of 3 stops.
        City from = new City("C");
        City to = from;

        Integer with1Stop = m.getMatrixElement(from, to);
        Integer with2Stops = m.power(2).getMatrixElement(from, to);
        Integer with3Stops = m.power(3).getMatrixElement(from, to);
        assertEquals(
                2,
                Math.abs((with1Stop == null ? 0 : with1Stop)
                        + (with2Stops == null ? 0 : with2Stops)
                        + (with3Stops == null ? 0 : with3Stops)));

    }

    @Test
    public void testTransitiveClosure() throws IllegalInputException {
        String input = "AD1, BA2, BC3, CA5, CD6, DC7";

        RouteMatrix<Integer> m = ConnectionRouteMatrix.newInstance();
        m.addRoutes(RouteParser.newInstance().parse(input));
        m.build();

        printMatrix(m, "Input Matrix");

        RouteMatrix<Integer> closure = m.getMatrixTransitiveClosure();
        printMatrix(closure, "Transitive closure: ");
        RouteMatrix<Integer> closureByPower = m.add(m.power(2)).add(m.power(3))
                .add(m.power(4));
        printMatrix(closureByPower, "Closure by power: ");

        RouteMatrix<Integer> m2 = ConnectionRouteMatrix.newInstance();
        m2.addRoutes(Arrays.asList(new Route[] { new Route("A", "A", 2),
                new Route("A", "C", 3), new Route("B", "B", 4),
                new Route("C", "A", 5), new Route("C", "B", 6) }));
        m2.build();

        closure = m2.getMatrixTransitiveClosure();
        printMatrix(closure, "Transitive closure: ");
    }

    @Test
    public void testAddtionIdentify() throws IllegalInputException {
        String input = "AD1, BA2, BC3, CA5, CD6, DC7";

        RouteMatrix<Integer> m = ConnectionRouteMatrix.newInstance();
        m.addRoutes(RouteParser.newInstance().parse(input));
        m.build();

        RouteMatrix<Integer> identify = m.getAdditionIdentify();

        testMatrixEqual(m, m.add(identify));
        testMatrixEqual(m, identify.add(m));
    }

    @Test
    public void testMultiplyIdentify() throws IllegalInputException {
        String input = "AD1, BA2, BC3, CA5, CD6, DC7";

        RouteMatrix<Integer> m = ConnectionRouteMatrix.newInstance();
        m.addRoutes(RouteParser.newInstance().parse(input));
        m.build();

        RouteMatrix<Integer> identify = m.getMultiplyIdentify();

        testMatrixEqual(m, m.multiply(identify));
        testMatrixEqual(m, identify.multiply(m));
    }
    
    @Test(expected=BrokenRouteMatrixExcpetion.class)
    public void testGetMatrixElementException() throws IllegalInputException {
        String input = "AD1, BA2, BC3, CA5, CD6, DC7";

        RouteMatrix<Integer> m = ConnectionRouteMatrix.newInstance();
        m.addRoutes(RouteParser.newInstance().parse(input));
        m.build();
        
        City a = new City("A");
        City b = new City("D");
        m.getMatrixRow(a).put(b, Integer.valueOf(1));
        m.getMatrixColumn(b).put(a, Integer.valueOf(2));
        m.getMatrixElement(a, b);
    }
}
