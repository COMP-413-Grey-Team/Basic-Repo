package edu.rice.rbox.Location.interest;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public class EqualityNumberPredicate<T extends Number> extends EqualityPredicate<T> {
    final private boolean isRelative;

    public EqualityNumberPredicate(String field, T value, boolean isRelative) {
        super(field, value);
        this.isRelative = isRelative;
    }

    @Override
    public Bson toMongoQuery(HashMap<String, Serializable> map) {
        double valueAsDouble = value.doubleValue();

        if (isRelative) {
            T fieldValue = (T) map.get(fieldName);
            valueAsDouble = fieldValue.doubleValue() + valueAsDouble;
        }

        return Filters.eq(fieldName, valueAsDouble);
    }
}
