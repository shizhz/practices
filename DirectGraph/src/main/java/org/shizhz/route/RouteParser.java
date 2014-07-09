package org.shizhz.route;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.shizhz.exception.IllegalInputException;

/**
 * The class <code>RouteParser</code> is used to parse routes information to a
 * List of <code>Route</code> object.
 * 
 * @author shizhz
 * 
 */
public class RouteParser {
    public static final String ROUTE_INPUT_SEPERATOR = ",";

    private static final String CITY_LEGAL_PATTERN = "[A-Z]";

    private static final String ROUTE_DISTANCE_LEGAL_PATTERN = "[1-9][0-9]*$";

    public static RouteParser newInstance() {
        return new RouteParser();
    }

    private String buildRoutePattern() {
        return CITY_LEGAL_PATTERN + CITY_LEGAL_PATTERN
                + ROUTE_DISTANCE_LEGAL_PATTERN;
    }

    private boolean validateRoute(String route) {
        return route.matches(buildRoutePattern());
    }

    /**
     * Split the raw routes input information to a List, throws
     * <code>IllegalInputFormatException</code> if it is not legally formatted.
     * 
     * @param routesInput
     *            the input of routes information, The
     * @param delimiter TODO
     * @return A List contains all routes
     * @throws IllegalInputException
     */
    private List<String> splitRoutes(String routesInput, String delimiter)
            throws IllegalInputException {
        if (routesInput == null) {
            throw new IllegalInputException("Input for routes can not be empty");
        }

        String[] routes = routesInput.split(ROUTE_INPUT_SEPERATOR);
        List<String> results = new ArrayList<String>(routes.length);

        for (String route : routes) {
            route = route.trim();

            if ("".equals(route)) {
                continue;
            }

            if (!validateRoute(route)) {
                throw new IllegalInputException(
                        "Route '"
                                + route
                                + "' is not well formatted, the accepted form is two city codes followed by a distance. Such as: AB12. But found "
                                + route);
            }

            results.add(route);
        }

        return results;
    }

    /**
     * Extract a <code>java.util.List</code> of substring according the given
     * pattern.
     * 
     * @param input
     *            The input string from which the substring extract.
     * @param pattern
     *            The pattern used to extract substrings.
     * @return A list containing all matched substrings.
     */
    private List<String> getSubstringListByPattern(String input, String pattern) {
        List<String> targetGroup = new ArrayList<String>();

        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(input);
        while (matcher.find()) {
            targetGroup.add(matcher.group());
        }

        return targetGroup;
    }

    private City parseSourceCity(String route) {
        String name = getSubstringListByPattern(route, CITY_LEGAL_PATTERN).get(
                0);

        return new City(name);
    }

    private City parseDestinationCity(String route) {
        String name = getSubstringListByPattern(route, CITY_LEGAL_PATTERN).get(
                1);

        return new City(name);
    }

    private int parseDistance(String route) {
        return Integer.valueOf(
                getSubstringListByPattern(route, ROUTE_DISTANCE_LEGAL_PATTERN)
                        .get(0)).intValue();
    }

    private Route parseRoute(String route) {
        return new Route(parseSourceCity(route), parseDestinationCity(route),
                parseDistance(route));
    }

    /**
     * Parse the raw input of routes information to a list of <code>Route</code>
     * objects.
     * 
     * @param routesInput
     *            The raw input of routes information, which should be a
     *            well-formatted string.
     * @return A list containing <code>Route</code> objects.
     * @throws IllegalInputException
     */
    public List<Route> parse(String routesInput) throws IllegalInputException {
        return parse(routesInput, ROUTE_INPUT_SEPERATOR);
    }
    
    
    
    /**
     * Parse the raw of routes information to a list of <code>Route</code> objects, using specified delimiter.
     * 
     * @param routesInput The raw input of routes information.
     * @param delimiter 
     * @return
     * @throws IllegalInputException
     */
    public List<Route> parse(String routesInput, String delimiter) throws IllegalInputException {
        Map<String, Route> routes = new LinkedHashMap<>();

        for (String route : splitRoutes(routesInput, delimiter)) {
            Route r = parseRoute(route);

            if (routes.containsKey(r.shortRouteRepr())) {
                throw new IllegalInputException(
                        "Duplicate routes are not permitted: " + route + ", "
                                + routes.get(r.shortRouteRepr()).toString());
            }

            if (r.getSourceCity().equals(r.getDestinationCity())) {
                throw new IllegalInputException(
                        "A route can not has the same source city and destination city: "
                                + r.toString());
            }

            routes.put(r.shortRouteRepr(), r);
        }
        return new ArrayList<Route>(routes.values());
    }

    /**
     * Parse all cities from routes information given.
     * 
     * @param routesInput
     *            The raw input of routes information, which should be a
     *            well-formatted string.
     * @return A set containing all cities.
     * @throws IllegalInputException
     */
    public Set<City> parseCities(String routesInput)
            throws IllegalInputException {
        Set<City> cities = new HashSet<>();

        for (String route : splitRoutes(routesInput, ROUTE_INPUT_SEPERATOR)) {
            cities.add(parseSourceCity(route));
            cities.add(parseDestinationCity(route));
        }

        return cities;
    }

    /**
     * Parse all cities from a list of <code>Route</code> objects.
     * 
     * @param routes
     *            A list of routes.
     * @return A set containing all cities.
     */
    public Set<City> parseCities(Collection<Route> routes) {
        Set<City> cities = new HashSet<>();

        for (Route route : routes) {
            cities.add(route.getSourceCity());
            cities.add(route.getDestinationCity());
        }

        return cities;
    }
}
