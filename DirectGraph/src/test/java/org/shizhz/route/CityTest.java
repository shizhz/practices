package org.shizhz.route;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class CityTest {

    @Test
    public void testCityToString() {
        City city = new City("Chengdu");
        assertEquals("Chengdu", city.toString());
        city.setName("Beijing");
        assertEquals("Beijing", city.toString());
    }

    @Test
    public void testCityEquality() {
        String name = "chengdu";
        City cd = new City(name);

        // reflexive
        assertTrue(cd.equals(cd));
        City cdNew = new City(name);

        // symmetric
        assertTrue(cd.equals(cdNew));
        assertTrue(cdNew.equals(cd));
        City cdEvenNewer = new City(name);

        // transitive
        assertTrue(cd.equals(cdNew));
        assertTrue(cdNew.equals(cdEvenNewer));
        assertTrue(cd.equals(cdEvenNewer));

        // null case
        assertTrue(new City(null).equals(new City("")));

        assertFalse(new City("A").equals(null));
        assertFalse(new City("A").equals("A"));
    }

    @Test
    public void testCompareTo() {
        assertTrue(new City("A").compareTo(new City("B")) < 0);
        assertTrue(new City("A").compareTo(new City("A")) == 0);
        assertTrue(new City("Z").compareTo(new City("A")) > 0);

        City[] cities = new City[] { new City("A"), new City("D"),
                new City("C") };

        assertArrayEquals(new City[] { new City("A"), new City("D"),
                new City("C") }, cities);

        Arrays.sort(cities);
        assertArrayEquals(new City[] { new City("A"), new City("C"),
                new City("D") }, cities);
    }
}
