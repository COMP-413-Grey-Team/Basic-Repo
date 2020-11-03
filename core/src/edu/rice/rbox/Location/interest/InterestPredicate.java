package edu.rice.rbox.Location.interest;

import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public interface InterestPredicate {

    Bson toMongoQuery(HashMap<String, Serializable> map);
}
