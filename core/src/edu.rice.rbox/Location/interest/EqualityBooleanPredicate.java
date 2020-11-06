package edu.rice.rbox.Location.interest;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

/*
 * For 'isRelative', 'value' indicates whether or not to match current boolean value.
 */
public class EqualityBooleanPredicate extends EqualityPredicate<Boolean> {
    private final boolean isRelative;

    public EqualityBooleanPredicate(String field, Boolean value, Boolean isRelative) {
        super(field, value);
        this.isRelative = isRelative;
    }

    @Override
    public Bson toMongoQuery(HashMap<String, Serializable> map) {
        Boolean valueAsBoolean = value;

        if (isRelative) {
            Boolean fieldValue = (Boolean) map.get(fieldName);
            valueAsBoolean = value ? fieldValue : !fieldValue;
        }

        return Filters.eq(fieldName, valueAsBoolean);
    }
}
