package org.shizhz.matrix;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.shizhz.exception.BrokenRouteMatrixExcpetion;
import org.shizhz.route.City;
import org.shizhz.route.Route;
import org.shizhz.route.RouteParser;

/**
 * The abstract class of <code>RouteMatrix</code> interface.
 * 
 * @author shizhz
 * @param <T>
 * 
 */
public abstract class AbstractRouteMatrix<T> implements RouteMatrix<T> {

    private List<City> matrixRouteCities = new ArrayList<City>();

    private List<Route> matrixRoutes = new ArrayList<Route>();

    private Map<City, Map<City, T>> matrixRowBucket = new TreeMap<City, Map<City, T>>();

    private Map<City, Map<City, T>> matrixColumnBucket = new TreeMap<City, Map<City, T>>();

    @Override
    public List<City> getMatrixRouteCities() {
        return Collections.unmodifiableList(matrixRouteCities);
    }

    protected Map<City, Map<City, T>> getMatrixRowBucket() {
        return Collections.unmodifiableMap(matrixRowBucket);
    }

    protected Map<City, Map<City, T>> getMatrixColumnBucket() {
        return Collections.unmodifiableMap(matrixColumnBucket);
    }

    @Override
    public RouteMatrix<T> power(int exponent) {
        if (exponent <= 0) {
            throw new UnsupportedOperationException(
                    "In current implementation, the exponent should be a positive integer.");
        }

        RouteMatrix<T> result = this;
        for (int i = exponent; i > 1; i--) {
            result = result.multiply(this);
        }

        return result;
    }

    @Override
    public int getMatrixDimensions() {
        return getMatrixRouteCities().size();
    }

    @Override
    public Map<City, T> getMatrixRow(City city) {
        Map<City, T> row = getMatrixRowBucket().get(city);
        if (row == null) {
            row = new TreeMap<City, T>();
        }

        return row;
    }

    @Override
    public Map<City, T> getMatrixColumn(City city) {
        Map<City, T> column = getMatrixColumnBucket().get(city);
        if (column == null) {
            column = new TreeMap<City, T>();
        }

        return column;
    }

    @Override
    public T getMatrixElement(City rowCity, City columnCity) {
        T valueInRowBucket = getMatrixRow(rowCity).get(columnCity);
        T valueInColumnBucket = getMatrixColumn(columnCity).get(rowCity);
        if (valueInRowBucket != valueInColumnBucket) {
            // The two objects found here are supposed to point to the same spot
            // of the memory.
            throw new BrokenRouteMatrixExcpetion(
                    "The matrix is broken. The value found by order row-> column is: "
                            + valueInRowBucket.toString()
                            + " and the value found by order column -> row is: "
                            + valueInColumnBucket.toString()
                            + ", but they are supposed to point to the same object.");
        }
        return valueInRowBucket;
    }

    @Override
    public void setMatrixElement(City rowCity, City columnCity, T element) {
        Map<City, T> row = matrixRowBucket.get(rowCity);
        if (row == null) {
            row = new TreeMap<City, T>();
            matrixRowBucket.put(rowCity, row);
        }
        row.put(columnCity, element);

        Map<City, T> column = matrixColumnBucket.get(columnCity);
        if (column == null) {
            column = new TreeMap<City, T>();
            matrixColumnBucket.put(columnCity, column);
        }
        column.put(rowCity, element);
    }

    @Override
    public RouteMatrix<T> getMatrixTransitiveClosure() {
        // Warshall's algorithm to get the transitive closure of this matrix.
        // Let's define a set S which contains all cities in this RouteMatrix,
        // and the relation R on it.
        // A pair (a, b) belongs to R means there is a route from city a to city
        // b.
        RouteMatrix<T> transitiveClosure = getMultiplyIdentify().multiply(this);
        List<City> matrixCities = getMatrixRouteCities();
        int size = getMatrixDimensions();

        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    City cityK = matrixCities.get(k);
                    City cityI = matrixCities.get(i);
                    City cityJ = matrixCities.get(j);
                    T element = elementAdd(
                            transitiveClosure.getMatrixElement(cityI, cityJ),
                            elementMultiply(transitiveClosure.getMatrixElement(
                                    cityI, cityK), transitiveClosure
                                    .getMatrixElement(cityK, cityJ)));
                    transitiveClosure.setMatrixElement(cityI, cityJ, element);
                }
            }
        }

        return transitiveClosure;
    }

    @Override
    public RouteMatrix<T> add(RouteMatrix<T> routeMatrix) {
        if (routeMatrix == null) {
            return null;
        }

        if (getMatrixDimensions() != routeMatrix.getMatrixDimensions()) {
            throw new UnsupportedOperationException(
                    "It's not support to add two matrix with different dimensions");
        }

        if (!getMatrixRouteCities().containsAll(
                routeMatrix.getMatrixRouteCities())) {
            throw new UnsupportedOperationException(
                    "It's not support to add two route matrix with different set of cities");
        }
        List<City> matrixCities = getMatrixRouteCities();
        RouteMatrix<T> sum = newMatrixInstance();

        sum.addMatrixRouteCities(matrixCities);

        int size = getMatrixDimensions();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                City row = matrixCities.get(i);
                City column = matrixCities.get(j);
                T element = elementAdd(getMatrixElement(row, column),
                        routeMatrix.getMatrixElement(row, column));
                if (element != null) {
                    sum.setMatrixElement(row, column, element);
                }
            }
        }

        return sum;
    }

    @Override
    public RouteMatrix<T> multiply(RouteMatrix<T> routeMatrix) {
        if (routeMatrix == null) {
            return null;
        }

        if (getMatrixDimensions() != routeMatrix.getMatrixDimensions()) {
            throw new UnsupportedOperationException(
                    "It's not support to multiply two square matrix with different dimensions");
        }

        if (!getMatrixRouteCities().containsAll(
                routeMatrix.getMatrixRouteCities())) {
            throw new UnsupportedOperationException(
                    "It's not support to multiply two square route matrix with different set of cities");
        }

        RouteMatrix<T> product = newMatrixInstance();
        List<City> matrixCities = getMatrixRouteCities();
        product.addMatrixRouteCities(matrixCities);

        int size = getMatrixDimensions();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                T element = null;
                City row = matrixCities.get(i);
                City column = matrixCities.get(j);
                for (int k = 0; k < size; k++) {
                    City cursor = matrixCities.get(k);
                    element = elementAdd(
                            element,
                            elementMultiply(getMatrixElement(row, cursor),
                                    routeMatrix
                                            .getMatrixElement(cursor, column)));
                }
                if (element != null) {
                    product.setMatrixElement(row, column, element);
                }
            }
        }

        return product;
    }

    @Override
    public void addMatrixRouteCities(Collection<City> cities) {
        for (City city : cities) {
            if (!matrixRouteCities.contains(city)) {
                matrixRouteCities.add(city);
            }
        }

        Collections.sort(matrixRouteCities);
    }

    @Override
    public void addRoutes(Collection<Route> routes) {
        if (routes == null) {
            return;
        }

        for (Route route : routes) {
            if (!matrixRoutes.contains(route)) {
                if (route.getType() != Route.Type.SIGNLE) {
                    throw new IllegalArgumentException(
                            "Only Single type route can be added into matrix");
                }
                matrixRoutes.add(route);
            }
        }
    }

    @Override
    public void build() {
        if (matrixRoutes.isEmpty()) {
            return;
        }

        addMatrixRouteCities(RouteParser.newInstance()
                .parseCities(matrixRoutes));

        for (Route route : matrixRoutes) {
            City sourceCity = route.getSourceCity();
            City destCity = route.getDestinationCity();
            setMatrixElement(sourceCity, destCity, extractMatrixElement(route));
        }
    }

    @Override
    public RouteMatrix<T> getAdditionIdentify() {
        RouteMatrix<T> identify = newMatrixInstance();
        List<City> cities = getMatrixRouteCities();

        identify.addMatrixRouteCities(cities);

        for (City row : cities) {
            for (City column : cities) {
                identify.setMatrixElement(row, column, getIdentifyElement());
            }
        }

        return identify;
    }

    @Override
    public RouteMatrix<T> getMultiplyIdentify() {
        RouteMatrix<T> identify = newMatrixInstance();
        List<City> cities = getMatrixRouteCities();
        identify.addMatrixRouteCities(cities);

        for (City row : cities) {
            for (City column : cities) {
                if (row.equals(column)) {
                    identify.setMatrixElement(row, column, getIdentifyElement());
                }
            }
        }
        return identify;
    }

    protected abstract T elementMultiply(T multiplicand, T multiplier);

    protected abstract T elementAdd(T addend, T augend);

    protected abstract RouteMatrix<T> newMatrixInstance();

    protected abstract T extractMatrixElement(Route route);

    protected abstract T getIdentifyElement();
}
