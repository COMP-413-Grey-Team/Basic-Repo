package edu.rice.rbox.Location.interest;

import com.mongodb.client.model.Filters;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Location.locator.Locator2Replication;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public class EqualityStringPredicate extends EqualityPredicate<String>  {

    public EqualityStringPredicate(String field, String value, boolean isRelative) {
        super(field, value, isRelative);
    }

    @Override
    public Bson toMongoQuery(GameObjectUUID relative_object_uuid, ObjectLocationStorageInterface storage) {
        String valueAsString = value;

        if (isRelative) {
//            String fieldValue = (String) storage.queryOneField(relative_object_uuid, this.field).get(fieldName);
//            valueAsString = value.concat(fieldValue);
        }

        return Filters.eq(this.field, valueAsString);
    }
}
