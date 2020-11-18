package edu.rice.rbox.Location.interest;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Location.locator.Locator2Replication;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public interface InterestPredicate {
    Bson toMongoQuery(GameObjectUUID relative_object_uuid, ObjectLocationStorageInterface adapter);
}
