package org.shizhz.matrix;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.shizhz.route.Route;

/**
 * The element in this matrix contains all path between two cities.
 * 
 * @author shizhz
 * 
 */
public class PathRouteMatrix extends AbstractRouteMatrix<List<Route>> {

    public PathRouteMatrix() {
    }

    public static RouteMatrix<List<Route>> newInstance() {
        return new PathRouteMatrix();
    }

    @Override
    protected List<Route> elementMultiply(List<Route> multiplicand,
            List<Route> multiplier) {
        if (multiplicand == null || multiplier == null) {
            return null;
        }

        if (multiplicand.isEmpty()) {
            return multiplier;
        }

        if (multiplier.isEmpty()) {
            return multiplicand;
        }

        List<Route> result = new ArrayList<Route>(multiplicand.size()
                * multiplier.size());

        for (Route firstPartRoute : multiplicand) {
            for (Route secondPartRoute : multiplier) {
                result.add(firstPartRoute.addInnerRoute(secondPartRoute));
            }
        }

        return result;
    }

    @Override
    protected List<Route> elementAdd(List<Route> addend, List<Route> augend) {
        if (addend == null && augend == null) {
            return null;
        }

        if (addend == null) {
            if (augend.isEmpty()) {
                // empty list is identify object.
                return null;
            } else {
                return augend;
            }
        }

        if (augend == null) {
            if (addend.isEmpty()) {
                return null;
            } else {
                return addend;
            }
        }

        Set<Route> result = new HashSet<Route>();
        result.addAll(addend);
        result.addAll(augend);

        return new ArrayList<Route>(result);
    }

    @Override
    protected RouteMatrix<List<Route>> newMatrixInstance() {
        return newInstance();
    }

    @Override
    protected List<Route> extractMatrixElement(Route route) {
        return Arrays.asList(new Route[] { route });
    }

    @Override
    protected List<Route> getIdentifyElement() {
        return new ArrayList<Route>();
    }
}
