package org.shizhz.matrix;

import org.shizhz.route.Route;

/**
 * Used to represent the connectivities among all cities. The matrix element
 * value is negative integer, the absolute value of which represents the number
 * of routes between two cities.
 * 
 * @author shizhz
 * 
 */
public class ConnectionRouteMatrix extends AbstractRouteMatrix<Integer> {

    public ConnectionRouteMatrix() {
    }

    public static RouteMatrix<Integer> newInstance() {
        return new ConnectionRouteMatrix();
    }

    @Override
    protected Integer elementMultiply(Integer multiplicand, Integer multiplier) {
        if (multiplicand == null || multiplier == null) {
            return null;
        }

        if (multiplicand.equals(getIdentifyElement())) {
            return multiplier;
        }

        if (multiplier.equals(getIdentifyElement())) {
            return multiplicand;
        }

        return multiplicand * multiplier;
    }

    @Override
    protected Integer elementAdd(Integer addend, Integer augend) {
        if (addend == null && augend == null) {
            return null;
        }
        if (addend == null) {
            if (augend.equals(getIdentifyElement())) {
                return null;
            }
            return augend;
        }

        if (augend == null) {
            if (addend.equals(getIdentifyElement())) {
                return null;
            }
            return addend;
        }

        if (addend.equals(getIdentifyElement())) {
            return augend;
        }

        if (augend.equals(getIdentifyElement())) {
            return addend;
        }

        return addend + augend;
    }

    @Override
    protected RouteMatrix<Integer> newMatrixInstance() {
        return ConnectionRouteMatrix.newInstance();
    }

    @Override
    protected Integer extractMatrixElement(Route route) {
        return Integer.valueOf(1);
    }

    @Override
    protected Integer getIdentifyElement() {
        return Integer.valueOf(-1);
    }
}
