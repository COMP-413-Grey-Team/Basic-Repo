package edu.rice.rbox.ObjStorage;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Common.InterestingGameField;
import edu.rice.rbox.Location.interest.InterestPredicate;

import java.util.HashMap;

public class DummyLocationManager implements ObjectStorageLocationInterface {
    @Override
    public void add(GameObjectUUID id, InterestPredicate predicate, HashMap<String, InterestingGameField> value) {

    }

    @Override
    public void update(GameObjectUUID id, String field, InterestingGameField value) {

    }

    @Override
    public void delete(GameObjectUUID id) {

    }

    @Override
    public void queryInterest() {

    }
}
