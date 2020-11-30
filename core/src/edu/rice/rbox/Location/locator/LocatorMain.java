package edu.rice.rbox.Location.locator;

import edu.rice.rbox.Common.GameField.GameField;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Location.interest.InterestPredicate;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.UUID;

// TODO: replace with gamefield
public interface LocatorMain {

    /* Adding the interesting fields for this object and assigning predicate. */
    void addObjectInterest(GameObjectUUID object_uuid, InterestPredicate predicate, HashMap<String, Object> fields);

    /* Updating interesting fields for this object. */
    void updateObjectInterest(GameObjectUUID object_uuid, String fieldName, Object value);

    /* Removing the interesting fields for this object */
    void removeObjectInterest(GameObjectUUID object_uuid);

    /* Query the interest of all primary objects */
    void queryInterest();

}
