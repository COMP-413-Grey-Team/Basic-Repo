package edu.rice.rbox.Location.interest;

import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public class NoInterestPredicate implements InterestPredicate {

    public NoInterestPredicate() {

    }

    @Override
    public Bson toMongoQuery(HashMap<String, Serializable> map) {
        return Filters.exists("field_that_should_never_be_used");
    }
}
