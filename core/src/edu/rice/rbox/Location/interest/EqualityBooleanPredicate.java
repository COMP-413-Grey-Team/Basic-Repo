package edu.rice.rbox.Location.interest;

import com.mongodb.client.model.Filters;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;
import org.bson.conversions.Bson;

/*
 * For 'isRelative', 'value' indicates whether or not to match current boolean value.
 */
public class EqualityBooleanPredicate extends EqualityPredicate<Boolean> {

    public EqualityBooleanPredicate(String field, Boolean value, Boolean isRelative) {
        super(field, value, isRelative);
    }

    @Override //TODO: fill in adapter
    public Bson toMongoQuery(GameObjectUUID relative_object_uuid, ObjectLocationStorageInterface storage) {
        Boolean valueAsBoolean = value;

        if (this.isRelative) {
            Boolean fieldValue = (Boolean) storage.queryOneField(relative_object_uuid, this.field).getValue();
            valueAsBoolean = value ? fieldValue : !fieldValue;
        }

        return Filters.eq(this.field, valueAsBoolean);
    }
}
