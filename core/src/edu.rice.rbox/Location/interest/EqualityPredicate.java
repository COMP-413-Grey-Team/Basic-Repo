package edu.rice.rbox.Location.interest;

import org.bson.conversions.Bson;

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
