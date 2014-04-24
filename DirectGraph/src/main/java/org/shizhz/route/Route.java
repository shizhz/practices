package org.shizhz.route;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.shizhz.exception.UnconnectedRouteException;
import org.shizhz.util.Joiner;

/**
 * This class use to present a route between cities. Single route means this
 * route connects two cities directly, Composite type route means a path
 * connects two cities by crossing other city or cities.
 * 
 * @author shizhz
 * 
 */
public class Route implements Comparable<Route> {
    public enum Type {
        SIGNLE, COMPOSITE
    }

    private static final String ROUTE_CITY_DELIMITER = "-";

    private City sourceCity;

    private City destinationCity;

    private int distance;

    private List<Route> innerRoutes = new ArrayList<>();

    private Type type;

    public Route(City sourceCity, City destinationCity, int distance) {
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.distance = distance;
        this.type = Type.SIGNLE;
    }

    public Route(String sourceCityName, String destinationCityName, int distance) {
        this(new City(sourceCityName), new City(destinationCityName), distance);
    }

    public Type getType() {
        return type;
    }

    public City getSourceCity() {
        return sourceCity;
    }

    public City getDestinationCity() {
        return destinationCity;
    }

    public int getDistance() {
        return distance;
    }

    public List<Route> getInnerRoutes() {
        if (type == Type.SIGNLE) {
            return Collections.unmodifiableList(Arrays
                    .asList(new Route[] { this }));
        } else {
            return Collections.unmodifiableList(innerRoutes);
        }
    }

    private void addRoutes(List<Route> routes) {
        innerRoutes.addAll(routes);
        type = Type.COMPOSITE;
    }

    /**
     * The full representation of this route, which is a <code>String</code>
     * with all cities the route crossed, and delimited with delimiter '-'
     * 
     * @return Full representation of this route.
     */
    public String fullRouteRepr() {
        List<Route> allInnerRoutes = getInnerRoutes();
        String[] cities = new String[allInnerRoutes.size() + 1];

        for (int i = 0, size = allInnerRoutes.size(); i < size; i++) {
            cities[i] = allInnerRoutes.get(i).getSourceCity().getName();
            if (i + 1 == size) {
                cities[i + 1] = (allInnerRoutes.get(i).getDestinationCity()
                        .getName());
            }
        }

        return Joiner.on(ROUTE_CITY_DELIMITER).join(cities);
    }

    /**
     * The short representation of this route, which is a <code>String</code>
     * with only the source city and the destination city, and delimited with
     * delimiter '-'
     * 
     * @return The short representation of this route.
     */
    public String shortRouteRepr() {
        return Joiner.on(ROUTE_CITY_DELIMITER)
                .join(new String[] { sourceCity.getName(),
                        destinationCity.getName() });
    }

    @Override
    public String toString() {
        return getSourceCity().toString() + getDestinationCity().toString()
                + getDistance();
    }

    /**
     * Add another <code>Route</code> to this <code>Route</code> to get a new
     * <code>Route</code>.
     * 
     * @param route
     *            The <code>Route</code> to be added to this one.
     * @return New <code>Route</code> object.
     * @throws UnconnectedRouteException
     *             Thrown when the two routes are not connected.
     */
    public Route addInnerRoute(Route route) throws UnconnectedRouteException {
        if (route == null) {
            return this;
        }

        if (!route.startsWith(this)) {
            throw new UnconnectedRouteException("Route '" + route.toString()
                    + "' is not start from the end of the Route '"
                    + this.toString() + "'");
        }

        Route newRoute = new Route(sourceCity, route.getDestinationCity(),
                distance + route.getDistance());
        newRoute.addRoutes(getInnerRoutes());
        newRoute.addRoutes(route.getInnerRoutes());

        return newRoute;
    }

    /**
     * Check whether this <code>Route</code> starts where another
     * <code>Route</code> ends, which means the source city of this route is the
     * same with the destination city of another route.
     * 
     * @param route
     * @return
     */
    public boolean startsWith(Route route) {
        if (route == null) {
            return false;
        }

        return sourceCity.equals(route.getDestinationCity());
    }

    /**
     * Compare <code>Route</code> by the order of SourceCity --> DestinationCity
     * --> Distance
     * 
     */
    @Override
    public int compareTo(Route route) {
        int result = getSourceCity().compareTo(route.getSourceCity());
        if (result != 0) {
            return result;
        }

        result = getDestinationCity().compareTo(route.getDestinationCity());
        if (result != 0) {
            return result;
        }
        return getDistance() - route.getDistance();
    }

    private boolean compareSingleRoute(Route r1, Route r2) {
        return r1.getSourceCity().equals(r2.getSourceCity())
                && r1.getDestinationCity().equals(r2.getDestinationCity())
                && r1.getDistance() == r2.getDistance();
    }

    private boolean compareCompositeRoute(Route r1, Route r2) {
        List<Route> innerRoutesR1 = r1.getInnerRoutes();
        List<Route> innerRoutesR2 = r2.getInnerRoutes();

        if (innerRoutesR1.size() != innerRoutesR2.size()) {
            return false;
        }

        for (int i = 0; i < innerRoutesR1.size(); i++) {
            if (!innerRoutesR1.get(i).equals(innerRoutesR2.get(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Two Single type routes are considerd to be the same if and only if their
     * SourceCity/DestinationCity/Distance are equal respectively. Two Composite
     * routes are considered to be the same if and only if their inner routes
     * equal respectively.
     * 
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Route)) {
            return false;
        }

        Route route = Route.class.cast(obj);

        if (getType() != route.getType()) {
            return false;
        }

        if (getType() == Type.SIGNLE) {
            return compareSingleRoute(this, route);
        } else {
            return compareCompositeRoute(this, route);
        }
    }

    @Override
    public int hashCode() {
        int code = 1;

        for (Route route : getInnerRoutes()) {
            code = (int) route.getSourceCity().hashCode()
                    * route.getDestinationCity().hashCode() * route.distance
                    * code;
        }

        return code;
    }
}
