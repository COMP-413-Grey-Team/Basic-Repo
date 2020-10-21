package edu.rice.comp413fall2020grey.Location.interest;

import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public abstract class EqualityPredicate<T> implements InterestPredicate {
    final String fieldName;
    final T value;


    public EqualityPredicate(String field, T value) {
        this.fieldName = field;
        this.value = value;
    }

//    @Override
//    public String toMongoQuery() {
//        return String.format("eq(%s, %s)", fieldName, value);
//    }

}
