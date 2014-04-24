package org.shizhz.route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.shizhz.exception.IllegalInputException;
import org.shizhz.exception.NoRouteExistingException;
import org.shizhz.matrix.ConnectionRouteMatrix;
import org.shizhz.matrix.PathRouteMatrix;
import org.shizhz.matrix.RouteMatrix;

/**
 * The class <code>RouteNetwork</code> represents the network of filled routes.
 * It provides some basic interfaces to provide some basic information of this
 * route.
 * 
 * @author shizhz
 * 
 */
public final class RouteNetwork {
    private static final String ROUTE_DELIMITER = "-";

    private static final String ROUTE_INPUT_PATTERN = "([A-Z]-)+[A-Z]$";

    private static final String ROUTE_PAIR_INPUT_PATTERN = "[A-Z]-[A-Z]";

    private static final String MESSAGE_NO_SUCH_ROUTE = "NO SUCH ROUTE";

    private RouteMatrix<List<Route>> pathMatrix = PathRouteMatrix
            .newInstance();

    private RouteMatrix<Integer> connectedMatrix = ConnectionRouteMatrix
            .newInstance();

    private RouteMatrix<List<Route>> trasitiveClosure = PathRouteMatrix
            .newInstance();

    public RouteNetwork() {
    }

    public RouteNetwork(String routesInput) throws IllegalInputException {
        this(RouteParser.newInstance().parse(routesInput));
    }

    public RouteNetwork(Collection<Route> routes) {
        this.addRoutes(routes);
    }

    private void validateRoute(String route, String pattern)
            throws IllegalInputException {
        if (route == null || "".equals(route)) {
            throw new IllegalInputException(
                    "Please input a valid route. e.g. A-D");
        }

        if (!route.matches(pattern)) {
            throw new IllegalInputException(
                    "The route is not valid. The valid format is like: A-D");
        }
    }

    private City getSourceCity(String routes) {
        return new City(routes.split(ROUTE_DELIMITER)[0]);
    }

    private City getDestCity(String routes) {
        String[] array = routes.split(ROUTE_DELIMITER);
        return new City(array[array.length - 1]);
    }

    private int getStops(String routes) {
        return routes.split(ROUTE_DELIMITER).length - 1;
    }

    private List<Route> findRoutes(City from, City to, int stops) {
        List<Route> result = pathMatrix.power(stops).getMatrixElement(from,
                to);
        return result == null ? new ArrayList<Route>() : result;
    }

    /**
     * Find route according to the input String representation.
     * 
     * @param routeMatrix
     *            The internal route matrix used to find the target route.
     * @param route
     *            The String representation route, valid format is like: A-B-D
     * @return
     */
    private Route findRoute(String route) {
        List<Route> results = findRoutes(getSourceCity(route),
                getDestCity(route), getStops(route));

        if (results == null || results.isEmpty()) {
            return null;
        }

        Route result = null;
        for (Route r : results) {
            if (r.fullRouteRepr().equals(route)) {
                result = r;
                break;
            }
        }

        return result;
    }

    /**
     * Add routes to this route network.
     * 
     * @param routes
     */
    public void addRoutes(Collection<Route> routes) {
        pathMatrix.addRoutes(routes);
        pathMatrix.build();
        trasitiveClosure = pathMatrix.getMatrixTransitiveClosure();
        connectedMatrix.addRoutes(routes);
        connectedMatrix.build();
    }

    /**
     * Add routes to this route network
     * 
     * @param routes
     * @throws IllegalInputException
     */
    public void addRoutes(String routes) throws IllegalInputException {
        addRoutes(RouteParser.newInstance().parse(routes));
    }

    /**
     * Find the distance of the specified route represented by input String. The
     * valid format is like: A-B-D
     * 
     * @param route
     *            The String represented route.
     * @return The distance of the target route.
     * @throws IllegalInputException
     *             Thrown if the input is not valid.
     * @throws NoRouteExistingException
     *             Thrown if no such route found.
     */
    public int findRouteDistance(String route) throws IllegalInputException,
            NoRouteExistingException {
        validateRoute(route, ROUTE_INPUT_PATTERN);

        Route result = findRoute(route);

        if (result == null) {
            throw new NoRouteExistingException(MESSAGE_NO_SUCH_ROUTE);
        }
        return result.getDistance();
    }

    /**
     * Find the amount number of trip according to the route pair. With the
     * number of stops the target route croossed.
     * 
     * @param route
     *            The route pair.
     * @param stops
     *            Exact stops the route crossed.
     * @return The amount number of different trips.
     * @throws IllegalInputException
     *             Thrown if the input is not valid.
     * @throws NoRouteExistingException
     *             Thrown if no trip found.
     */
    public int tripsAmountWithStops(String route, int stops)
            throws IllegalInputException, NoRouteExistingException {
        validateRoute(route, ROUTE_PAIR_INPUT_PATTERN);

        if (stops < 1) {
            throw new IllegalInputException(
                    "The number of stops can not less than 1.");
        }

        RouteMatrix<Integer> power = connectedMatrix.power(stops);
        Integer tripsAmount = power.getMatrixElement(getSourceCity(route),
                getDestCity(route));

        if (tripsAmount == null) {
            throw new NoRouteExistingException(MESSAGE_NO_SUCH_ROUTE);
        }

        return tripsAmount;
    }

    /**
     * Find the amount number of trips according to the route pair. All trips
     * crosses less equal than the specified stops number are included.
     * 
     * @param route
     *            The route pair.
     * @param stops
     *            The maximum stops each trip crossed.
     * @return The amount number of different trips.
     * @throws IllegalInputException
     *             Thrown if the input is not valid.
     * @throws NoRouteExistingException
     *             Thrown if no trip found.
     */
    public int tripsAmountWithMaximumStops(String route, int stops)
            throws IllegalInputException, NoRouteExistingException {
        validateRoute(route, ROUTE_PAIR_INPUT_PATTERN);

        if (stops < 1) {
            throw new IllegalInputException(
                    "The number of stops can not less than 1.");
        }

        City from = getSourceCity(route);
        City to = getDestCity(route);

        Integer tripsAmount = 0;

        for (int i = 1; i <= stops; i++) {
            RouteMatrix<Integer> power = connectedMatrix.power(i);
            Integer trips = power.getMatrixElement(from, to);
            tripsAmount += (trips == null ? 0 : trips);
        }

        if (tripsAmount == 0) {
            throw new NoRouteExistingException(MESSAGE_NO_SUCH_ROUTE);
        }
        return tripsAmount;
    }

    /**
     * Find the trips amount number of a city pair, with maximum distance is
     * less than a specified distance.
     * 
     * @param route
     *            The city pair
     * @param distance
     *            The distance threshold value
     * @return
     * @throws IllegalInputException
     * @throws NoRouteExistingException
     */
    public int tripsLessThanDistance(String route, int distance)
            throws IllegalInputException, NoRouteExistingException {
        validateRoute(route, ROUTE_PAIR_INPUT_PATTERN);

        if (distance <= 0) {
            throw new IllegalInputException(
                    "The distance threshold should be a positive number.");
        }

        List<Route> candidates = new ArrayList<>();
        int dimension = pathMatrix.getMatrixDimensions();
        RouteMatrix<List<Route>> product = pathMatrix.getMultiplyIdentify();
        City from = getSourceCity(route);
        City to = getDestCity(route);

        while (true) {
            List<Route> loopResult = new ArrayList<Route>();

            for (int i = 1; i <= dimension; i++) {
                // Each possible cycle in one path must be less than the
                // dimension of this matrix, which is
                // the number of cities involved.
                product = product.multiply(pathMatrix);
                // The path between the specified city pair with exact
                // <loopNumber - 1> * dimension + i
                // stops.
                List<Route> rs = product.getMatrixElement(from, to);
                if (rs != null) {
                    loopResult.addAll(rs);
                }
            }

            Collections.sort(loopResult, new Comparator<Route>() {
                @Override
                public int compare(Route o1, Route o2) {
                    return o1.getDistance() - o2.getDistance();
                }
            });

            if (loopResult.isEmpty()
                    || loopResult.get(0).getDistance() > distance) {
                // If no route found in this loop, or the shortest path in this
                // loop has already exceeded
                // the distance threshold, we can stop searching.
                break;
            }

            candidates.addAll(loopResult);
        }

        if (candidates.isEmpty()) {
            throw new NoRouteExistingException(MESSAGE_NO_SUCH_ROUTE);
        }

        List<Route> result = new ArrayList<>();

        for (Route r : candidates) {
            if (r.getDistance() < distance) {
                result.add(r);
            }
        }
        return result.size();
    }

    /**
     * Find the shortest path between two city.
     * 
     * @param route
     *            The input city pair.
     * @return The shortest path.
     * @throws IllegalInputException
     *             Thrown if the input is not valid.
     * @throws NoRouteExistingException
     *             Thrown if no route found.
     */
    public Route shortestPath(String route) throws IllegalInputException,
            NoRouteExistingException {
        validateRoute(route, ROUTE_PAIR_INPUT_PATTERN);
        List<Route> routes = trasitiveClosure.getMatrixElement(
                getSourceCity(route), getDestCity(route));

        if (routes == null) {
            throw new NoRouteExistingException(MESSAGE_NO_SUCH_ROUTE);
        }

        Collections.sort(routes, new Comparator<Route>() {

            @Override
            public int compare(Route o1, Route o2) {
                return o1.getDistance() - o2.getDistance();
            }
        });

        return routes.get(0);
    }

    /**
     * Print a well-formated route network represented by matrix
     * 
     * @return
     */
    public String repr() {
        StringBuilder result = new StringBuilder("\n    ");
        List<City> cities = connectedMatrix.getMatrixRouteCities();
        for (City city : cities) {
            result.append(String.format("%4s", city.getName()));
        }
        result.append("\n");

        for (City row : cities) {
            result.append(String.format("%4s", row.getName()));
            for (City column : cities) {
                Integer element = connectedMatrix.getMatrixElement(row, column);
                result.append(String.format("%4s", element == null ? 0
                        : element));
            }
            result.append("\n");
        }

        return result.toString();
    }
}
