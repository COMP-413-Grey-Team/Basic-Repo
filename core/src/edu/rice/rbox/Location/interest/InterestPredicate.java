package edu.rice.rbox.Location.interest;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;
import org.bson.conversions.Bson;

public interface InterestPredicate {
    Bson toMongoQuery(GameObjectUUID relative_object_uuid, ObjectLocationStorageInterface adapter);
}
