package edu.rice.rbox.Location;

import edu.rice.rbox.Common.GameField.GameFieldInteger;
import edu.rice.rbox.Common.GameField.GameFieldString;
import edu.rice.rbox.Common.GameField.InterestingGameField;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.ServerUUID;
import edu.rice.rbox.Location.interest.*;
import edu.rice.rbox.Location.locator.LocatorMainImpl;
import edu.rice.rbox.Replication.HolderInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class LocatorMainImplTest {
    private LocatorMainImpl impl;
    private ServerUUID test_server_uuid;
    private final String test_db_name = "TEST_DB";
    private final String test_collection_name = "TEST_FIELDS";


    @BeforeAll
    private void init() {
        this.test_server_uuid = ServerUUID.randomUUID();
        //TODO: testing DB and collection
        impl = new LocatorMainImpl(test_server_uuid,
                new DummyStorageToLocation(),
                new DummyReplicationToLocation(),
                test_db_name,
                test_collection_name);
        impl.removeAllFromCollection();
    }
    //W.I.P. -- will involve mongo...
    @Test
    public void testLocatorRoutine() {
        init();
        GameObjectUUID testObjectUUID = new GameObjectUUID(UUID.randomUUID());
        GameObjectUUID otherObjectUUID1 = new GameObjectUUID(UUID.randomUUID());
        GameObjectUUID otherObjectUUID2 = new GameObjectUUID(UUID.randomUUID());

        List<String> other_UUIDs = List.of(otherObjectUUID1.getUUID().toString(), otherObjectUUID2.getUUID().toString());

        //These initial values are hardcoded within the fake queryOneField(...) method in DummyStorageToLocation.java
        HashMap<String, InterestingGameField> test_obj = new HashMap<>(Map.of(
                "team", new GameFieldString("red"),
                "location", new GameFieldInteger(123)
        ));
        HashMap<String, InterestingGameField> other_obj1 = new HashMap<>(Map.of(
                "team", new GameFieldString("red"),
                "location", new GameFieldInteger(500)
        ));
        HashMap<String, InterestingGameField> other_obj2 = new HashMap<>(Map.of(
                "team", new GameFieldString("blue"),
                "location", new GameFieldInteger(129)
        ));

        InterestPredicate testObjectPredicate = new CompositePredicate(
                new EqualityStringPredicate("team", "", true), //Same team name.
                new RangePredicate<>("location", 10, 10, true), //Within 10 units
                PredicateBiOperator.OR
        );

        // Adding the testing object as well as other objects in mongo
        impl.add(testObjectUUID, testObjectPredicate, test_obj);
        impl.addForTest(otherObjectUUID1, other_obj1);
        impl.addForTest(otherObjectUUID2, other_obj2);

        // Check for initial query, should return both other objects...
        List<HolderInfo> results = impl.formulate_results();
        List<String> result_uuids = results.stream().map(
                info -> info.getGameObjectUUID().getUUID().toString()
        ).collect(Collectors.toList());

        //Both 'other_uuids' should be in the results, one matches on team, the other on location.
        System.out.println(result_uuids);

    }
}
