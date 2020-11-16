package edu.rice.comp413fall2020grey.Location.locator.Tests;

import com.mongodb.client.model.Filters;
import edu.rice.comp413fall2020grey.Location.locator.LocatorMainImpl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class LocatorMainImplTests {


    private static LocatorMainImpl locatorMain = new LocatorMainImpl();
    private final UUID UUID_ONE = UUID.fromString("48804d18-22e5-11eb-adc1-0242ac120002");
    private final UUID UUID_TWO = UUID.randomUUID();

    @Test
    public void testUpdateOne() {

        // Create some random object to store in the document
        HashMap<String, Object> map = new HashMap<>();
        ArrayList object1 = new ArrayList();
        object1.add("abc");
        Integer object2 = 11;
        map.put("one", object1);
        map.put("two", object2);

        locatorMain.updatePrimary(UUID_ONE, map);
        locatorMain.queryInterest(UUID_ONE, Filters.gte("two", 11));
    }

    @Test
    public void testDelete() {
        locatorMain.removePrimary(UUID_ONE);
    }





}
