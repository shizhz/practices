package org.shizhz.matrix;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.shizhz.route.City;
import org.shizhz.route.Route;

/**
 * <code>RouteMatrix</code> is the interface to operate the route graph. The
 * inner data structure is an square adjacency matrix, which is built on the
 * sorted cities in the route graph.
 * 
 * @author shizhz
 * 
 */
public interface RouteMatrix<T> {
    /**
     * The multiplication operation of RouteMatrix.
     * 
     * @param routeMatrix
     * @return The product RouteMatrix of multiplication.
     */
    RouteMatrix<T> multiply(RouteMatrix<T> routeMatrix);

    /**
     * The addition operation of RouteMatrix.
     * 
     * @param routeMatrix
     * @return The sum RouteMatrix of addition.
     */
    RouteMatrix<T> add(RouteMatrix<T> routeMatrix);

    /**
     * The power operation of RouteMatrix.
     * <p>
     * The exponent should be a positive integer currently.
     * 
     * @param exponent
     * @return The result RouteMatrix of power operation.
     */
    RouteMatrix<T> power(int exponent);

    /**
     * Get row from matrix for the specified city, which contains all routes
     * starting from the specified city.
     * 
     * @param city
     *            Source city
     * @return A map of Route list. All routes contained in the result list have
     *         the same source city.
     *         <p>
     *         null if the city is not in the matrix.
     */
    Map<City, T> getMatrixRow(City city);

    /**
     * Get row from matrix for the specified city, which contains all routes
     * ending at the specified city.
     * 
     * @param city
     *            Destination city
     * @return A map of Route list. All routes contained in the result list have
     *         the same destination city.
     *         <p>
     *         null if the city is not in the matrix.
     */
    Map<City, T> getMatrixColumn(City city);

    /**
     * Get the specified element in the matrix, which contains all routes start
     * from the city the row stands for, and end at the city the column stands
     * for.
     * 
     * @param rowCity
     * @param columnCity
     * @return A list of routes.
     */
    T getMatrixElement(City rowCity, City columnCity);

    /**
     * Get the matrix dimension, which actually equals the number of all cities.
     * 
     * @return The matrix dimension
     */
    int getMatrixDimensions();

    /**
     * Get the transitive closure of this matrix.
     * 
     * @return The transitive closure of this matrix.
     */
    RouteMatrix<T> getMatrixTransitiveClosure();

    /**
     * Set RouteMatrix element by row city and column city. What the element
     * means depends on the implementation of subclass.
     * 
     * @param rowCity
     * @param columnCity
     * @param element
     *            The element of the implementation matrix.
     */
    void setMatrixElement(City rowCity, City columnCity, T element);

    /**
     * Trigger to build this matrix.
     */
    void build();

    /**
     * Add a collection of <code>Route</code> into the matrix. This operation
     * will <B>not</B> trigger the rebuild of matrix.
     * 
     * @param routes
     */
    void addRoutes(Collection<Route> routes);

    /**
     * Add a collection of <code>City</code> into the matrix. The dimension of
     * the matrix equals to the number of all cities contained in it.
     * 
     * @param cities
     */
    void addMatrixRouteCities(Collection<City> cities);

    /**
     * Get all cities in this matrix. which is in the order by which the matrix
     * is built.
     * 
     * @return A list of cities.
     */
    List<City> getMatrixRouteCities();

    /**
     * Get the identify matrix for addition.
     * 
     * @return
     */
    RouteMatrix<T> getAdditionIdentify();

    /**
     * Get the identify matrix for multiply.
     * 
     * @return
     */
    RouteMatrix<T> getMultiplyIdentify();
}
