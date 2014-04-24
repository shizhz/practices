package org.shizhz.route;

/**
 * This class used to present a city. The name is supposed to be unique as a city code.
 * 
 * @author shizhz
 *
 */
public class City implements Comparable<City> {

    private String name;

    public City(String name) {
        this.name = name == null ? "" : name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof City)) {
            return false;
        }

        return name.equals(obj.toString());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(City o) {
        return getName().compareTo(o.getName());
    }
}
