package edu.rice.rbox.Location.interest;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public class EqualityStringPredicate extends EqualityPredicate<String>  {
    private final boolean isRelative;

    public EqualityStringPredicate(String field, String value, boolean isRelative) {
        super(field, value);
        this.isRelative = isRelative;
    }

    @Override
    public Bson toMongoQuery(HashMap<String, Serializable> map) {
        String valueAsString = value;

        if (isRelative) {
            String fieldValue = (String) map.get(fieldName);
            valueAsString = value.concat(fieldValue);
        }

        return Filters.eq(fieldName, valueAsString);
    }
}
