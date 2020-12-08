package edu.rice.rbox.Location.interest;

import com.mongodb.client.model.Filters;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public class NoInterestPredicate implements InterestPredicate {

    public NoInterestPredicate() {}

    @Override
    public Bson toMongoQuery(GameObjectUUID relative_object_uuid, ObjectLocationStorageInterface adapter) {
        return Filters.exists("field_that_should_never_be_used");
    }
}
