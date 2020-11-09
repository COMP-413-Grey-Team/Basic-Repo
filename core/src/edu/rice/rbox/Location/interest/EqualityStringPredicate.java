package edu.rice.rbox.Location.interest;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public class EqualityStringPredicate extends EqualityPredicate<String>  {

    public EqualityStringPredicate(String field, String value) {
        super(field, value);
    }

    @Override
    public Bson toMongoQuery(HashMap<String, Serializable> map) {
        return Filters.eq(fieldName, value);
    }
}
