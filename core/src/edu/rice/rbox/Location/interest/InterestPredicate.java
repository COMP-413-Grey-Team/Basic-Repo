package edu.rice.rbox.Location.interest;

import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Location.locator.Locator2Replication;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public abstract class InterestPredicate {
    final String field;
    final Boolean isRelative;

    public InterestPredicate(String field, Boolean isRelative) {
        this.field = field;
        this.isRelative = isRelative;
    }
    abstract Bson toMongoQuery(GameObjectUUID relative_object_uuid, ObjectLocationStorageInterface adapter);
}
