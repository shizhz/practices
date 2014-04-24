package org.shizhz.directive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.shizhz.exception.DirectiveException;
import org.shizhz.exception.IllegalInputException;
import org.shizhz.exception.NoRouteExistingException;
import org.shizhz.exception.UnRecognizedDirectiveException;
import org.shizhz.route.Route;
import org.shizhz.route.RouteNetwork;
import org.shizhz.route.RouteParser;
import org.shizhz.util.Joiner;

/**
 * This class used to process directives. 
 * 
 * @author shizhz
 *
 */
public class DirectiveProcessor {
    private static final String RESULT_TITLE_DELIMITER = " : ";

    private static final String MULTI_RESULT_DELIMITER = ", ";

    private static final String OPERATION_DONE = "Done";

    private DirectiveParser parser = DirectiveParser.newInstance();

    private RouteNetwork routeNetwork = new RouteNetwork();

    public static DirectiveProcessor newInstance() {
        return new DirectiveProcessor();
    }

    private void parameterRequired(DirectiveInfo directive)
            throws DirectiveException {
        String[] params = directive.getDirectiveParams();
        if (params.length == 0) {
            throw new DirectiveException("Directive "
                    + directive.getDirectiveName() + " needs parameters.");
        }
    }

    /**
     * Add route to route network
     * 
     * @param directive
     * @return
     * @throws IllegalInputException
     * @throws DirectiveException
     */
    private String processAddRoute(DirectiveInfo directive)
            throws IllegalInputException, DirectiveException {
        parameterRequired(directive);
        routeNetwork.addRoutes(Joiner.on(RouteParser.ROUTE_INPUT_SEPERATOR)
                .join(directive.getDirectiveParams()));
        return OPERATION_DONE;
    }

    /**
     * Process directive of finding distance between routes. A list of routes
     * can be process at one time.
     * 
     * @param directive
     * @return
     * @throws DirectiveException
     * @throws IllegalInputException
     * @throws NoRouteExistingException
     */
    private String processFindDistance(DirectiveInfo directive)
            throws DirectiveException, IllegalInputException {
        parameterRequired(directive);

        StringBuilder result = new StringBuilder();
        for (String route : directive.getDirectiveParams()) {
            try {
                int distance = routeNetwork.findRouteDistance(route);
                result.append(route + RESULT_TITLE_DELIMITER + distance
                        + MULTI_RESULT_DELIMITER);
            } catch (NoRouteExistingException nee) {
                result.append(route + RESULT_TITLE_DELIMITER + nee.getMessage()
                        + MULTI_RESULT_DELIMITER);
            }
        }

        String distance = result.toString();
        distance = distance.substring(0,
                distance.lastIndexOf(MULTI_RESULT_DELIMITER));

        return distance + "\n";
    }

    private int getNumericParameter(String param) throws DirectiveException {
        try {
            return Integer.valueOf(param);
        } catch (NumberFormatException nfe) {
            throw new DirectiveException(
                    "The stops should be a positive number");
        }
    }

    /**
     * Process directive of finding trips amount of a route with specified stops
     * number.
     * 
     * @param directive
     * @return
     * @throws DirectiveException
     * @throws IllegalInputException
     * @throws NoRouteExistingException
     */
    private String processTripsWithStops(DirectiveInfo directive)
            throws DirectiveException, IllegalInputException,
            NoRouteExistingException {
        parameterRequired(directive);

        if (directive.getDirectiveParams().length != 2) {
            throw new DirectiveException(
                    "Wrong parameter to query trips. The valid format is like `A-B-C, 4`, but get "
                            + directive.getDirective());
        }

        String route = directive.getDirectiveParams()[0];

        int trips = routeNetwork.tripsAmountWithStops(route,
                getNumericParameter(directive.getDirectiveParams()[1]));

        return route + RESULT_TITLE_DELIMITER + trips;
    }

    /**
     * Process directive of finding trips amount of a route with maximum stops
     * number.
     * 
     * @param directive
     * @return
     * @throws DirectiveException
     * @throws IllegalInputException
     * @throws NoRouteExistingException
     */
    private String processTripsWithMaximumStops(DirectiveInfo directive)
            throws DirectiveException, IllegalInputException,
            NoRouteExistingException {
        parameterRequired(directive);

        if (directive.getDirectiveParams().length != 2) {
            throw new DirectiveException(
                    "Wrong parameter to query trips. The valid format is like `A-B-C, 4`, but get "
                            + directive.getDirective());
        }

        String route = directive.getDirectiveParams()[0];
        int trips = routeNetwork.tripsAmountWithMaximumStops(route,
                getNumericParameter(directive.getDirectiveParams()[1]));

        return route + RESULT_TITLE_DELIMITER + trips;
    }

    /**
     * Process directive of finding different trips amount between two city with
     * distance less than a specified number.
     * 
     * @param directive
     * @return
     * @throws DirectiveException
     * @throws IllegalInputException
     * @throws NoRouteExistingException
     */
    private String processTripsLessThanDistance(DirectiveInfo directive)
            throws DirectiveException, IllegalInputException,
            NoRouteExistingException {
        parameterRequired(directive);

        if (directive.getDirectiveParams().length != 2) {
            throw new DirectiveException(
                    "Wrong parameter to query trips. The valid format is like `A-C, 4`, but get "
                            + directive.getDirective());
        }

        String route = directive.getDirectiveParams()[0];

        int trips = routeNetwork.tripsLessThanDistance(route,
                getNumericParameter(directive.getDirectiveParams()[1]));

        return route + RESULT_TITLE_DELIMITER + trips;
    }

    /**
     * Process the directive of find the shortest distance between two cities.
     * Several pairs of cities can be process at one time.
     * 
     * @param directive
     * @return
     * @throws DirectiveException
     * @throws IllegalInputException
     */
    private String processFindShortestPath(DirectiveInfo directive)
            throws DirectiveException, IllegalInputException {
        parameterRequired(directive);

        StringBuilder result = new StringBuilder("");

        for (String route : directive.getDirectiveParams()) {
            try {
                Route path = routeNetwork.shortestPath(route);
                result.append(route + RESULT_TITLE_DELIMITER
                        + path.getDistance() + MULTI_RESULT_DELIMITER);
            } catch (NoRouteExistingException nee) {
                result.append(route + RESULT_TITLE_DELIMITER + nee.getMessage()
                        + MULTI_RESULT_DELIMITER);
            }
        }

        String path = result.toString();
        path = path.substring(0, path.lastIndexOf(MULTI_RESULT_DELIMITER));

        return path + "\n";
    }

    /**
     * Process the print route network directive.
     * 
     * @return
     */
    private String processPrintRouteNetwork() {
        String result = routeNetwork.repr();
        if ("".equals(result.trim())) {
            return "Route network is empty";
        }

        return result;
    }

    private int getMaxLengthOfDirective(
            List<DirectiveInfo.DirectiveType> directiveTypes) {
        int maxLength = 0;
        for (DirectiveInfo.DirectiveType directive : directiveTypes) {
            maxLength = Math.max(maxLength, directive.name().length());
        }

        return maxLength;
    }

    private int getMaxLengthOfDirective(String[] directives) {
        int maxLength = 0;
        for (String directive : directives) {
            maxLength = Math.max(maxLength, directive.length());
        }

        return maxLength;
    }

    private String formatDirective(String directiveName, String directiveDesc,
            int headerLength) {
        return String.format("%" + headerLength + "s : %s\n", directiveName,
                directiveDesc);
    }

    /**
     * Provide the help usage information.
     * 
     * @return
     */
    private String processHelp(DirectiveInfo directive) {
        StringBuilder result = new StringBuilder("\n");

        List<DirectiveInfo.DirectiveType> requestedDirectives = new ArrayList<>();
        int headerLength = 0;

        if (directive.getDirectiveParams().length == 0) {
            requestedDirectives.addAll(Arrays
                    .asList(DirectiveInfo.DirectiveType.values()));
            headerLength = getMaxLengthOfDirective(requestedDirectives);
        } else {
            headerLength = getMaxLengthOfDirective(directive
                    .getDirectiveParams());
            for (String direct : directive.getDirectiveParams()) {
                try {
                    requestedDirectives.add(DirectiveInfo.DirectiveType
                            .valueOf(direct));
                } catch (Exception e) {
                    result.append(formatDirective(direct,
                            "Unsupported directive.", headerLength));
                }
            }
        }

        for (DirectiveInfo.DirectiveType direct : requestedDirectives) {
            result.append(formatDirective(direct.name(), direct.getDesc(),
                    headerLength));
        }

        return result.toString();
    }

    /**
     * Process a directive and return the return a String representation result.
     * 
     * @param directive
     * @return
     * @throws DirectiveException
     * @throws IllegalInputException
     */
    public String process(DirectiveInfo directive) throws DirectiveException {
        if (directive == null) {
            return "";
        }

        String result = "";
        try {
            switch (directive.getDirectiveType()) {
                case G:
                    result = processAddRoute(directive);
                    break;
                case D:
                    result = processFindDistance(directive);
                    break;
                case TS:
                    result = processTripsWithStops(directive);
                    break;
                case TMS:
                    result = processTripsWithMaximumStops(directive);
                    break;
                case TLTD:
                    result = processTripsLessThanDistance(directive);
                    break;
                case SD:
                    result = processFindShortestPath(directive);
                    break;
                case PRINT:
                    result = processPrintRouteNetwork();
                    break;
                case HELP:
                    result = processHelp(directive);
                    break;
            }
        } catch (Exception e) {
            throw new DirectiveException(e.getMessage() + "\n");
        }
        return directive.getDirectiveType().getDesc() + " " + result;
    }

    /**
     * Process a directive and return the return a String representation result.
     * 
     * @param directive
     * @return
     * @throws DirectiveException
     */
    public String process(String directive) throws DirectiveException {
        try {
            return process(parser.parse(directive));
        } catch (UnRecognizedDirectiveException ude) {
            throw new DirectiveException(ude);
        }
    }
}
