package edu.rice.rbox.Location.interest;

import com.mongodb.client.model.Filters;
import edu.rice.rbox.Common.GameObjectUUID;
import edu.rice.rbox.Location.locator.Locator2Replication;
import edu.rice.rbox.ObjStorage.ObjectLocationStorageInterface;
import org.bson.conversions.Bson;

import java.io.Serializable;
import java.util.HashMap;

public class RangePredicate<T extends Number> extends InterestPredicate {

    final Number upper;
    final Number lower;

    public RangePredicate(String field, T upper, T lower, boolean isRelative) {
        super(field, isRelative);
        this.upper = upper;
        this.lower = lower;

    }


    @Override
    public Bson toMongoQuery(GameObjectUUID relative_object_uuid, ObjectLocationStorageInterface storage) {
        double upperValue = upper.doubleValue();
        double lowerValue = lower.doubleValue();

        if (isRelative) {
//            T fieldValue = (T) storage.queryOneField(relative_object_uuid, this.field).get(fieldName);
//            lowerValue = fieldValue.doubleValue() + lowerValue;
//            upperValue = fieldValue.doubleValue() + upperValue;
        }
        return Filters.and(
                Filters.gte(this.field, lowerValue),
                Filters.lte(this.field, upperValue)
        );
    }
}

/*
class RangePredicate {

   ...

public String toMongoQuery(HashMap<String, Serializable> map) {
    T fieldValue = (T) map.get(fieldName));
    return and(gte(fieldName, fieldValue + lower), lte(fieldName, fieldValue + upper))
    ??? How does this work with a tree growth height ???
}

}

12345: {

    uuid: "12345"
    name: "bob"
    x_val: "10"
    y_val: "10"
    type: "person"
    relevant_fields: ["name", "x_val"]
    predicate: new CompositePredicate(
        new RangePredicate("x_val", 123, 50),
        new RangePredicate("growth", 10, 10000000),
        OR
    )


},
54321: {

    ...
    growth: "50"
    type: "tree"
    ...

}

private void sendPredicate(HashMap<String, Serializable> map = ^^^ ) {

    return predicate.toMongoQuery(map);

}

Game Server calls 'createObject(HashMap<String, Serializable)'

 */