package edu.rice.rbox.Location.interest;

import com.mongodb.client.model.Filters;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Location.locator.Locator2Replication;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public class EqualityNumberPredicate<T extends Number> extends EqualityPredicate<T> {

    public EqualityNumberPredicate(String field, T value, boolean isRelative) {
        super(field, value, isRelative);
    }

    @Override
    public Bson toMongoQuery(GameObjectUUID relative_object_uuid, ObjectLocationStorageInterface storage) {
        double valueAsDouble = value.doubleValue();

//        if (isRelative) {
//            T fieldValue = (T) storage.queryOneField(relative_object_uuid, this.field).get(fieldName);
//            valueAsDouble = fieldValue.doubleValue() + valueAsDouble;
//        }

        return Filters.eq(this.field, valueAsDouble);
    }
}
